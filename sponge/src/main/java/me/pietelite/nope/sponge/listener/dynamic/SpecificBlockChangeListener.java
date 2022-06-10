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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import me.pietelite.nope.common.struct.SingleValueSet;
import me.pietelite.nope.sponge.api.setting.SettingEventContext;
import me.pietelite.nope.sponge.api.setting.SettingEventListener;
import me.pietelite.nope.sponge.api.setting.SettingEventReport;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.server.ServerLocation;

/**
 * A generic listener for handling the events and settings related to changing blocks.
 */
public class SpecificBlockChangeListener implements SettingEventListener<Boolean, ChangeBlockEvent.All> {

  private final Set<BlockType> originalBlocks;
  private final Set<BlockType> finalBlocks;
  private final boolean environmentCauseOnly;

  private SpecificBlockChangeListener(Set<BlockType> originalBlocks, Set<BlockType> finalBlocks) {
    this(originalBlocks, finalBlocks, false);
  }

  private SpecificBlockChangeListener(Set<BlockType> originalBlocks,
                                      Set<BlockType> finalBlocks,
                                      boolean environmentCauseOnly) {
    this.originalBlocks = originalBlocks;
    this.finalBlocks = finalBlocks;
    this.environmentCauseOnly = environmentCauseOnly;
  }

  /**
   * Create a listener for handling the changing from one type of block to one other block type.
   *
   * @param originalBlock the starting type of block
   * @param finalBlock    the ending type of block
   * @return the listener
   */
  public static SpecificBlockChangeListener oneToOne(BlockType originalBlock,
                                                     BlockType finalBlock) {
    return new SpecificBlockChangeListener(new SingleValueSet<>(originalBlock),
        new SingleValueSet<>(finalBlock));
  }

  /**
   * Create a listener for handling the changing from one type of block to one other block type.
   *
   * @param originalBlock        the starting type of block
   * @param finalBlock           the ending type of block
   * @param environmentCauseOnly whether to only handle the event if it was caused naturally,
   *                             by the environment
   * @return the listener
   */
  public static SpecificBlockChangeListener oneToOne(BlockType originalBlock,
                                                     BlockType finalBlock,
                                                     boolean environmentCauseOnly) {
    return new SpecificBlockChangeListener(new SingleValueSet<>(originalBlock),
        new SingleValueSet<>(finalBlock),
        environmentCauseOnly);
  }

  /**
   * Create a listener for handling the changing from one of a set of block types
   * to one of another set of block types.
   *
   * @param originalBlocks a set of starting block types
   * @param finalBlocks    a set of ending block types
   * @return the listener
   */
  public static SpecificBlockChangeListener manyToMany(Set<BlockType> originalBlocks,
                                                       Set<BlockType> finalBlocks) {
    return new SpecificBlockChangeListener(originalBlocks, finalBlocks);
  }

  /**
   * Create a listener for handling the changing from one type of block to any other type ot block.
   *
   * @param blockType the starting type of block
   * @return the listener
   */
  public static SpecificBlockChangeListener forOriginal(BlockType blockType) {
    return new SpecificBlockChangeListener(new SingleValueSet<>(blockType), Collections.emptySet());
  }

  /**
   * Create a listener for handling the changing from any type of block to one other type ot block.
   *
   * @param blockType the final type of block
   * @return the listener
   */
  public static SpecificBlockChangeListener forFinal(BlockType blockType) {
    return new SpecificBlockChangeListener(Collections.emptySet(), new SingleValueSet<>(blockType));
  }

  @Override
  public void handle(SettingEventContext<Boolean, ChangeBlockEvent.All> context) {
    if (environmentCauseOnly && context.event().source() instanceof ServerPlayer) {
      // We don't want to do anything if it's caused by a player here
      return;
    }
    Optional<ServerLocation> blockLocation;
    for (BlockTransaction transaction : context.event().transactions()) {
      blockLocation = transaction.finalReplacement().location();
      if (blockLocation.isPresent()) {
        if ((originalBlocks.isEmpty() || originalBlocks.contains(transaction.original().state().type()))
            && (finalBlocks.isEmpty() || finalBlocks.contains(transaction.finalReplacement().state().type()))
            && (!context.lookup(blockLocation.get()))) {
          transaction.invalidate();
          context.report(SettingEventReport.restricted()
              .target(transaction.original().state().type().key(RegistryTypes.BLOCK_TYPE).formatted())
              .build());
        }
      }
    }
  }
}