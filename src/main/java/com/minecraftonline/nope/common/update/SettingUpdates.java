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

package com.minecraftonline.nope.common.update;

import com.google.gson.JsonElement;
import com.minecraftonline.nope.common.setting.keys.BooleanSettingKey;
import com.minecraftonline.nope.common.setting.Setting;
import com.minecraftonline.nope.common.setting.SettingLibrary;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A class to store a series of static {@link SettingUpdate}s
 * which help to convert a version of settings to new settings
 * if changes were made to how settings worked throughout
 * development.
 */
@SuppressWarnings("unused")
public class SettingUpdates {

  @ActiveSettingUpdate
  public static final SettingUpdate<Boolean, Set<SettingLibrary.Explosive>> CREEPER_EXPLOSION_DAMAGE_UPDATE =
      new SettingUpdate<>(new BooleanSettingKey(
          "creeper-explosion-damage",
          true
      ), value -> {
        if (value.getData()) {
          return Setting.of(SettingLibrary.EXPLOSION_DAMAGE_BLACKLIST,
              SettingValue.of(new HashSet<>(), value.getTarget()));
        } else {
          return Setting.of(SettingLibrary.EXPLOSION_DAMAGE_BLACKLIST,
              SettingValue.of(new HashSet<>(Collections.singleton(
                  SettingLibrary.Explosive.CREEPER)),
                  value.getTarget()));
        }
      });

  @ActiveSettingUpdate
  public static final SettingUpdate<Boolean, Set<SettingLibrary.Explosive>> CREEPER_EXPLOSION_GRIEF_UPDATE =
      new SettingUpdate<>(new BooleanSettingKey(
          "creeper-explosion-grief",
          true
      ), value -> {
        if (value.getData()) {
          return Setting.of(SettingLibrary.EXPLOSION_GRIEF_BLACKLIST,
              SettingValue.of(new HashSet<>(), value.getTarget()));
        } else {
          return Setting.of(SettingLibrary.EXPLOSION_GRIEF_BLACKLIST,
              SettingValue.of(new HashSet<>(Collections.singleton(
                  SettingLibrary.Explosive.CREEPER)),
                  value.getTarget()));
        }
      });

  /**
   * Get all {@link SettingUpdate}s in the class that are
   * annotated with {@link ActiveSettingUpdate} and apply
   * the appropriate converter if it exists
   * method.
   */
  @SuppressWarnings("unchecked")
  public static <T> Optional<? extends Setting<?>> convertSetting(String oldSettingId,
                                                                  JsonElement oldSettingValue,
                                                                  JsonElement oldSettingTarget) {
    return Arrays.stream(SettingUpdates.class.getDeclaredFields())
        .filter(field -> Modifier.isStatic(field.getModifiers()))
        .filter(field -> SettingUpdate.class.isAssignableFrom(field.getType()))
        .filter(field -> Arrays.stream(field.getAnnotations()).anyMatch(annotation ->
            annotation instanceof ActiveSettingUpdate))
        .map(field -> {
          try {
            return (SettingUpdate<T, ?>) field.get(null);
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
          return null;
        }).filter(Objects::nonNull)
        .filter(update -> update.getKey().getId().equals(oldSettingId))
        .map(update -> update.getConverter().apply(SettingValue.of(
            update.getKey().deserializeDataGenerified(oldSettingValue),
            SettingValue.Target.fromJson(oldSettingTarget))))
        .findFirst();
  }

  /**
   * An annotation to mark a setting update as "active",
   * which will mean that the update will be applied.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface ActiveSettingUpdate {
    // none
  }

}
