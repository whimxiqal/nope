package com.minecraftonline.nope.bridge.collision;

import net.minecraft.scoreboard.ScorePlayerTeam;

/**
 * Scoreboard bridge.
 */
public interface ServerScoreboardBridge {
  @SuppressWarnings("checkstyle:MethodName")
  ScorePlayerTeam nope$getDummyNoCollisionTeam();
}
