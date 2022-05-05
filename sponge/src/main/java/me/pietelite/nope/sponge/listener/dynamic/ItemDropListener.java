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
import me.pietelite.nope.sponge.api.setting.SettingEventContext;
import me.pietelite.nope.sponge.api.setting.SettingEventListener;
import me.pietelite.nope.sponge.util.Formatter;
import net.kyori.adventure.audience.Audience;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.world.Locatable;

/**
 * Implements {@link me.pietelite.nope.common.setting.SettingKeys#ITEM_DROP}.
 */
public class ItemDropListener implements SettingEventListener<Boolean, DropItemEvent.Pre> {
  @Override
  public void handle(SettingEventContext<Boolean, DropItemEvent.Pre> context) {
    if (!(context.event().source() instanceof Locatable)) {
      return;
    }
    Locatable locatable = (Locatable) context.event().source();
    Optional<Carrier> carrier = context.event().cause().first(Carrier.class);
    if (!context.lookup(locatable, locatable.serverLocation())) {
      context.event().droppedItems().clear();
      carrier.ifPresent(c -> context.event().originalDroppedItems().forEach(snapshot -> {
        InventoryTransactionResult result = c.inventory().offer(snapshot.createStack());
        if (result.type() != InventoryTransactionResult.Type.SUCCESS && c instanceof Audience) {
          ((Audience) c).sendMessage(Formatter.error("You cannot drop items here and "
              + "there was a problem trying to return your item(s) to you"));
        }
      }));
      // TODO schedule this for later because someone else may have re-added the items
      //  that we removed here, so we shouldn't be giving them back.
      /*
        carrier.ifPresent(c -> Sponge.server().scheduler().submit(Task.builder()
            .plugin(SpongeNope.instance().pluginContainer())
            .delay(Ticks.of(1))
            .execute(task -> {
              List<ItemStackSnapshot> snapshots = new LinkedList<>(event.originalDroppedItems());
              snapshots.removeAll(event.droppedItems());
              snapshots.forEach(snapshot -> {
                InventoryTransactionResult result = c.inventory().offer(snapshot.createStack());
                if (result.type() != InventoryTransactionResult.Type.SUCCESS && c instanceof Audience) {
                  ((Audience) c).sendMessage(Formatter.error("You cannot drop items here and "
                      + "there was a problem trying to return your item(s) to you"));
                }
              });
            })
            .build()));
*/
    }
  }
}
