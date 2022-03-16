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
import java.util.Random;
import me.pietelite.nope.sponge.api.event.SettingEventContext;
import me.pietelite.nope.sponge.api.event.SettingEventListener;
import me.pietelite.nope.sponge.api.event.SettingEventReport;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.server.ServerLocation;

public class FireEffectListener implements SettingEventListener<Boolean, ChangeBlockEvent.All> {

  private final Random random = new Random();

  @Override
  public void handle(SettingEventContext<Boolean, ChangeBlockEvent.All> context) {
    Optional<ServerLocation> location;
    final Object eventCause = context.event().cause().root();
    if (eventCause instanceof LocatableBlock) {
      LocatableBlock block = (LocatableBlock) eventCause;
      for (BlockTransaction transaction : context.event().transactions()) {
        boolean fireAging = false;
        if (block.blockState().type().equals(BlockTypes.FIRE.get())) {
          // It's caused by fire
          if (transaction.original().state().type().equals(BlockTypes.FIRE.get())) {
            if (transaction.finalReplacement().state().type().equals(BlockTypes.FIRE.get())) {
              // The fire isn't moving, it's just getting older
              fireAging = true;

              // TODO uncomment this once IntegerStateProperties are registered appropariately in Sponge.
              //  This stuff makes it so that fire dies out in due time if Fire Effect is off
//              // See if we should just manually extinguish
//              //  (it shouldn't be going above age 3 if 'fire effect' is off because
//              //  0 fire effect means not living long enough to try to burn it)
//              Optional<Integer> age = transaction.finalReplacement().state().stateProperty(IntegerStateProperties.FIRE_AGE);
//              if (age.isPresent() && age.get() >= 3 /*&& random.nextDouble() > .5*/) {
//                transaction.setCustom(BlockSnapshot.builder()
//                    .blockState(BlockState.builder()
//                        .blockType(BlockTypes.AIR)
//                        .build())
//                    .build());
//                continue;
//              }
            } else if (transaction.finalReplacement().state().type().equals(BlockTypes.AIR.get())) {
              // Fire just extinguished itself, go ahead
              continue;
            }
          }
          // Otherwise, just invalidate the transaction (unless it's just the fire aging itself)
          if (!fireAging && !context.lookup(null, block.serverLocation())) {
            transaction.invalidate();
            context.report(SettingEventReport.restricted().build());
          }
        }
      }
    }
  }
}