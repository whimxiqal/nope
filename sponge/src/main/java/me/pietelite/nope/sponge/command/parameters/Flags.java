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

package me.pietelite.nope.sponge.command.parameters;

import org.spongepowered.api.command.parameter.managed.Flag;

/**
 * Stores {@link Flag}s for commands.
 */
public class Flags {

  public static final Flag ADDITIVE_VALUE_FLAG = Flag.of("additive");
  public static final Flag OPEN_EDITOR = Flag.of("editor", "e");
  public static final Flag PLAYER = Flag.builder()
      .aliases("player")
      .setParameter(Parameters.PLAYER)
      .build();
  public static final Flag PRIORITY = Flag.builder()
      .aliases("p", "priority")
      .setParameter(Parameters.PRIORITY)
      .build();
  public static final Flag SETTING_KEY = Flag.builder()
      .aliases("setting")
      .setParameter(Parameters.SETTING_KEY)
      .build();
  public static final Flag SUBTRACTIVE_VALUE_FLAG = Flag.of("subtractive");
  public static final Flag TARGET = Flag.builder()
      .aliases("target")
      .setParameter(Parameters.STRING)
      .build();

}
