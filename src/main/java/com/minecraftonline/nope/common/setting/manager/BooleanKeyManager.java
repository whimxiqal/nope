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

package com.minecraftonline.nope.common.setting.manager;

import com.google.common.collect.ImmutableMap;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingValue;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class BooleanKeyManager extends SettingKey.Manager.Unary<Boolean> {

  private final Map<String, Object> options;

  /**
   * Whether to give terminology as in "allow" or "deny"
   * versus "true" or "false".
   */
  private final boolean useStates;

  public BooleanKeyManager() {
    this(false);
  }

  public BooleanKeyManager(boolean useStates) {
    this.useStates = useStates;
    Map<String, String> options = new HashMap<>();
    if (useStates) {
      options.put("allow", "Permit users to perform certain behavior");
      options.put("deny", "Prohibit users from performing certain behaviors");
    } else {
      options.put("t", "Enable behavior");
      options.put("true", "Enable behavior");
      options.put("f", "Disable behavior");
      options.put("false", "Disable behavior");
    }
    this.options = ImmutableMap.copyOf(options);
  }

  @Override
  public Class<Boolean> dataType() throws SettingKey.ParseSettingException {
    return Boolean.class;
  }

  @Override
  public Boolean parseData(String data) throws SettingKey.ParseSettingException {
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
        throw new SettingKey.ParseSettingException("Allowed values: "
            + String.join(", ", elementOptions().keySet()));
    }
  }

  @Override
  public @NotNull Map<String, Object> elementOptions() {
    return options;
  }

  @Override
  public @NotNull String printData(@NotNull Boolean data) {
    if (useStates) {
      return data ? "allow" : "deny";
    } else {
      return String.valueOf(data);
    }
  }
}
