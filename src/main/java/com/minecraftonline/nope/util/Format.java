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
 *
 */

package com.minecraftonline.nope.util;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public final class Format {

  private Format() {
  }

  public static final TextColor THEME = TextColors.GRAY;

  public static Text prefix() {
    return Text.of(THEME, "Nope ", TextColors.DARK_GRAY, "-=- ");
  }

  public static Text error(Object... message) {
    return Text.of(prefix(), TextColors.RED, Text.of(message));
  }

  public static Text warn(Object... message) {
    return Text.of(prefix(), TextColors.YELLOW, Text.of(message));
  }

  public static Text info(Object... message) {
    return Text.of(prefix(), TextColors.WHITE, Text.of(message));
  }

  public static Text regionInfo(String key, String value) { return Text.of(TextColors.GREEN, key, TextColors.GRAY, value);}

}
