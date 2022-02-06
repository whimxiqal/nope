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

import me.pietelite.nope.common.struct.AltSet;
import me.pietelite.nope.sponge.api.event.SettingEventListener;
import me.pietelite.nope.sponge.api.event.SettingValueLookupFunction;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.InteractEntityEvent;

public class InteractiveEntitiesListener implements SettingEventListener<AltSet<String>, InteractEntityEvent.Secondary> {
  @Override
  public void handle(InteractEntityEvent.Secondary event, SettingValueLookupFunction<AltSet<String>> lookupFunction) {
    if (event.source() instanceof Player) {
      final Player player = (Player) event.source();
      final String entityName = EntityTypes.registry().valueKey(event.entity().type()).value();
      if (!lookupFunction.lookup(player, event.entity().serverLocation()).contains(entityName)
          || !lookupFunction.lookup(player, player.serverLocation()).contains(entityName)) {
        event.setCancelled(true);
      }
    }
  }
}
