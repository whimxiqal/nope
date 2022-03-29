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
import me.pietelite.nope.common.setting.sets.ExplosiveSet;
import me.pietelite.nope.common.struct.AltSet;
import me.pietelite.nope.sponge.api.event.SettingEventContext;
import me.pietelite.nope.sponge.api.event.SettingEventListener;
import me.pietelite.nope.sponge.api.event.SettingEventReport;
import me.pietelite.nope.sponge.util.SpongeUtil;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.event.block.ChangeBlockEvent;

/**
 * Implementation of {@link me.pietelite.nope.common.setting.SettingKeys#DESTRUCTIVE_EXPLOSIVES},
 * specifically for handling the physical destruction of blocks due to explosions.
 */
public class DestructiveExplosivesBlockListener
    implements SettingEventListener<AltSet<ExplosiveSet.Explosive>, ChangeBlockEvent.All> {
  @Override
  public void handle(SettingEventContext<AltSet<ExplosiveSet.Explosive>, ChangeBlockEvent.All> context) {
    final Optional<Explosive> sourceExplosive = context.event().cause().first(Explosive.class);
    if (sourceExplosive.isPresent()) {
      final ExplosiveSet.Explosive explosive = SpongeUtil.reduceExplosive(sourceExplosive.get());
      for (BlockTransaction transaction : context.event().transactions()) {
        if (transaction.finalReplacement()
            .location()
            .map(loc -> !context.lookup(loc).contains(explosive))
            .orElse(false)) {
          transaction.invalidate();
          context.report(SettingEventReport.restricted()
              .source(context.event().source())
              .target(explosive.name())
              .build());
        }
      }
    }
  }
}