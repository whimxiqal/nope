/*
 * MIT License
 *
 * Copyright (c) 2021 MinecraftOnline
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
 *
 */

package com.minecraftonline.nope.sponge.listener;

import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.sponge.util.Extra;
import javax.annotation.Nonnull;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.block.ChangeBlockEvent;

public class EntityBreakConditionSettingListener
    extends CancelConditionSettingListener<ChangeBlockEvent.Pre> {
  public EntityBreakConditionSettingListener(@Nonnull SettingKey<Boolean> key,
                                             @Nonnull EntityType entityType) {
    super(key,
        ChangeBlockEvent.Pre.class,
        event -> {
          for (Entity entity : event.cause().allOf(Entity.class)) {
            if (entity.type().equals(entityType)) {
              if (!SpongeNope.calc(key, entity.getLocation())) {
                return true;
              }
              event.getTransactions()
                  .stream()
                  .filter(Transaction::isValid)
                  .forEach(transaction ->
                      transaction.setValid(SpongeNope.getInstance()
                          .getHostTreeAdapter()
                          .lookupAnonymous(key, transaction.getFinal()
                              .getLocation()
                              .orElseThrow(Extra.noLocation(key,
                                  ChangeBlockEvent.Break.class,
                                  null)))));
            }
          }
          return false;
        });
  }
}
