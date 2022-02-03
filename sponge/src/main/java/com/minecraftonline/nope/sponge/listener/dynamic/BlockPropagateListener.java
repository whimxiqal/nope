/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

import com.minecraftonline.nope.common.setting.SettingKeys;
import com.minecraftonline.nope.sponge.api.event.SettingEventListener;
import com.minecraftonline.nope.sponge.api.event.SettingValueLookupFunction;
import java.util.Optional;
import org.spongepowered.api.block.transaction.NotificationTicket;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.world.server.ServerLocation;

/**
 * Block propagate listener.
 *
 * @see SettingKeys#BLOCK_CHANGE
 */
public class BlockPropagateListener implements SettingEventListener<Boolean, NotifyNeighborBlockEvent> {
  @Override
  public void handle(NotifyNeighborBlockEvent event, SettingValueLookupFunction<Boolean> lookupFunction) {
    for (NotificationTicket ticket : event.tickets()) {
      final ServerLocation start = ticket.notifier().serverLocation();
      final Optional<ServerLocation> endOptional = ticket.target().location();
      if (endOptional.isPresent()) {
        if (!lookupFunction.lookup(null, start) || !lookupFunction.lookup(null, endOptional.get())) {
          event.setCancelled(true);
          return;
        }
      }
    }
  }
}
