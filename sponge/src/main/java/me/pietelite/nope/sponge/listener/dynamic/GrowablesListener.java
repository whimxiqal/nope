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
import me.pietelite.nope.common.api.struct.AltSet;
import me.pietelite.nope.sponge.api.setting.SettingEventContext;
import me.pietelite.nope.sponge.api.setting.SettingEventListener;
import me.pietelite.nope.sponge.api.setting.SettingEventReport;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.block.ChangeBlockEvent;

/**
 * Implements {@link me.pietelite.nope.common.setting.SettingKeys#GROWABLES}.
 */
public class GrowablesListener implements SettingEventListener<AltSet<String>, ChangeBlockEvent.All> {
  @Override
  public void handle(SettingEventContext<AltSet<String>, ChangeBlockEvent.All> context) {
    final Optional<ServerPlayer> player = context.event().cause().first(ServerPlayer.class);
    final ServerPlayer playerOrNull = player.orElse(null);
    for (BlockTransaction transaction : context.event().transactions()) {
      final String crop = BlockTypes.registry()
          .valueKey(transaction.finalReplacement().state().type())
          .value();
      if (transaction.original()
          .location()
          .map(loc -> !context.lookup(playerOrNull, loc).contains(crop))
          .orElse(false)
          || transaction.finalReplacement()
          .location()
          .map(loc -> !context.lookup(playerOrNull, loc).contains(crop))
          .orElse(false)
          || player.map(p -> !context.lookup(playerOrNull, p.serverLocation()).contains(crop))
          .orElse(false)) {
        transaction.setValid(false);
        context.report(SettingEventReport.restricted().build());
      }
    }
  }
}