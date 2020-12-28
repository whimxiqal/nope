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

import com.google.common.collect.ImmutableSet;
import com.minecraftonline.nope.host.Host;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

/**
 * The value component of a {@link Setting}.
 * A SettingValue is designed to be matched with a {@link SettingKey}
 * and placed into a {@link SettingMap} inside a {@link Host}
 *
 * @param <T>
 */
public class SettingValue<T> {

  /**
   * The important configurable value which determines the behavior
   * of the plugin for the corresponding {@link SettingKey}.
   * This field is called data but its called value in the configuration.
   */
  @Getter
  private final T data;

  /**
   * The targeted subject for the game behavior alteration made by
   * the {@link #data} value respective to the {@link SettingKey}
   */
  @Getter
  private final Target target;

  /**
   * Static factory. The target is set to null.
   *
   * @param data the core data
   * @param <X>  the type of data stored
   * @return the setting value
   */
  public static <X> SettingValue<X> of(X data) {
    return new SettingValue<>(data, null);
  }

  /**
   * Full static factory.
   *
   * @param data the core data
   * @param target the intended target of the setting
   * @param <X> the type of raw data stored
   * @return the setting
   */
  public static <X> SettingValue<X> of(X data, Target target) {
    return new SettingValue<>(data, target);
  }

  private SettingValue(T data, Target target) {
    this.data = data;
    this.target = target;
  }


  /**
   * A class to manage the entities to which an instance of
   * a {@link Setting} applies.
   */
  @Builder
  public static class Target {
    private Set<String> groups;
    private Set<UUID> players;

    public Set<String> getGroups() {
      return ImmutableSet.copyOf(groups);
    }

    public Set<UUID> getPlayers() {
      return ImmutableSet.copyOf(players);
    }
  }

}
