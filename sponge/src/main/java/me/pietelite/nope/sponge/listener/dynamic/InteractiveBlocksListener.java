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
import me.pietelite.nope.common.struct.AltSet;
import me.pietelite.nope.sponge.api.event.SettingEventContext;
import me.pietelite.nope.sponge.api.event.SettingEventListener;
import me.pietelite.nope.sponge.api.event.SettingEventReport;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.world.server.ServerLocation;

/**
 * Implements {@link me.pietelite.nope.common.setting.SettingKeys#INTERACTIVE_BLOCKS}.
 */
public class InteractiveBlocksListener implements
    SettingEventListener<AltSet<String>, InteractBlockEvent.Secondary> {
  @Override
  public void handle(SettingEventContext<AltSet<String>, InteractBlockEvent.Secondary> context) {
    final Optional<ServerLocation> location = context.event().block().location();
    if (location.isPresent()) {
      final Optional<Player> player = context.event().cause().first(Player.class);
      if (player.isPresent()) {
        final ResourceKey blockKey = BlockTypes.registry().valueKey(context.event().block().state().type());
        if (!context.lookup(player.get(), location.get()).contains(blockKey.value())
            || !context.lookup(player.get(), player.get().serverLocation()).contains(blockKey.value())) {
          context.event().setCancelled(true);
          context.report(SettingEventReport.restricted()
              .target(blockKey.formatted())
              .build());
        }
      }
    }
  }
}
