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

package me.pietelite.nope.sponge.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingKeyStore;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.api.setting.SettingEventListener;
import me.pietelite.nope.sponge.api.setting.SettingListenerRegistration;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListenerRegistration;

/**
 * The in-memory storage for all registered {@link SettingEventListener}s.
 */
public class SettingListenerStore {

  private final SettingKeyStore settingKeys;
  private final HashMap<String, List<SettingListenerRegistration<?, ?>>> settingListeners = new HashMap<>();
  private List<String> unregisteredKeys = new LinkedList<>();

  public SettingListenerStore(@NotNull SettingKeyStore settingKeys) {
    this.settingKeys = settingKeys;
  }

  /**
   * Stage ("register") a new {@link SettingEventListener}
   * wrapped in a {@link SettingListenerRegistration}.
   * This only prepares the wrapped listeners for registration with the mod platform.
   *
   * @param registration the registration to stage
   * @param <T>          the data type
   * @param <E>          the event type
   * @see #registerAll()
   */
  public <T, E extends Event> void stage(SettingListenerRegistration<T, E> registration) {
    if (!settingKeys.containsId(registration.settingKey())) {
      throw new IllegalStateException(String.format("Cannot register setting listener because "
          + "setting key %s is not registered.", registration.settingKey()));
    }

    this.settingListeners.computeIfAbsent(registration.settingKey(),
            (k) -> new ArrayList<>(1))
        .add(registration);
    this.unregisteredKeys.add(registration.settingKey());
  }

  /**
   * Register to the mod platform all staged listeners that must be registered to perform their
   * required functionality.
   * This should be called any time the {@link me.pietelite.nope.common.setting.Setting}s change
   * anywhere because some listeners may have only just become required.
   * It must be called on the main thread.
   */
  public void registerAll() {
    List<String> stillUnregistered = new LinkedList<>();
    int registerCount = 0;
    SettingKey<?, ?, ?> key;
    for (String keyId : unregisteredKeys) {
      key = settingKeys.get(keyId);
      if (SpongeNope.instance().system().isAssigned(key)) {
        for (SettingListenerRegistration<?, ?> registration : settingListeners.get(key.id())) {
          registerCount++;
          registerToSponge(registration);
        }
      } else {
        stillUnregistered.add(keyId);
      }
    }
    if (registerCount > 0) {
      SpongeNope.instance().logger().info("Registered " + registerCount + " dynamic listener(s)");
    }
    unregisteredKeys = stillUnregistered;
  }

  @SuppressWarnings("unchecked")
  private <T, E extends Event> void registerToSponge(SettingListenerRegistration<T, E> registration) {
    SettingKey<?, ?, ?> key = settingKeys.get(registration.settingKey());
    if (!registration.dataClass().isAssignableFrom(key.manager().dataType())) {
      throw new IllegalStateException("The setting key's data type in the registration "
          + "must match the registration's data type");
    }
    Sponge.eventManager().registerListener(EventListenerRegistration.builder(registration.eventClass())
        .listener(event -> registration.settingEventListener().handle(new SettingEventContextImpl<>(event,
            (SettingKey<T, ?, ?>) settingKeys.get(registration.settingKey()))))
        .plugin(registration.plugin())
        .order(registration.order())
        .build());
  }

}
