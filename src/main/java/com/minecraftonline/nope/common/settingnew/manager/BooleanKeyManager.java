/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Pieter Svenson
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

package com.minecraftonline.nope.common.settingnew.manager;

import com.google.common.collect.Lists;
import com.minecraftonline.nope.common.settingnew.SettingKey;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class BooleanKeyManager extends SettingKey.Manager<Boolean> {

  private static final List<String> booleanOptions = Lists.newArrayList("true", "false", "t", "f");
  private static final List<String> stateOptions = Lists.newArrayList("allow", "deny");

  /**
   * Whether to give terminology as in "allow" or "deny"
   * versus "true" or "false".
   */
  private final boolean useStates;

  public BooleanKeyManager() {
    this.useStates = false;
  }

  public BooleanKeyManager(boolean useStates) {
    this.useStates = useStates;
  }

  @Override
  public Class<Boolean> type() throws SettingKey.ParseSettingException {
    return Boolean.class;
  }

  @Override
  public Boolean parse(String data) throws SettingKey.ParseSettingException {
    switch (data.toLowerCase()) {
      case "true":
      case "t":
      case "allow":
        return true;
      case "false":
      case "f":
      case "deny":
        return false;
      default:
        throw new SettingKey.ParseSettingException("Allowed values: " + String.join(", ", options()));
    }
  }

  @Override
  @NotNull
  public List<String> options() {
    if (useStates) {
      return stateOptions;
    } else {
      return booleanOptions;
    }
  }

  @Override
  public @NotNull String print(@NotNull Boolean data) {
    if (useStates) {
      return data ? "allow" : "deny";
    } else {
      return String.valueOf(data);
    }
  }
}
