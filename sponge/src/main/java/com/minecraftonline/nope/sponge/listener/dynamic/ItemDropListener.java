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

import com.minecraftonline.nope.sponge.api.event.SettingEventListener;
import com.minecraftonline.nope.sponge.api.event.SettingValueLookupFunction;
import com.minecraftonline.nope.sponge.util.Formatter;
import java.util.Optional;
import net.kyori.adventure.audience.Audience;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.world.Locatable;

public abstract class ItemDropListener<E extends Event>
    implements SettingEventListener<Boolean, E> {

  public static class DropItemPre extends ItemDropListener<DropItemEvent.Pre> {
    @Override
    public void handle(DropItemEvent.Pre event, SettingValueLookupFunction<Boolean> lookupFunction) {
      if (!(event.source() instanceof Locatable)) {
        return;
      }
      Locatable locatable = (Locatable) event.source();
      Optional<Carrier> carrier = event.cause().first(Carrier.class);
      if (!lookupFunction.lookup(locatable, locatable.serverLocation())) {
        event.droppedItems().clear();
        carrier.ifPresent(c -> event.originalDroppedItems().forEach(snapshot -> {
          InventoryTransactionResult result = c.inventory().offer(snapshot.createStack());
          if (result.type() != InventoryTransactionResult.Type.SUCCESS && c instanceof Audience) {
            ((Audience) c).sendMessage(Formatter.error("You cannot drop items here and "
                + "there was a problem trying to return your item(s) to you"));
          }
        }));
        // TODO schedule this for later because someone else may have re-added the items
        //  that we removed here, so we shouldn't be giving them back.
//        carrier.ifPresent(c -> Sponge.server().scheduler().submit(Task.builder()
//            .plugin(SpongeNope.instance().pluginContainer())
//            .delay(Ticks.of(1))
//            .execute(task -> {
//              List<ItemStackSnapshot> snapshots = new LinkedList<>(event.originalDroppedItems());
//              snapshots.removeAll(event.droppedItems());
//              snapshots.forEach(snapshot -> {
//                InventoryTransactionResult result = c.inventory().offer(snapshot.createStack());
//                if (result.type() != InventoryTransactionResult.Type.SUCCESS && c instanceof Audience) {
//                  ((Audience) c).sendMessage(Formatter.error("You cannot drop items here and "
//                      + "there was a problem trying to return your item(s) to you"));
//                }
//              });
//            })
//            .build()));
      }
    }
  }

}
