/*
 * MIT License
 *
 * Copyright (c) 2021 MinecraftOnline
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
 *
 */

package com.minecraftonline.nope.setting;

import com.google.gson.JsonElement;

/**
 * A setting that stores a positive integer as a value.
 */
public class PositiveIntegerSettingKey extends SettingKey<Integer> {
  public PositiveIntegerSettingKey(String id, Integer defaultValue) {
    super(id, defaultValue);
  }

  @Override
  public Integer dataFromJsonGenerified(JsonElement json) throws ParseSettingException {
    int integer = json.getAsInt();
    if (integer < 0) {
      throw new ParseSettingException("Data must be a positive integer");
    }
    return integer;
  }

  @Override
  public Integer parse(String data) throws ParseSettingException {
    int integer;
    try {
      integer = Integer.parseInt(data);
    } catch (NumberFormatException e) {
      throw new ParseSettingException("Data must be an integer");
    }
    if (integer < 0) {
      throw new ParseSettingException("Data must be a positive integer");
    }
    return integer;
  }
}
