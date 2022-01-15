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

package com.minecraftonline.nope.sponge.api;

import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.struct.Location;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.util.SpongeUtil;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.server.ServerLocation;

public class SettingValueLookupFunction<T> {

  private final SettingKey<T, ?> settingKey;

  public SettingValueLookupFunction(@NotNull SettingKey<T, ?> settingKey) {
    this.settingKey = settingKey;
  }

  public T lookup(Entity entity, ServerLocation location) {
    return SpongeUtil.valueFor(settingKey, entity, location);
  }

  public T lookup(UUID userUuid, Location location) {
    return SpongeNope.instance().hostSystem().lookup(settingKey, userUuid, location);
  }

}
