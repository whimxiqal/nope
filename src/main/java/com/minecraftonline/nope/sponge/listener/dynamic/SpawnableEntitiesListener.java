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

package com.minecraftonline.nope.sponge.listener.dynamic;

import com.minecraftonline.nope.common.struct.AltSet;
import com.minecraftonline.nope.sponge.api.event.SettingEventListener;
import com.minecraftonline.nope.sponge.api.event.SettingValueLookupFunction;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.world.server.ServerLocation;

public class SpawnableEntitiesListener implements SettingEventListener<AltSet<String>, SpawnEntityEvent.Pre> {
  @Override
  public void handle(SpawnEntityEvent.Pre event,
                     SettingValueLookupFunction<AltSet<String>> lookupFunction) {
    final Object rootCause = event.cause().root();
    final Player player;
    if (rootCause instanceof Player) {
      player = (Player) rootCause;
    } else {
      player = null;
    }

    ServerLocation location;
    AltSet<String> set;
    String entityName;
    for (Entity entity : event.entities()) {
      location = entity.serverLocation();
      set = lookupFunction.lookup(player, location);
      entityName = EntityTypes.registry().valueKey(entity.type()).value();
      if (!set.contains(entityName)) {
        event.setCancelled(true);
        return;
      }

      // If it was spawned by a player, then even if the spawning location is allowed for the
      //  entity type, make sure that the player can be spawning from their location.
      if (player != null) {
        set = lookupFunction.lookup(player, player.serverLocation());
        if (!set.contains(entityName)) {
          event.setCancelled(true);
          return;
        }
      }
    }
  }
}