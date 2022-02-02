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

import com.minecraftonline.nope.common.setting.sets.ExplosiveSet;
import com.minecraftonline.nope.common.struct.AltSet;
import com.minecraftonline.nope.sponge.api.event.SettingEventListener;
import com.minecraftonline.nope.sponge.api.event.SettingValueLookupFunction;
import com.minecraftonline.nope.sponge.util.SpongeUtil;
import java.util.Optional;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.world.explosion.Explosion;

public class DestructiveExplosivesExplosionListener implements SettingEventListener<AltSet<ExplosiveSet.Explosive>, ExplosionEvent.Pre> {
  @Override
  public void handle(ExplosionEvent.Pre event,
                     SettingValueLookupFunction<AltSet<ExplosiveSet.Explosive>> lookupFunction) {
    final Object rootCause = event.cause().root();
    final Optional<Explosive> sourceExplosive = event.explosion().sourceExplosive();
    if (sourceExplosive.isPresent()) {
      final ExplosiveSet.Explosive explosive = SpongeUtil.reduceExplosive(sourceExplosive.get());
      if (!lookupFunction.lookup(rootCause, sourceExplosive.get().serverLocation()).contains(explosive)) {
        event.setExplosion(Explosion.builder()
            .from(event.explosion())
            .shouldBreakBlocks(false)
            .build());
      }
    }
  }
}
