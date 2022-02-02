/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

//package com.minecraftonline.nope.sponge.mixin.collision;
//
//import net.minecraft.scoreboard.ScorePlayerTeam;
//import net.minecraft.scoreboard.Scoreboard;
//import net.minecraft.scoreboard.Team;
//
///**
// * Magic class that manages player teams for the purpose of
// * managing collisions.
// */
//public interface ScorePlayerTeamBridge {
//  /**
//   * Sets all the data, apart from CollisionRule from the oldTeam.
//   * (Things that must be set in constructor will not be changed).
//   *
//   * @param oldTeam Team to copy data from
//   * @param rule    Rule to set this collision to.
//   */
//  @SuppressWarnings("checkstyle:MethodName")
//  void nope$fromWithNewCollisionRule(ScorePlayerTeam oldTeam, Team.CollisionRule rule);
//
//  @SuppressWarnings("checkstyle:MethodName")
//  Scoreboard nope$getScoreboard();
//
//  /**
//   * Sets the collision rule <b>without</b>
//   * broadcasting the change to the server.
//   *
//   * @param collisionRule CollisionRule to set
//   */
//  @SuppressWarnings("checkstyle:MethodName")
//  void nope$setCollisionQuietly(Team.CollisionRule collisionRule);
//
//  @SuppressWarnings("checkstyle:MethodName")
//  void nope$setSeeFriendlyInvisiblesQuietly(boolean canSeeFriendlyInvisibles);
//}
