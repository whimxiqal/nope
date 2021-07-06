package com.minecraftonline.nope.mixin.collision;

import com.minecraftonline.nope.bridge.collision.ScorePlayerTeamBridge;
import java.util.Set;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Score player team mixin. It's magic.
 */
@Mixin(ScorePlayerTeam.class)
public abstract class ScorePlayerTeamMixin implements ScorePlayerTeamBridge {

  @Shadow
  @Final
  private Scoreboard scoreboard;
  @Shadow
  @Final
  private Set<String> membershipSet;
  @Shadow
  private String displayName;
  @Shadow
  private String prefix;
  @Shadow
  private String suffix;
  @Shadow
  private boolean allowFriendlyFire;
  @Shadow
  private boolean canSeeFriendlyInvisibles;
  @Shadow
  private Team.EnumVisible nameTagVisibility;

  @Shadow
  private Team.EnumVisible deathMessageVisibility;

  @Shadow
  private TextFormatting color;

  @Shadow
  private Team.CollisionRule collisionRule;

  @Shadow
  public abstract boolean getSeeFriendlyInvisiblesEnabled();

  /**
   * A set of all team member usernames.
   */
  @Override
  public void nope$fromWithNewCollisionRule(ScorePlayerTeam oldTeam, Team.CollisionRule rule) {
    this.membershipSet.clear();
    this.membershipSet.addAll(oldTeam.getMembershipCollection());
    this.displayName = oldTeam.getDisplayName();
    this.prefix = oldTeam.getPrefix();
    this.suffix = oldTeam.getSuffix();
    this.allowFriendlyFire = oldTeam.getAllowFriendlyFire();
    this.canSeeFriendlyInvisibles = oldTeam.getSeeFriendlyInvisiblesEnabled();
    this.nameTagVisibility = oldTeam.getNameTagVisibility();
    this.deathMessageVisibility = oldTeam.getDeathMessageVisibility();
    this.color = oldTeam.getColor();
    this.collisionRule = rule;
  }

  @Override
  public Scoreboard nope$getScoreboard() {
    return this.scoreboard;
  }

  @Override
  public void nope$setCollisionQuietly(Team.CollisionRule collisionRule) {
    this.collisionRule = collisionRule;
  }

  @Override
  public void nope$setSeeFriendlyInvisiblesQuietly(boolean canSeeFriendlyInvisibles) {
    this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
  }
}
