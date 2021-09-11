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

package com.minecraftonline.nope.sponge.listener;

import com.minecraftonline.nope.common.setting.Setting;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.sponge.SpongeNope;
import java.util.Arrays;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.Order;

/**
 * A class to store information about how a SettingKey and SettingValue
 * changes the behavior of the game by listening to game events.
 *
 * @param <E> the event type for which to listen
 * @see Setting
 * @see SettingKey
 */
public abstract class SettingListener<E extends Event> implements EventListener<E> {

  private final SettingKey<?>[] keys;
  private final Class<E> eventClass;

  public SettingListener(@NotNull Class<E> eventClass,
                         @NotNull SettingKey<?>... keys) {
    this.eventClass = eventClass;
    this.keys = keys;
  }

  public final boolean registerIfAssigned() {
    boolean assigned;
    for (SettingKey<?> key : keys) {
      assigned = SpongeNope.instance().hostSystem().isAssigned(key);
      if (key.isUnnaturalDefault() || assigned) {
        Sponge.eventManager().registerListener(EventListenerRegistration.builder(eventClass)
            .listener(this)
            .plugin(SpongeNope.instance().pluginContainer())
            .order(Order.EARLY)
            .build());
        return true;
      }
    }
    return false;
  }

}
