package com.minecraftonline.nope.util;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.bridge.collision.CollisionHandler;
import com.minecraftonline.nope.bridge.collision.ScorePlayerTeamBridge;
import com.minecraftonline.nope.bridge.collision.ServerScoreboardBridge;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Various Utilities for horrible icky nms.
 * Recommendation: do not touch
 */
public class CollisionUtil {

  public static final int CREATE_SCOREBOARD = 0;
  public static final int REMOVE_TEAM = 1;
  public static final int UPDATE_SCOREBOARD = 2;
  public static final int ADD_PLAYER_TO_SCOREBOARD = 3;
  public static final int REMOVE_PLAYER_FROM_TEAM = 4;

  /**
   * Make a score player team without collision.
   *
   * @param scorePlayerTeam score player team
   * @return score player team with no collision
   */
  public static ScorePlayerTeam makeWithNoCollision(ScorePlayerTeam scorePlayerTeam) {
    ScorePlayerTeam team = new ScorePlayerTeam(
        ((ScorePlayerTeamBridge) scorePlayerTeam).nope$getScoreboard(),
        scorePlayerTeam.getName());
    ((ScorePlayerTeamBridge) team).nope$fromWithNewCollisionRule(scorePlayerTeam,
        Team.CollisionRule.NEVER);
    return team;
  }

  /**
   * Adds players from the given list who should not have collision,
   * and adds them to a dummy scoreboard that stops their collision.
   *
   * @param team    Dummy team with no collision
   * @param players Players of which only disabled collision players
   *                get added to the dummy scoreboard
   */
  public static void addDisabledCollisionPlayersToTeam(ScorePlayerTeam team,
                                                       List<EntityPlayerMP> players) {
    // Tell client about this new fancy collision disabling scoreboard
    SPacketTeams disabledCollisionTeamCreatePacket = new SPacketTeams(team, CREATE_SCOREBOARD);

    CollisionHandler collisionHandler = Nope.getInstance().getCollisionHandler();
    for (EntityPlayerMP entityPlayerMp : players) {
      if (!collisionHandler.isCollisionDisabled((Player) entityPlayerMp)) {
        continue;
      }
      // Create team (for this player only)
      entityPlayerMp.connection.sendPacket(disabledCollisionTeamCreatePacket);

      // Tell client that they just got added to this totally real team
      SPacketTeams addPlayerPacket = new SPacketTeams(team,
          Collections.singletonList(entityPlayerMp.getName()),
          CollisionUtil.ADD_PLAYER_TO_SCOREBOARD);
      entityPlayerMp.connection.sendPacket(addPlayerPacket);
    }
  }

  /**
   * Update the team which handles disabled collisions for players.
   *
   * @param currentTeam the current score player team
   * @param players     the list of players
   */
  public static void updateDisabledCollisionPlayers(ScorePlayerTeam currentTeam,
                                                    List<EntityPlayerMP> players) {
    if (currentTeam.getCollisionRule() != Team.CollisionRule.ALWAYS) {
      return; // Scoreboard has modified collision rules, do not change anything.
    }
    CollisionHandler collisionHandler = Nope.getInstance().getCollisionHandler();

    ScorePlayerTeam newDummyTeam = CollisionUtil.makeWithNoCollision(currentTeam);
    SPacketTeams disabledCollisionPacket = new SPacketTeams(newDummyTeam, UPDATE_SCOREBOARD);

    for (EntityPlayerMP entityPlayerMp : players) {
      if (collisionHandler.isCollisionDisabled((Player) entityPlayerMp)) {
        entityPlayerMp.connection.sendPacket(disabledCollisionPacket);
      }
    }
  }

  /**
   * Removes the given players from the dummy team,
   * <b>regardless of whether they have collision enabled</b>
   * Can be used to cleanup when a player's collision doesn't
   * need to be disabled any more, or when a player is about to
   * be added to a team, and needs to lose this team, and then
   * get their new one with collision disabled.
   *
   * @param dummyNoCollisionTeam The dummy collision
   * @param players              Players to remove the dummy collision scoreboard from.
   */
  public static void removeDummyTeam(ScorePlayerTeam dummyNoCollisionTeam,
                                     List<EntityPlayerMP> players) {
    SPacketTeams packet = new SPacketTeams(dummyNoCollisionTeam, REMOVE_TEAM);
    for (EntityPlayerMP entityPlayerMp : players) {
      entityPlayerMp.connection.sendPacket(packet);
    }
  }

  /**
   * Disable collision for a player.
   *
   * @param player the player
   */
  public static void disableCollision(Player player) {
    EntityPlayerMP entityPlayerMp = (EntityPlayerMP) player;
    Team team = entityPlayerMp.getTeam();
    if (team == null) {
      ServerScoreboardBridge scoreboardBridge = (ServerScoreboardBridge) Sponge.getServer()
          .getServerScoreboard()
          .get();

      addDisabledCollisionPlayersToTeam(scoreboardBridge.nope$getDummyNoCollisionTeam(),
          Collections.singletonList(entityPlayerMp));
    } else {
      updateDisabledCollisionPlayers((ScorePlayerTeam) team,
          Collections.singletonList(entityPlayerMp));
    }
  }

  /**
   * Enable collision for a player.
   *
   * @param player the player
   */
  public static void enableCollision(Player player) {
    EntityPlayerMP entityPlayerMp = (EntityPlayerMP) player;
    Team team = entityPlayerMp.getTeam();
    if (team == null) {
      // Remove the dummy team
      ServerScoreboardBridge scoreboardBridge = (ServerScoreboardBridge) Sponge.getServer()
          .getServerScoreboard()
          .get();
      removeDummyTeam(scoreboardBridge.nope$getDummyNoCollisionTeam(),
          Collections.singletonList(entityPlayerMp));
    } else {
      // Send them the real team.
      SPacketTeams packet = new SPacketTeams((ScorePlayerTeam) team, UPDATE_SCOREBOARD);
      entityPlayerMp.connection.sendPacket(packet);
    }
  }

  /**
   * Convert player names into minecraft {@link EntityPlayerMP}s.
   *
   * @param usernames player usernames
   * @return a list of players
   */
  public static List<EntityPlayerMP> convertPlayers(Collection<String> usernames) {
    return usernames.stream()
        .map(name -> Sponge.getServer().getPlayer(name).orElse(null))
        .filter(Objects::nonNull)
        .map(player -> (EntityPlayerMP) player)
        .collect(Collectors.toList());
  }
}
