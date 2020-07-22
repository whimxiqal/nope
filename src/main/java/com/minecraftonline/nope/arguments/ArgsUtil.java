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

import com.google.common.collect.ImmutableSet;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.flags.Flag;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ArgsUtil {
  private static ImmutableSet<Setting<Flag<?>>> flagSettings = null;

  @SuppressWarnings("unchecked")
  public static ImmutableSet<Setting<Flag<?>>> getFlagSettings() {
    if (flagSettings != null) {
      return flagSettings;
    }
    ImmutableSet.Builder<Setting<Flag<?>>> builder = ImmutableSet.builder();
    Settings.REGISTRY_MODULE.getByApplicability(Setting.Applicability.REGION).stream()
        .filter(setting -> setting.getDefaultValue() instanceof Flag<?>)
        .map(setting -> (Setting<Flag<?>>)setting)
        .forEach(builder::add);
    flagSettings = builder.build();
    return flagSettings;
  }

  /**
   * Filters possibilities by beginning match
   * @param typed String that was typed
   * @param choices possible completions
   * @return Possible completions, or null if its already a match
   */
  @Nullable
  public static List<String> filterPossibilities(String typed, Collection<String> choices) {
    List<String> result = new ArrayList<>();
    for (String s : choices) {
      if (typed.equals(s)) {
        return null;
      }
      else if (s.startsWith(typed)) {
        result.add(s);
      }
    }
    return result;
  }
}
