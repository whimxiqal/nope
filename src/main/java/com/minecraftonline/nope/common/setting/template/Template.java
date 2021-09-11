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

package com.minecraftonline.nope.common.setting.template;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.setting.Setting;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.setting.SettingKeys;
import com.minecraftonline.nope.common.struct.Named;
import java.util.Collection;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

public class Template extends SettingCollection implements Named {

  public static Collection<Template> INITIAL = Lists.newArrayList(
      new Template("smp-protections",
          "General global protection settings for a generic survival multiplayer server",
          Setting.of(SettingKeys.ENDERDRAGON_GRIEF, false),
          Setting.of(SettingKeys.ENDERMAN_GRIEF, false),
          Setting.of(SettingKeys.EXPLOSION_GRIEF_BLACKLIST,
              Sets.newHashSet(SettingKeys.Explosive.values())),
          Setting.of(SettingKeys.FIRE_EFFECT, false),
          Setting.of(SettingKeys.FIRE_IGNITION, false),
          Setting.of(SettingKeys.LAVA_GRIEF, false),
          Setting.of(SettingKeys.TNT_IGNITION, false),
          Setting.of(SettingKeys.TNT_PLACEMENT, false),
          Setting.of(SettingKeys.WATER_GRIEF, false),
          Setting.of(SettingKeys.ZOMBIE_GRIEF, false)),
      new Template("destructive-player-protections",
          "Protection settings from players attempting to be destructive",
          Setting.of(SettingKeys.ARMOR_STAND_DESTROY, false),
          Setting.of(SettingKeys.ARMOR_STAND_INTERACT, false),
          Setting.of(SettingKeys.ARMOR_STAND_PLACE, false),
          Setting.of(SettingKeys.BLOCK_BREAK, false),
          Setting.of(SettingKeys.BLOCK_PLACE, false),
          Setting.of(SettingKeys.FLOWER_POT_INTERACT, false),
          Setting.of(SettingKeys.ITEM_FRAME_DESTROY, false),
          Setting.of(SettingKeys.ITEM_FRAME_INTERACT, false),
          Setting.of(SettingKeys.ITEM_FRAME_PLACE, false),
          Setting.of(SettingKeys.PAINTING_DESTROY, false),
          Setting.of(SettingKeys.PAINTING_PLACE, false),
          Setting.of(SettingKeys.VEHICLE_DESTROY, false),
          Setting.of(SettingKeys.VEHICLE_PLACE, false)));

  @Getter
  @Accessors(fluent = true)
  private final String name;
  @Getter
  @Accessors(fluent = true)
  private final String description;

  public Template(@NotNull String name,
                  @NotNull String description,
                  Setting<?>... settings) {
    this.name = name;
    this.description = description;
    Stream.of(settings).forEach(this::set);
  }

  public Template(@NotNull String name,
                  @NotNull String description,
                  Iterable<Setting<?>> settings) {
    this.name = name;
    this.description = description;
    settings.forEach(this::set);
  }

  @Override
  public int hashCode() {
    return this.name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Template && this.name.equals(((Template) obj).name);
  }

  @Override
  public void save() {
    Nope.instance().data().templates().save(Nope.instance().hostSystem().templates());
  }

}
