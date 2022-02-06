/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

package me.pietelite.nope.sponge.util;

import java.util.UUID;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.struct.Location;
import me.pietelite.nope.sponge.api.event.SettingValueLookupFunction;
import org.spongepowered.api.world.server.ServerLocation;

public class SettingValueLookupFunctionImpl<T> implements SettingValueLookupFunction<T> {

  private final SettingKey<? extends T, ?, ?> key;

  public SettingValueLookupFunctionImpl(SettingKey<? extends T, ?, ?> key) {
    this.key = key;
  }

  @Override
  public T lookup(Object cause, ServerLocation location) {
    return SpongeUtil.valueFor(key, cause, location);
  }

  @Override
  public T lookup(UUID userUuid, Location location) {
    return SpongeUtil.valueFor(key, userUuid, location);
  }
}
