/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) 2021 Pieter Svenson
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package com.minecraftonline.nope.sponge.util;

import com.minecraftonline.nope.common.setting.SettingKey;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.AttackEntityEvent;

public final class EventUtil {

  private EventUtil() {
  }

  public static boolean shouldCancelEntityAttackingEntity(
      SettingKey<Boolean, ?, ?> key,
      Class<? extends Entity> sourceClass,
      Class<? extends Entity> sinkClass,
      AttackEntityEvent event,
      boolean sourceControlled) {

    if (!sinkClass.isInstance(event.entity())) {
      return false;
    }
    Entity sink = sinkClass.cast(event.entity());
    Entity source = event.cause().first(EntityDamageSource.class)
        .map(EntityDamageSource::source)
        .filter(sourceClass::isInstance)
        .orElse(event.cause().first(IndirectEntityDamageSource.class)
            .map(IndirectEntityDamageSource::indirectSource)
            .filter(sourceClass::isInstance)
            .orElse(null));
    if (source == null) {
      return false;
    }
    return !SpongeUtil.valueFor(key,
        sourceControlled ? source : sink,
        source.serverLocation())
        || !SpongeUtil.valueFor(key,
        sourceControlled ? source : sink,
        sink.serverLocation());
  }

}
