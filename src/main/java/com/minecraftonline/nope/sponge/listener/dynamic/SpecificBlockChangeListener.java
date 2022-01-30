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

import com.minecraftonline.nope.common.struct.SingleValueSet;
import com.minecraftonline.nope.sponge.api.event.SettingEventListener;
import com.minecraftonline.nope.sponge.api.event.SettingValueLookupFunction;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.world.server.ServerLocation;

public class SpecificBlockChangeListener implements SettingEventListener<Boolean, ChangeBlockEvent.All> {

  private final Set<BlockType> originalBlocks;
  private final Set<BlockType> finalBlocks;
  private final boolean environmentCauseOnly;

  private SpecificBlockChangeListener(Set<BlockType> originalBlocks, Set<BlockType> finalBlocks) {
    this(originalBlocks, finalBlocks, false);
  }
  private SpecificBlockChangeListener(Set<BlockType> originalBlocks, Set<BlockType> finalBlocks, boolean environmentCauseOnly) {
    this.originalBlocks = originalBlocks;
    this.finalBlocks = finalBlocks;
    this.environmentCauseOnly = environmentCauseOnly;
  }

  public static SpecificBlockChangeListener oneToOne(BlockType originalBlock,
                                                     BlockType finalBlock) {
    return new SpecificBlockChangeListener(new SingleValueSet<>(originalBlock),
        new SingleValueSet<>(finalBlock));
  }

  public static SpecificBlockChangeListener oneToOne(BlockType originalBlock,
                                                     BlockType finalBlock,
                                                     boolean environmentCauseOnly) {
    return new SpecificBlockChangeListener(new SingleValueSet<>(originalBlock),
        new SingleValueSet<>(finalBlock),
        environmentCauseOnly);
  }

  public static SpecificBlockChangeListener manyToMany(Set<BlockType> originalBlocks,
                                                       Set<BlockType> finalBlocks) {
    return new SpecificBlockChangeListener(originalBlocks, finalBlocks);
  }

  public static SpecificBlockChangeListener forOriginal(BlockType blockType) {
    return new SpecificBlockChangeListener(new SingleValueSet<>(blockType), Collections.emptySet());
  }

  public static SpecificBlockChangeListener forFinal(BlockType blockType) {
    return new SpecificBlockChangeListener(Collections.emptySet(), new SingleValueSet<>(blockType));
  }

  @Override
  public void handle(ChangeBlockEvent.All event,
                     SettingValueLookupFunction<Boolean> lookupFunction) {
    final Object eventCause = event.cause().root();
    final Player player;
    if (eventCause instanceof Player) {
      if (environmentCauseOnly) {
        return;
      }
      player = (Player) eventCause;
    } else {
      player = null;
    }
    Optional<ServerLocation> blockLocation;
    for (BlockTransaction transaction : event.transactions()) {
      blockLocation = transaction.finalReplacement().location();
      if (blockLocation.isPresent()) {
        if (originalBlocks.isEmpty() || originalBlocks.contains(transaction.original().state().type())
            && (finalBlocks.isEmpty() || finalBlocks.contains(transaction.finalReplacement().state().type()))
            && (!lookupFunction.lookup(eventCause, blockLocation.get())
            || (player != null && !lookupFunction.lookup(player, player.serverLocation())))) {
          transaction.invalidate();
        }
      }
    }
  }
}