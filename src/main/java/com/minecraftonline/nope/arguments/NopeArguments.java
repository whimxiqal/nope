/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
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

package com.minecraftonline.nope.arguments;

import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

public class NopeArguments {

  /**
   * Creates a region command element, that returns a <b>{@link RegionWrapper}<\b>
   * @param key Key to use
   * @return CommandElement
   */
  public static CommandElement regionWrapper(Text key) {
    return new RegionCommandElement(key);
  }

  public static CommandElement flagValueWrapper(Text key) {
    return new FlagValueCommandElement(key);
  }

  public static CommandElement playerOrPlayerUUID(Text key) {
    return new PlayerOrPlayerUUID(key);
  }
}
