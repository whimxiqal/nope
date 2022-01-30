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

package com.minecraftonline.nope.sponge.listener;

import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingKeyStore;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.api.event.SettingListenerRegistration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.Event;

public class SettingListenerStore {

  private final SettingKeyStore settingKeys;
  private final HashMap<String, List<SettingListenerRegistration<?, ?>>> settingListeners = new HashMap<>();
  private List<SettingKey<?, ?, ?>> unregisteredKeys = new LinkedList<>();

  public SettingListenerStore(@NotNull SettingKeyStore settingKeys) {
    this.settingKeys = settingKeys;
  }

  public <T, E extends Event> void stage(SettingListenerRegistration<T, E> registration) {
    if (!settingKeys.containsId(registration.settingKey().id())) {
      throw new IllegalStateException(String.format("Cannot register setting listener because "
          + "setting key %s is not registered.", registration.settingKey().id()));
    }

    this.settingListeners.computeIfAbsent(registration.settingKey().id(), (k) -> new ArrayList<>(1))
        .add(registration);
    this.unregisteredKeys.add(registration.settingKey());
  }

  public void registerAll() {
    List<SettingKey<?, ?, ?>> stillUnregistered = new LinkedList<>();
    for (SettingKey<?, ?, ?> key : unregisteredKeys) {
      if (SpongeNope.instance().hostSystem().isAssigned(key)) {
        settingListeners.get(key.id()).forEach(SettingListenerRegistration::registerToSponge);
      } else {
        stillUnregistered.add(key);
      }
    }
    final int count = unregisteredKeys.size() - stillUnregistered.size();
    if (count > 0) {
      SpongeNope.instance().logger().info("Registered " + count + " dynamic listener(s)");
    }
    unregisteredKeys = stillUnregistered;
  }

}
