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

import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingKeyStore;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.api.event.SettingListenerRegistration;
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

    this.settingListeners.computeIfAbsent(registration.settingKey().id(),
            (k) -> new ArrayList<>(1))
        .add(registration);
    this.unregisteredKeys.add(registration.settingKey());
  }

  public void registerAll() {
    List<SettingKey<?, ?, ?>> stillUnregistered = new LinkedList<>();
    int registerCount = 0;
    for (SettingKey<?, ?, ?> key : unregisteredKeys) {
      if (SpongeNope.instance().hostSystem().isAssigned(key)) {
        for (SettingListenerRegistration<?, ?> registration : settingListeners.get(key.id())) {
          registerCount++;
          registration.registerToSponge();
        }
      } else {
        stillUnregistered.add(key);
      }
    }
    if (registerCount > 0) {
      SpongeNope.instance().logger().info("Registered " + registerCount + " dynamic listener(s)");
    }
    unregisteredKeys = stillUnregistered;
  }

}
