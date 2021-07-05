package com.minecraftonline.nope.bridge.collision;

import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;

/**
 * Magic class that manages player teams for the purpose of
 * managing collisions.
 */
public interface ScorePlayerTeamBridge {
  /**
   * Sets all the data, apart from CollisionRule from the oldTeam.
   * (Things that must be set in constructor will not be changed).
   *
   * @param oldTeam Team to copy data from
   * @param rule    Rule to set this collision to.
   */
  @SuppressWarnings("checkstyle:MethodName")
  void nope$fromWithNewCollisionRule(ScorePlayerTeam oldTeam, Team.CollisionRule rule);

  @SuppressWarnings("checkstyle:MethodName")
  Scoreboard nope$getScoreboard();

  /**
   * Sets the collision rule <b>without</b>
   * broadcasting the change to the server.
   *
   * @param collisionRule CollisionRule to set
   */
  @SuppressWarnings("checkstyle:MethodName")
  void nope$setCollisionQuietly(Team.CollisionRule collisionRule);

  @SuppressWarnings("checkstyle:MethodName")
  void nope$setSeeFriendlyInvisiblesQuietly(boolean canSeeFriendlyInvisibles);
}
