/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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

package me.pietelite.nope.sponge.api.event;

import org.spongepowered.api.world.server.ServerLocation;

/**
 * A group of functions to assist in evaluating what value is under a certain
 * pre-specified setting key has.
 *
 * @param <T> the type of value to request
 */
public interface SettingValueLookupFunction<T> {

  T lookup(ServerLocation location);

  /**
   * Look up the requested value using the cause of an event and a specific location.
   * If the cause is found to be a Sponge ServerPlayer,
   * the setting system will use this player's permission as part of the evaluation process
   * for the resulting value. The location is just the location where you want the setting evaluated.
   *
   * @param cause    the cause of the event
   * @param location the location of interest
   * @return the requested value
   */
  T lookup(Object cause, ServerLocation location);

}
