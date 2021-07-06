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

package com.minecraftonline.nope.common.setting;

import java.util.Map;
import java.util.Objects;

/**
 * A class to manage all configurable components of the entire plugin.
 * This includes global configurations and, arguably more importantly,
 * the settings involving custom defined volumes (Zones) in a World.
 *
 * @param <T> the type of value stored
 */
public class Setting<T> implements Map.Entry<SettingKey<T>, SettingValue<T>> {

  private final SettingKey<T> key;
  private SettingValue<T> value;

  /**
   * Basic static factory. The value is set to null.
   *
   * @param key the key object
   * @param <X> the type of raw data stored
   * @return the setting
   */
  public static <X> Setting<X> of(SettingKey<X> key) {
    return new Setting<>(key, null);
  }

  /**
   * Full static factory.
   *
   * @param key the key object
   * @param val the value object which stores the raw data
   * @param <X> the type of raw data stored
   * @return the setting
   */
  public static <X> Setting<X> of(SettingKey<X> key, SettingValue<X> val) {
    return new Setting<>(key, val);
  }

  private Setting(SettingKey<T> key, SettingValue<T> value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public SettingKey<T> getKey() {
    return key;
  }

  @Override
  public SettingValue<T> getValue() {
    return value;
  }

  @Override
  public SettingValue<T> setValue(SettingValue<T> settingValue) {
    SettingValue<T> previous = value;
    this.value = settingValue;
    return previous;
  }

  @Override
  public int hashCode() {
    return Objects.hash(key.getId(), value.getData());
  }

  @Override
  public boolean equals(Object other) {
    return (other instanceof Setting)
        && ((Setting<?>) other).key.getId().equals(this.key.getId())
        && ((Setting<?>) other).value.getData().equals(this.value.getData());
  }
}
