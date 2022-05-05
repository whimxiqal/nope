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
import me.pietelite.nope.common.setting.SettingKeys;
import me.pietelite.nope.sponge.api.setting.SettingEventContext;
import me.pietelite.nope.sponge.api.setting.SettingEventListener;
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
  public void handle(SettingEventContext<Boolean, NotifyNeighborBlockEvent> context) {
    for (NotificationTicket ticket : context.event().tickets()) {
      final ServerLocation start = ticket.notifier().serverLocation();
      final Optional<ServerLocation> endOptional = ticket.target().location();
      if (endOptional.isPresent()) {
        if (!context.lookup(null, start) || !context.lookup(null, endOptional.get())) {
          context.event().setCancelled(true);
          return;
        }
      }
    }
  }
}
