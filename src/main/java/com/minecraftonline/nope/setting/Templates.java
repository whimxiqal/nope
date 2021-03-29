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

package com.minecraftonline.nope.setting;

import com.google.common.collect.Sets;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class Templates {

  public static Template DEFAULT_PROTECTIONS = new Template("default-protections", () -> {
    SettingMap map = new SettingMap();
    map.put(Setting.of(SettingLibrary.ENDERDRAGON_GRIEF, SettingValue.of(false)));
    map.put(Setting.of(SettingLibrary.ENDERMAN_GRIEF, SettingValue.of(false)));
    map.put(Setting.of(SettingLibrary.EXPLOSION_GRIEF_BLACKLIST, SettingValue.of(Sets.newHashSet(SettingLibrary.ExplosiveEnum.values()))));
    map.put(Setting.of(SettingLibrary.FIRE_EFFECT, SettingValue.of(false)));
    map.put(Setting.of(SettingLibrary.FIRE_IGNITION, SettingValue.of(false)));
    map.put(Setting.of(SettingLibrary.LAVA_GRIEF, SettingValue.of(false)));
    map.put(Setting.of(SettingLibrary.TNT_IGNITION, SettingValue.of(false)));
    map.put(Setting.of(SettingLibrary.TNT_PLACEMENT, SettingValue.of(false)));
    map.put(Setting.of(SettingLibrary.WATER_GRIEF, SettingValue.of(false)));
    map.put(Setting.of(SettingLibrary.ZOMBIE_GRIEF, SettingValue.of(false)));
    return map;
  });

  public static Template MALICIOUS_PROTECTIONS = new Template("malicious-protections", () -> {
    SettingMap map = new SettingMap();
    map.put(Setting.of(SettingLibrary.ARMOR_STAND_DESTROY, SettingValue.of(false)));
    map.put(Setting.of(SettingLibrary.BLOCK_BREAK, SettingValue.of(false)));
    map.put(Setting.of(SettingLibrary.BLOCK_PLACE, SettingValue.of(false)));
    map.put(Setting.of(SettingLibrary.INTERACT, SettingValue.of(false)));
    map.put(Setting.of(SettingLibrary.ITEM_FRAME_DESTROY, SettingValue.of(false)));
    map.put(Setting.of(SettingLibrary.PAINTING_DESTROY, SettingValue.of(false)));
    map.put(Setting.of(SettingLibrary.VEHICLE_DESTROY, SettingValue.of(false)));
    map.put(Setting.of(SettingLibrary.VEHICLE_PLACE, SettingValue.of(false)));
    return map;
  });

  private Templates() {
  }

  public static Map<String, SettingMap> getMap() {
    return Arrays.stream(Templates.class.getDeclaredFields())
        .filter(field -> Modifier.isStatic(field.getModifiers()))
        .filter(field -> Template.class.isAssignableFrom(field.getType()))
        .map(field -> {
          try {
            return (Template) field.get(null);
          } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
          }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(Template::getName, Template::getValue));
  }

  public static SettingMap get(String name) throws NoSuchElementException {
    return getMap().get(name);
  }

}
