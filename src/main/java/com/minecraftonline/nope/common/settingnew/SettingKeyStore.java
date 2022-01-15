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

package com.minecraftonline.nope.common.settingnew;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;

public class SettingKeyStore {

  private final HashMap<String, SettingKey<?, ?>> settingMap = Maps.newHashMap();
  private boolean locked;

  public void register(@NotNull SettingKey<?, ?> settingKey) throws IllegalArgumentException {
    if (locked) {
      throw new IllegalStateException("The setting key store is locked. "
          + "You may only register keys during the allocated event.");
    }
    if (settingMap.containsKey(settingKey.id())) {
      throw new IllegalArgumentException(String.format("A setting key with id %s already exists", settingKey.id()));
    }
    settingMap.put(settingKey.id(), settingKey);
  }

  /**
   * Get a SettingKey based on its id.
   *
   * @param id the id of a SettingKey
   * @return the SettingKey keyed with that id
   * @throws NoSuchElementException if there is no SettingKey with that id
   */
  public SettingKey<?, ?> get(@NotNull String id) throws NoSuchElementException {
    SettingKey<?, ?> output = settingMap.get(id);
    if (output == null) {
      throw new NoSuchElementException(String.format(
          "There is no setting with id '%s'",
          id));
    }
    return output;
  }

  public boolean containsId(@NotNull String id) {
    return settingMap.containsKey(id);
  }

  public Map<String, SettingKey<?, ?>> keys() {
    return settingMap;
  }

  public void lock() {
    this.locked = true;
  }

  public boolean isEmpty() {
    return settingMap.isEmpty();
  }

}
