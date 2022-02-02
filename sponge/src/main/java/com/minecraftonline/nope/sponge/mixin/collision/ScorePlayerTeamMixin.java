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
//import java.util.Set;
//import net.minecraft.scoreboard.ScorePlayerTeam;
//import net.minecraft.scoreboard.Scoreboard;
//import net.minecraft.scoreboard.Team;
//import net.minecraft.util.text.TextFormatting;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//
///**
// * Score player team mixin. It's magic.
// */
//@Mixin(ScorePlayerTeam.class)
//public abstract class ScorePlayerTeamMixin implements ScorePlayerTeamBridge {
//
//  @Shadow
//  @Final
//  private Scoreboard scoreboard;
//  @Shadow
//  @Final
//  private Set<String> membershipSet;
//  @Shadow
//  private String displayName;
//  @Shadow
//  private String prefix;
//  @Shadow
//  private String suffix;
//  @Shadow
//  private boolean allowFriendlyFire;
//  @Shadow
//  private boolean canSeeFriendlyInvisibles;
//  @Shadow
//  private Team.EnumVisible nameTagVisibility;
//
//  @Shadow
//  private Team.EnumVisible deathMessageVisibility;
//
//  @Shadow
//  private TextFormatting color;
//
//  @Shadow
//  private Team.CollisionRule collisionRule;
//
//  @Shadow
//  public abstract boolean getSeeFriendlyInvisiblesEnabled();
//
//  /**
//   * A set of all team member usernames.
//   */
//  @Override
//  public void nope$fromWithNewCollisionRule(ScorePlayerTeam oldTeam, Team.CollisionRule rule) {
//    this.membershipSet.clear();
//    this.membershipSet.addAll(oldTeam.getMembershipCollection());
//    this.displayName = oldTeam.getDisplayName();
//    this.prefix = oldTeam.getPrefix();
//    this.suffix = oldTeam.getSuffix();
//    this.allowFriendlyFire = oldTeam.getAllowFriendlyFire();
//    this.canSeeFriendlyInvisibles = oldTeam.getSeeFriendlyInvisiblesEnabled();
//    this.nameTagVisibility = oldTeam.getNameTagVisibility();
//    this.deathMessageVisibility = oldTeam.getDeathMessageVisibility();
//    this.color = oldTeam.getColor();
//    this.collisionRule = rule;
//  }
//
//  @Override
//  public Scoreboard nope$getScoreboard() {
//    return this.scoreboard;
//  }
//
//  @Override
//  public void nope$setCollisionQuietly(Team.CollisionRule collisionRule) {
//    this.collisionRule = collisionRule;
//  }
//
//  @Override
//  public void nope$setSeeFriendlyInvisiblesQuietly(boolean canSeeFriendlyInvisibles) {
//    this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
//  }
//}
