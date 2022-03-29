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

package me.pietelite.nope.common.setting.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import me.pietelite.nope.common.setting.SettingKey;
import org.jetbrains.annotations.NotNull;

/**
 * A manager for a {@link SettingKey.Unary} holding a {@link Boolean}.
 */
public class BooleanKeyManager extends SettingKey.Manager.Unary<Boolean> {

  protected final Map<String, Object> options;
  private final Map<String, Object> suggestions;

  /**
   * Default constructor.
   */
  public BooleanKeyManager() {
    this.options = new HashMap<>();
    options.put("t", "Enable behavior");
    options.put("true", "Enable behavior");
    options.put("allow", "Permit behavior");
    options.put("on", "Enable behavior");
    options.put("enable", "Enable behavior");
    options.put("f", "Disable behavior");
    options.put("false", "Disable behavior");
    options.put("deny", "Prohibit behavior");
    options.put("off", "Disable behavior");
    options.put("disable", "Disable behavior");

    this.suggestions = this.options.entrySet().stream()
        .filter(entry -> entry.getKey().equals("true") || entry.getKey().equals("false"))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
      case "on":
        return true;
      case "false":
      case "f":
      case "deny":
      case "off":
        return false;
      default:
        throw new SettingKey.ParseSettingException("Allowed values: "
            + String.join(", ", elementOptions().keySet()));
    }
  }

  @Override
  public @NotNull Map<String, Object> elementOptions() {
    return this.options;
  }

  @Override
  public @NotNull Map<String, Object> elementSuggestions() {
    return this.suggestions;
  }

  @Override
  public Boolean createAlternate(Boolean data) {
    return !data;
  }

  @Override
  public @NotNull String printData(@NotNull Boolean data) {
    return String.valueOf(data);
  }
}
