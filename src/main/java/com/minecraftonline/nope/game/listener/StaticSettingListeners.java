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

package com.minecraftonline.nope.game.listener;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.setting.SettingLibrary;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

public final class StaticSettingListeners {

  public static void register() {
    Sponge.getEventManager().registerListeners(Nope.getInstance(), new StaticSettingListeners());
  }

  private StaticSettingListeners() {
  }

  /**
   * A static listener to handle every entity within a movement event
   *
   * @param event  the event
   * @param player the cause of movement
   */
  @Listener
  public void onMoveEntityChain(MoveEntityEvent.Position event, @First Player player) {
    // Run the threshold handler for every player on the vehicle stack
    LinkedList<Entity> entities = new LinkedList<>();
    entities.add(player.getBaseVehicle());
    AtomicReference<Entity> current = new AtomicReference<>();
    while (!entities.isEmpty()) {
      current.set(entities.pop());
      entities.addAll(current.get().getPassengers());
      if (current.get() instanceof Player) {
        Nope.getInstance().getPlayerMovementHandler().tryPassThreshold(
            (Player) current.get(),
            event.getFromTransform().getLocation(),
            event.getToTransform().getLocation(),
            true,
            cancelled -> {
              // Do both just to be sure
              if (cancelled) {
                event.setToTransform(event.getFromTransform());
              } else {
                Nope.getInstance()
                    .getPlayerMovementHandler()
                    .updatePreviousLocation(player);
              }
              event.setCancelled(cancelled);
            }
        );
        if (current.get().getVehicle().isPresent()
            && !Nope.getInstance().getHostTree().lookup(SettingLibrary.RIDE,
                (Player) current.get(),
                current.get().getLocation())) {
          current.get().setVehicle(null);
        }
      }
    }
  }

  @Listener
  public void onLeave(ClientConnectionEvent.Disconnect event) {
    Nope.getInstance().getCollisionHandler().loggedOut(event.getTargetEntity());
  }

}
