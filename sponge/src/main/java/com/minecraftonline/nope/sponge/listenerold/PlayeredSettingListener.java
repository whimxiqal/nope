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

//package com.minecraftonline.nope.sponge.listenerold;
//
//import com.minecraftonline.nope.common.setting.SettingKey;
//import org.jetbrains.annotations.NotNull;
//import org.spongepowered.api.entity.living.player.Player;
//import org.spongepowered.api.event.Event;
//
///**
// * An accessibility class for handling events where a player is
// * the root cause.
// *
// * @param <E> the type of event for which to listen
// */
//public abstract class PlayeredSettingListener<E extends Event> extends SettingListener<E> {
//  public PlayeredSettingListener(@NotNull Class<E> eventClass,
//                                 @NotNull SettingKey<?>... keys) {
//    super(eventClass, keys);
//  }
//
//
//  @Override
//  public void handle(E event) {
//    if (!(event.cause().root() instanceof Player)) {
//      return;
//    }
//    handle(event, (Player) event.cause().root());
//  }
//
//  public abstract void handle(E event, Player player);
//
//}
