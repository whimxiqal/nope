package com.minecraftonline.nope.mixin.collision;

import com.minecraftonline.nope.CollisionHandler;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.bridge.collision.ScorePlayerTeamBridge;
import com.minecraftonline.nope.bridge.collision.ServerScoreboardBridge;
import com.minecraftonline.nope.util.CollisionUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.Sponge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collections;
import java.util.List;

/**
 * This mixin uses injects and a redirect to modify
 * scoreboard team packets sent to the client, that
 * have collision disabled, according to the
 * {@link CollisionHandler}. If their collision is
 * not disabled, nothing should differ from normal.
 */
@Mixin(ServerScoreboard.class)
public class ServerScoreboardCollisionMixin implements ServerScoreboardBridge {

  public final ScorePlayerTeam nope$dummyNoCollisionTeam = new ScorePlayerTeam((Scoreboard)(Object) this, "fakeColTeam"); // MUST not be longer than 16 in length!!

  @Inject(method = "<init>", at = @At("RETURN"))
  public void onInit(MinecraftServer mcServer, CallbackInfo ci) {
    ScorePlayerTeamBridge bridge = (ScorePlayerTeamBridge)nope$dummyNoCollisionTeam;
    bridge.nope$setCollisionQuietly(Team.CollisionRule.NEVER);
    bridge.nope$setSeeFriendlyInvisiblesQuietly(false);
  }

  /* Conflicts with sponges implementation
  /**
   * Redirects when a scoreboard sends updates about the new state
   * of the scoreboard. Check if any of the players about to receive
   * an update have collision disabled, if so, send them a packet
   * with the right information apart from the collision info, but
   * if the collision info is not the default, don't do anything.
   * @param playerList Players that are
   * @param packetIn Original update packet
   * @param playerTeam Original team
   */
  /*@Redirect(method = "broadcastTeamInfoUpdate(Lnet/minecraft/scoreboard/ScorePlayerTeam;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerList;sendPacketToAllPlayers(Lnet/minecraft/network/Packet;)V"))
  public void onScoreboardTeamUpdate(PlayerList playerList, Packet<?> packetIn, ScorePlayerTeam playerTeam) {
    List<EntityPlayerMP> players = CollisionUtil.convertPlayers(playerTeam.getMembershipCollection());
    CollisionUtil.updateDisabledCollisionPlayers(playerTeam, packetIn, players);

  }*/

  /**
   * Injects just after a team has been updated, to re-update
   * them with collision info (except if the team already has
   * modified collision). We can't redirect because it conflicts
   * with a SpongeCommon redirect. This should do just fine
   * however.
   */
  @Inject(method = "broadcastTeamInfoUpdate(Lnet/minecraft/scoreboard/ScorePlayerTeam;)V",
      at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/management/PlayerList;sendPacketToAllPlayers(Lnet/minecraft/network/Packet;)V"))
  public void afterScoreboardTeamUpdate(ScorePlayerTeam playerTeam, CallbackInfo ci) {
    List<EntityPlayerMP> players = CollisionUtil.convertPlayers(playerTeam.getMembershipCollection());
    CollisionUtil.updateDisabledCollisionPlayers(playerTeam, players);
  }

  /**
   * Injects just before a player is added to a team, so that
   * when they get added to the new team, they aren't in a
   * team already, which is likely to cause lots of issues.
   *
   * We inject after this to update the new team with disabled
   * collision.
   *
   * @param player name of the player being added
   * @param newTeam Team they be added to
   * @param cir callback info (Unused)
   */
  @Inject(method = "addPlayerToTeam(Ljava/lang/String;Ljava/lang/String;)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerList;sendPacketToAllPlayers(Lnet/minecraft/network/Packet;)V"))
  public void beforeScoreboardPlayerAdd(String player, String newTeam, CallbackInfoReturnable<Boolean> cir) {
    CollisionHandler collisionHandler = Nope.getInstance().getCollisionHandler();

    // Remove dummy team so the client doesn't get confused when its added to a team when its already in our dummy one.
    Sponge.getServer().getPlayer(player).filter(collisionHandler::isCollisionDisabled)
        .ifPresent(p -> CollisionUtil.removeDummyTeam(this.nope$dummyNoCollisionTeam, Collections.singletonList((EntityPlayerMP)p)));
  }

  /**
   * Injects just after a player is added to a team, so that
   * if their collision should be disabled, we can send an
   * update packet with fake collision information to
   * disable it.
   *
   * @param player Player being added
   * @param newTeam name of the Team they are being added to (Unused)
   * @param cir unused callback info
   * @param scoreplayerteam Team they are being added to
   */
  @Inject(method = "addPlayerToTeam(Ljava/lang/String;Ljava/lang/String;)Z",
      locals = LocalCapture.CAPTURE_FAILHARD,
      at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/management/PlayerList;sendPacketToAllPlayers(Lnet/minecraft/network/Packet;)V"))
  public void onScoreboardPlayerAdd(String player, String newTeam, CallbackInfoReturnable<Boolean> cir, ScorePlayerTeam scoreplayerteam) {
    List<EntityPlayerMP> playerMP = CollisionUtil.convertPlayers(Collections.singleton(player));
    CollisionUtil.updateDisabledCollisionPlayers(scoreplayerteam, playerMP);
  }

  /**
   * Inject just after a player is removed from a team
   * to handle when a team gets "disbanded",
   * which is the same to us, as all the players
   * getting removed from it, thus it follows similar logic
   * as {@link #onScoreboardTeamRemovePlayer(String, ScorePlayerTeam, CallbackInfo)}
   * but with more than one player.
   *
   * Any player in this will have no team, so if they need collision
   * disabling, we give them a dummy team with no collision.
   *
   * @param playerTeam Team that is being disbanded
   * @param ci Callback info (unused)
   */
  @Inject(method = "broadcastTeamRemove(Lnet/minecraft/scoreboard/ScorePlayerTeam;)V",
      at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/management/PlayerList;sendPacketToAllPlayers(Lnet/minecraft/network/Packet;)V"))
  public void onScoreboardTeamRemove(ScorePlayerTeam playerTeam, CallbackInfo ci) {
    List<EntityPlayerMP> players = CollisionUtil.convertPlayers(playerTeam.getMembershipCollection());

    CollisionUtil.addDisabledCollisionPlayersToTeam(this.nope$dummyNoCollisionTeam, players);
  }

  /**
   * Handle when a username is removed from a team
   *
   * See {@link #onScoreboardTeamRemove(ScorePlayerTeam, CallbackInfo)} for more
   * details.
   *
   * @param username username that is being removed
   * @param playerTeam Team its being removed from
   * @param ci callback info (Unused)
   */
  @Inject(method = "removePlayerFromTeam(Ljava/lang/String;Lnet/minecraft/scoreboard/ScorePlayerTeam;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerList;sendPacketToAllPlayers(Lnet/minecraft/network/Packet;)V"))
  public void onScoreboardTeamRemovePlayer(String username, ScorePlayerTeam playerTeam, CallbackInfo ci) {
    List<EntityPlayerMP> player = CollisionUtil.convertPlayers(Collections.singleton(username));

    CollisionUtil.addDisabledCollisionPlayersToTeam(this.nope$dummyNoCollisionTeam, player);
  }

  @Override
  public ScorePlayerTeam nope$getDummyNoCollisionTeam() {
    return this.nope$dummyNoCollisionTeam;
  }
}
