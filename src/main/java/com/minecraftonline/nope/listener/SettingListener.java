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

package com.minecraftonline.nope.listener;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.setting.Setting;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingValue;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;

import javax.annotation.Nonnull;

/**
 * A class to store information about how a SettingKey and SettingValue
 * changes the behavior of the game by listening to game events.
 *
 * @param <E> the event type for which to listen
 * @see Setting
 * @see SettingKey
 * @see SettingValue
 */
public class SettingListener<E extends Event> implements EventListener<E> {

  private final SettingKey<?> key;
  private final EventListener<E> listener;
  private final Class<E> eventClass;

  private boolean registered = false;

  public SettingListener(@Nonnull SettingKey<?> key,
                         @Nonnull Class<E> eventClass,
                         @Nonnull EventListener<E> listener) {
    this.key = key;
    this.eventClass = eventClass;
    this.listener = listener;
  }

  @Override
  public final void handle(@Nonnull E event) throws Exception {
    this.listener.handle(event);
  }

  /**
   * Register if this SettingKey is currently relevant on the server.
   * It is relevant if this SettingKey has been assigned to a value,
   * any of its ancestors have been assigned to a value,
   * or its default value provides unnatural changes to the behavior
   * of the game.
   */
  public final void registerIfNecessary() {
    if (this.registered) {
      return;
    }
    boolean assigned = false;
    assigned = Nope.getInstance().getHostTree().isAssigned(key);
    SettingKey<?> cur = key;
    while (!assigned && cur.getParent().isPresent()) {
      cur = cur.getParent().get();
      assigned = Nope.getInstance().getHostTree().isAssigned(cur);
    }
    if (key.hasUnnaturalDefault() || assigned) {
      Sponge.getEventManager().registerListener(Nope.getInstance(), eventClass, listener);
      this.registered = true;
    }
  }

  protected final SettingKey<?> getKey() {
    return this.key;
  }

  protected final EventListener<E> getListener() {
    return this.listener;
  }

  protected final Class<E> getEventClass() {
    return this.eventClass;
  }

  public final boolean isRegistered() {
    return this.registered;
  }
}
