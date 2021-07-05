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

package com.minecraftonline.nope.setting;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * A data structure to quickly retrieve {@link SettingValue}s based on
 * the static {@link SettingKey} associated with it.
 * These two types are combined into a {@link Setting} for ease of
 * retrieval and manipulation.
 */
public class SettingMap implements Map<SettingKey<?>, SettingValue<?>> {

  private final Map<SettingKey<?>, SettingValue<?>> data = Maps.newHashMap();

  @Override
  public int size() {
    return data.size();
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  @Override
  public boolean containsKey(Object o) {
    return data.containsKey(o);
  }

  @Override
  public boolean containsValue(Object o) {
    return data.containsValue(o);
  }

  @Deprecated
  @Override
  public SettingValue<?> get(Object o) {
    try {
      return this.get((SettingKey<?>) o);
    } catch (ClassCastException e) {
      throw new IllegalArgumentException("The input must be of type SettingKey");
    }
  }

  /**
   * Type specific version of {@link #get(Object)}
   * from default Java Map interface.
   *
   * @param key key to access value
   * @param <T> the type of setting
   * @return the stored value
   */
  @SuppressWarnings("unchecked")
  public <T> SettingValue<T> get(SettingKey<T> key) {
    SettingValue<?> val = data.get(key);
    try {
      return (SettingValue<T>) val;
    } catch (ClassCastException e) {
      throw mismatchedState(key, val);
    }
  }

  @Deprecated
  @Override
  public SettingValue<?> put(SettingKey<?> key, SettingValue<?> val) {
    if (!key.valueType().equals(val.getData().getClass())) {
      throw illegalArguments(key, val);
    }
    return data.put(key, val);
  }

  /**
   * Type specific version of {@link #put(SettingKey, SettingValue)}
   * from default Java Map interface.
   *
   * @param setting setting with the data required to set the value
   * @param <T>     the type of setting
   * @return the stored value
   */
  @SuppressWarnings("unchecked")
  public <T> SettingValue<T> put(Setting<T> setting) {
    try {
      return (SettingValue<T>) data.put(setting.getKey(), setting.getValue());
    } catch (ClassCastException e) {
      throw mismatchedState(setting.getKey(), setting.getValue());
    }
  }

  @Deprecated
  @Override
  public SettingValue<?> remove(Object o) {
    return data.remove(o);
  }

  /**
   * Type specific version of {@link #remove(Object)}
   * from default Java Map interface.
   *
   * @param key the key associated with the data to remove
   * @param <T> the type of key/value
   * @return the previous value that existed under that key, or null if none existed
   */
  @SuppressWarnings("unchecked")
  public <T> SettingValue<T> remove(SettingKey<T> key) {
    SettingValue<?> removed = data.remove(key);
    try {
      return (SettingValue<T>) removed;
    } catch (ClassCastException e) {
      throw mismatchedState(key, removed);
    }
  }

  @Override
  public void putAll(@Nonnull Map<? extends SettingKey<?>, ? extends SettingValue<?>> map) {
    map.forEach(this::put);
  }

  /**
   * Same as {@link #putAll(Map)} but specific to Settings,
   * so the type of SettingKey and SettingValue is preserved.
   *
   * @param settings all settings to add
   */
  public void putAll(@Nonnull Collection<Setting<?>> settings) {
    settings.forEach(this::put);
  }

  @Override
  public void clear() {
    data.clear();
  }

  @Override
  @Nonnull
  public Set<SettingKey<?>> keySet() {
    return data.keySet();
  }

  @Override
  @Nonnull
  public Collection<SettingValue<?>> values() {
    return data.values();
  }

  /**
   * Better to use the typed version of {@link #entries()}.
   *
   * @return the set of all keys and values.
   */
  @Deprecated
  @Override
  @Nonnull
  public Set<Entry<SettingKey<?>, SettingValue<?>>> entrySet() {
    return data.entrySet();
  }

  /**
   * Helping function to get all settings.
   * This is helpful for consistent typing.
   *
   * @return all settings in this map
   */
  @SuppressWarnings("unchecked")
  public Set<Setting<?>> entries() {
    return data.keySet().stream()
        .map(key -> Setting.of(
            (SettingKey<Object>) key,
            (SettingValue<Object>) this.get(key)))
        .collect(Collectors.toSet());
  }

  /**
   * Copy this setting map into a new one.
   *
   * @return the new copy
   */
  public SettingMap copy() {
    SettingMap copy = new SettingMap();
    copy.putAll(this);
    return copy;
  }

  private IllegalStateException mismatchedState(SettingKey<?> key, SettingValue<?> val) {
    return new IllegalStateException(String.format(
        "This SettingMap has an illegal mapping"
            + "of a key and value of two different types:"
            + "%s, %s",
        key.getDefaultData().getClass(),
        val.getData().getClass()));
  }

  private IllegalArgumentException illegalArguments(Object key, Object val) {
    return new IllegalArgumentException(String.format(
        "This method was given parameters with invalid"
            + "key and value types:"
            + "%s, %s",
        key.getClass(),
        val.getClass()));
  }
}
