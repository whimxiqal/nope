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

package com.minecraftonline.nope.control.flags;

import org.spongepowered.api.util.TypeTokens;

public class FlagState extends Flag<Boolean> {
  public FlagState(Boolean value) {
    super(value, TypeTokens.BOOLEAN_TOKEN);
  }

  public FlagState(Boolean value, TargetGroup group) {
    super(value, TypeTokens.BOOLEAN_TOKEN, group);
  }

  @Override
  public boolean shouldUseSerializeForConfigurate() {
    return true;
  }

  @Override
  public String serialize(Flag<Boolean> flag) {
    if (flag.getValue() == null) {
      throw new IllegalStateException("Flag<Boolean> with a null boolean!");
    }
    return flag.getValue() ? "allow" : "deny";
  }

  @Override
  public Boolean deserialize(String s) {
    if (s.equals("allow")) {
      return true;
    }
    if (s.equals("deny")) {
      return false;
    }
    throw new IllegalStateException("Tried to deserialize invalid Flag string!");
  }

  @Override
  public Boolean deserializeIngame(String s) {
    try {
      return deserialize(s);
    } catch (IllegalStateException e) {
      return null;
    }
  }
}
