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

import me.pietelite.nope.common.api.struct.AltSet;
import me.pietelite.nope.sponge.api.event.SettingEventContext;
import me.pietelite.nope.sponge.api.event.SettingEventListener;
import me.pietelite.nope.sponge.api.event.SettingEventReport;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.world.server.ServerLocation;

/**
 * Implementation for {@link me.pietelite.nope.common.setting.SettingKeys#SPAWNABLE_ENTITIES}.
 */
public class SpawnableEntitiesListener implements SettingEventListener<AltSet<String>, SpawnEntityEvent.Pre> {
  @Override
  public void handle(SettingEventContext<AltSet<String>, SpawnEntityEvent.Pre> context) {
    final Object rootCause = context.event().cause().root();
    final ServerPlayer player;
    if (rootCause instanceof ServerPlayer) {
      player = (ServerPlayer) rootCause;
    } else {
      player = null;
    }

    ServerLocation location;
    AltSet<String> set;
    ResourceKey entityKey;
    for (Entity entity : context.event().entities()) {
      location = entity.serverLocation();
      set = context.lookup(player, location);
      entityKey = EntityTypes.registry().valueKey(entity.type());
      SettingEventReport report = SettingEventReport.restricted()
          .target(entityKey.formatted())
          .build();
      if (!set.contains(entityKey.value())) {
        context.event().setCancelled(true);
        context.report(report);
        return;
      }

      // If it was spawned by a player, then even if the spawning location is allowed for the
      //  entity type, make sure that the player can be spawning from their location.
      if (player != null) {
        set = context.lookup(player, player.serverLocation());
        if (!set.contains(entityKey.value())) {
          context.event().setCancelled(true);
          context.report(report);
          return;
        }
      }
    }
  }
}