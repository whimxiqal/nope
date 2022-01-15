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

package com.minecraftonline.nope.sponge.listenernew;

import com.minecraftonline.nope.common.settingnew.SettingKeyStore;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.api.SettingEventHandler;
import com.minecraftonline.nope.sponge.api.SettingListenerRegistration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListenerRegistration;

public class SettingListenerStore {

  private final SettingKeyStore settingKeys;
  private final HashMap<String, List<SettingEventHandler<?, ?>>> settingListeners = new HashMap<>();

  public SettingListenerStore(@NotNull SettingKeyStore settingKeys) {
    this.settingKeys = settingKeys;
  }

  public <T, E extends Event> void register(SettingListenerRegistration<T, E> registration) {

    if (!settingKeys.containsId(registration.settingKey().id())) {
      throw new IllegalStateException(String.format("Cannot register setting listener because "
          + "setting key %s is not registered.", registration.settingKey().id()));
    }

    this.settingListeners.computeIfAbsent(registration.settingKey().id(), (k) -> new ArrayList<>(1))
        .add(registration.settingEventHandler());

    // TODO move this registration so that it only registers if the setting is set somewhere on the server and
    //  the value is set to something other than the default, non game-changing behavior way,
    //  or alternatively if the default value of the setting is game-changing inherently.
    Sponge.eventManager().registerListener(EventListenerRegistration.builder(registration.eventClass())
        .listener((event) -> {
          registration.settingEventHandler().handle(event, (userUuid, location) ->
              SpongeNope.instance().hostSystem().lookup(registration.settingKey(), userUuid, location));
        })
        .order(registration.order())
        .plugin(registration.plugin())
        .build());
  }

}
