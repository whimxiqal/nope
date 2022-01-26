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

import com.minecraftonline.nope.common.setting.sets.BlockChangeSet;
import com.minecraftonline.nope.sponge.api.event.SettingEventListener;
import com.minecraftonline.nope.sponge.api.event.SettingValueLookupFunction;
import java.util.Optional;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.ChangeBlockEvent;

public class BlockChangeListener implements SettingEventListener<BlockChangeSet, ChangeBlockEvent.All> {
  @Override
  public void handle(ChangeBlockEvent.All event, SettingValueLookupFunction<BlockChangeSet> lookupFunction) {
    for (BlockTransaction transaction : event.transactions()) {
      final boolean originallyAir = transaction.original().state().type().equals(BlockTypes.AIR.get());
      final boolean finallyAir = transaction.finalReplacement().state().type().equals(BlockTypes.AIR.get());
      final BlockChangeSet.BlockChange blockChangeType;
      if (originallyAir) {
        if (finallyAir) {
          return;
        } else {
          blockChangeType = BlockChangeSet.BlockChange.PLACE;
        }
      } else {
        if (finallyAir) {
          blockChangeType = BlockChangeSet.BlockChange.BREAK;
        } else {
          blockChangeType = BlockChangeSet.BlockChange.ALTER;
        }
      }
      final Optional<Player> player = event.cause().first(Player.class);
      final Player playerOrNull = player.orElse(null);
      if (transaction.original()
          .location()
          .map(loc -> !lookupFunction.lookup(playerOrNull, loc).contains(blockChangeType))
          .orElse(false)
          || transaction.finalReplacement()
          .location()
          .map(loc -> !lookupFunction.lookup(playerOrNull, loc).contains(blockChangeType))
          .orElse(false)
          || player.map(p -> !lookupFunction.lookup(playerOrNull, p.serverLocation()).contains(blockChangeType))
          .orElse(false)) {
        transaction.setValid(false);
      }
    }
  }
}
