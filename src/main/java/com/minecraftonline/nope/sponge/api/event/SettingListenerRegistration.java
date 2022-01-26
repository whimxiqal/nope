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

package com.minecraftonline.nope.sponge.api.event;

import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.sponge.listener.SettingValueLookupFunctionImpl;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.Order;
import org.spongepowered.plugin.PluginContainer;

public class SettingListenerRegistration<T, E extends Event> {

  @Getter
  @Accessors(fluent = true)
  private final SettingKey<? extends T, ?, ?> settingKey;

  @Getter
  @Accessors(fluent = true)
  private final Class<E> eventClass;

  @Getter
  @Accessors(fluent = true)
  private final Order order;

  @Getter
  @Accessors(fluent = true)
  private final PluginContainer plugin;

  @Getter
  @Accessors(fluent = true)
  private final SettingEventListener<T, E> settingEventListener;

  public SettingListenerRegistration(SettingKey<? extends T, ?, ?> settingKey,
                                     Class<E> eventClass,
                                     PluginContainer plugin,
                                     SettingEventListener<T, E> settingEventListener) {
    this.settingKey = settingKey;
    this.eventClass = eventClass;
    this.plugin = plugin;
    this.settingEventListener = settingEventListener;
    this.order = Order.EARLY;
  }

  public SettingListenerRegistration(SettingKey<? extends T, ?, ?> settingKey,
                                     Class<E> eventClass,
                                     PluginContainer plugin,
                                     SettingEventListener<T, E> settingEventListener,
                                     Order order) {
    this.settingKey = settingKey;
    this.eventClass = eventClass;
    this.plugin = plugin;
    this.settingEventListener = settingEventListener;
    this.order = order;
  }

  public void registerToSponge() {
    Sponge.eventManager().registerListener(EventListenerRegistration.builder(this.eventClass())
        .listener((event) -> this.settingEventListener()
            .handle(event, new SettingValueLookupFunctionImpl<>(this.settingKey())))
        .order(this.order())
        .plugin(this.plugin())
        .build());
  }

}
