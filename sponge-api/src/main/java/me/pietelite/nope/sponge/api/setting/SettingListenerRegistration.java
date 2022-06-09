/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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

package me.pietelite.nope.sponge.api.setting;

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Order;
import org.spongepowered.plugin.PluginContainer;

/**
 * A registration wrapper around a {@link SettingEventListener}.
 *
 * @param <T> the data type
 * @param <E> the event type
 */
public class SettingListenerRegistration<T, E extends Event> {

  private final String settingKey;
  private final Class<? extends T> dataClass;
  private final Class<E> eventClass;
  private final Order order;
  private final PluginContainer plugin;
  private final SettingEventListener<T, E> settingEventListener;

  /**
   * Generic constructor.
   *
   * @param settingKey           the setting key associated with this listener
   * @param dataClass            the type of data
   * @param eventClass           the type of event
   * @param plugin               the plugin responsible for this listener
   * @param settingEventListener the actual listener to handle the event
   */
  public SettingListenerRegistration(String settingKey,
                                     Class<? extends T> dataClass,
                                     Class<E> eventClass,
                                     PluginContainer plugin,
                                     SettingEventListener<T, E> settingEventListener) {
    this.settingKey = settingKey;
    this.dataClass = dataClass;
    this.eventClass = eventClass;
    this.plugin = plugin;
    this.settingEventListener = settingEventListener;
    this.order = Order.EARLY;
  }

  /**
   * More specific constructor.
   *
   * @param settingKey           the setting key associated with this listener
   * @param dataClass            the type of data
   * @param eventClass           the type of event
   * @param plugin               the plugin responsible for this listener
   * @param settingEventListener the actual listener to handle the event
   * @param order                the event ordering with which to register this event to the mod platform
   */
  public SettingListenerRegistration(String settingKey,
                                     Class<? extends T> dataClass,
                                     Class<E> eventClass,
                                     PluginContainer plugin,
                                     SettingEventListener<T, E> settingEventListener,
                                     Order order) {
    this.settingKey = settingKey;
    this.dataClass = dataClass;
    this.eventClass = eventClass;
    this.plugin = plugin;
    this.settingEventListener = settingEventListener;
    this.order = order;
  }

  public String settingKey() {
    return settingKey;
  }

  public Class<? extends T> dataClass() {
    return dataClass;
  }

  public Class<E> eventClass() {
    return eventClass;
  }

  public Order order() {
    return order;
  }

  public PluginContainer plugin() {
    return plugin;
  }

  public SettingEventListener<T, E> settingEventListener() {
    return settingEventListener;
  }

}
