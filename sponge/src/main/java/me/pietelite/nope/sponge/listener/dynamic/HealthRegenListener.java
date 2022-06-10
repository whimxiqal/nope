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

package me.pietelite.nope.sponge.listener.dynamic;

import java.util.Optional;
import me.pietelite.nope.sponge.api.setting.SettingEventContext;
import me.pietelite.nope.sponge.api.setting.SettingEventListener;
import me.pietelite.nope.sponge.api.setting.SettingEventReport;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;

/**
 * Implements {@link me.pietelite.nope.common.setting.SettingKeys#HEALTH_REGEN}.
 */
public class HealthRegenListener implements SettingEventListener<Boolean, ChangeDataHolderEvent.ValueChange> {
  @Override
  public void handle(SettingEventContext<Boolean, ChangeDataHolderEvent.ValueChange> context) {
    if (context.event().targetHolder() instanceof ServerPlayer) {
      ServerPlayer player = (ServerPlayer) context.event().targetHolder();
      Optional<Value.Immutable<Double>> changedHealth = context.event().endResult()
          .successfulValue(Keys.HEALTH);
      if (changedHealth.isPresent()
          && (changedHealth.get().get() > player.get(Keys.HEALTH).orElse(0d))
          && !context.lookup(player, player.serverLocation())) {
        context.event().setCancelled(true);
        context.report(SettingEventReport.restricted()
            .target(player.name())
            .build());
      }
    }
  }
}
