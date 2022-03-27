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

import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.common.struct.AltSet;
import me.pietelite.nope.sponge.api.event.SettingEventContext;
import me.pietelite.nope.sponge.api.event.SettingEventListener;
import me.pietelite.nope.sponge.api.event.SettingEventReport;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.entity.DamageEntityEvent;

/**
 * Implements {@link me.pietelite.nope.common.setting.SettingKeys#INVINCIBLE_ENTITIES}.
 */
public class InvincibleEntitiesListener implements SettingEventListener<AltSet<String>, DamageEntityEvent> {
  @Override
  public void handle(SettingEventContext<AltSet<String>, DamageEntityEvent> context) {
    if (context.event().source() instanceof ServerPlayer) {
      ServerPlayer playerCause = (ServerPlayer) context.event().source();
      if (playerCause.hasPermission(Permissions.UNRESTRICTED.get())) {
        // This player can damage whatever they want
        return;
      }
    }
    final ResourceKey entityKey = EntityTypes.registry().valueKey(context.event().entity().type());
    if (context.lookup(context.event().entity(), context.event().entity().serverLocation())
        .contains(entityKey.value())) {
      context.event().setCancelled(true);
      context.report(SettingEventReport.restricted().target(entityKey.formatted()).build());
    }
  }
}
