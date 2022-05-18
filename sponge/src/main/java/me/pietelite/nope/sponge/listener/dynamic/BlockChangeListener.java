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
import me.pietelite.nope.common.api.setting.BlockChange;
import me.pietelite.nope.common.setting.SettingKeys;
import me.pietelite.nope.common.setting.sets.BlockChangeSet;
import me.pietelite.nope.sponge.api.setting.SettingEventContext;
import me.pietelite.nope.sponge.api.setting.SettingEventListener;
import me.pietelite.nope.sponge.api.setting.SettingEventReport;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.server.ServerLocation;

/**
 * Block change listener.
 *
 * @see SettingKeys#BLOCK_CHANGE
 */
public class BlockChangeListener implements SettingEventListener<BlockChangeSet, ChangeBlockEvent.All> {
  @Override
  public void handle(SettingEventContext<BlockChangeSet, ChangeBlockEvent.All> context) {
    for (BlockTransaction transaction : context.event().transactions()) {
      final BlockChange blockChangeType;
      if (transaction.operation().equals(Operations.BREAK.get())) {
        blockChangeType = BlockChange.BREAK;
      } else if (transaction.operation().equals(Operations.PLACE.get())) {
        blockChangeType = BlockChange.PLACE;
      } else if (transaction.operation().equals(Operations.MODIFY.get())) {
        blockChangeType = BlockChange.MODIFY;
      } else if (transaction.operation().equals(Operations.GROWTH.get())) {
        blockChangeType = BlockChange.GROW;
      } else if (transaction.operation().equals(Operations.DECAY.get())) {
        blockChangeType = BlockChange.DECAY;
      } else {
        return;
      }
      if (transaction.finalReplacement()
          .location()
          .map(loc -> !context.lookup(loc).contains(blockChangeType))
          .orElse(false)) {
        transaction.invalidate();
        context.report(SettingEventReport.restricted()
            .source(context.event().source())
            .target(transaction.original().state().type().key(RegistryTypes.BLOCK_TYPE).formatted())
            .build());
      } else if (blockChangeType == BlockChange.GROW) {
        // If the origin of some growth is banned from growing anything, then we still must invalidate
        Optional<BlockSnapshot> growthOrigin = context.event().context().get(EventContextKeys.GROWTH_ORIGIN);
        if (growthOrigin.isPresent()) {
          Optional<ServerLocation> location = growthOrigin.get().location();
          if (location.isPresent()
              && !context.lookup(context.event().source(), location.get()).contains(blockChangeType)) {
            transaction.setValid(false);
            context.report(SettingEventReport.restricted()
                .target(growthOrigin.get().state().type().key(RegistryTypes.BLOCK_TYPE).formatted())
                .build());
          }
        }
      }
    }
  }

}
