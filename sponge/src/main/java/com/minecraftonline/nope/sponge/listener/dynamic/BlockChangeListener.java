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

import com.minecraftonline.nope.common.setting.sets.BlockChangeSet;
import com.minecraftonline.nope.sponge.api.event.SettingEventListener;
import com.minecraftonline.nope.sponge.api.event.SettingValueLookupFunction;
import com.minecraftonline.nope.sponge.listener.SpongeEventUtil;
import java.util.Optional;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.world.server.ServerLocation;

public class BlockChangeListener implements SettingEventListener<BlockChangeSet, ChangeBlockEvent.All> {
  @Override
  public void handle(ChangeBlockEvent.All event, SettingValueLookupFunction<BlockChangeSet> lookupFunction) {
    for (BlockTransaction transaction : event.transactions()) {
      final BlockChangeSet.BlockChange blockChangeType;
      if (transaction.operation().equals(Operations.BREAK.get())) {
        blockChangeType = BlockChangeSet.BlockChange.BREAK;
      } else if (transaction.operation().equals(Operations.PLACE.get())) {
        blockChangeType = BlockChangeSet.BlockChange.PLACE;
      } else if (transaction.operation().equals(Operations.MODIFY.get())) {
        blockChangeType = BlockChangeSet.BlockChange.MODIFY;
      } else if (transaction.operation().equals(Operations.GROWTH.get())) {
        blockChangeType = BlockChangeSet.BlockChange.GROW;
      } else if (transaction.operation().equals(Operations.DECAY.get())) {
        blockChangeType = BlockChangeSet.BlockChange.DECAY;
      } else {
        return;
      }
      if (SpongeEventUtil.invalidateTransactionIfNeeded(event.cause(),
          transaction,
          (cause, location) ->
              !lookupFunction.lookup(cause, location).contains(blockChangeType))) {
        return;
      } else if (blockChangeType == BlockChangeSet.BlockChange.GROW) {
        // If the origin of some growth is banned from growing anything, then we still must invalidate
        Optional<BlockSnapshot> growthOrigin = event.context().get(EventContextKeys.GROWTH_ORIGIN);
        if (growthOrigin.isPresent()) {
          Optional<ServerLocation> location = growthOrigin.get().location();
          if (location.isPresent()
              && !lookupFunction.lookup(event.cause().root(), location.get()).contains(blockChangeType)) {
            transaction.setValid(false);
          }
        }
      }
    }
  }
}
