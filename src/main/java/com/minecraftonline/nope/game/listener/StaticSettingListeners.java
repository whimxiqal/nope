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
import com.minecraftonline.nope.game.movement.PlayerMovementHandler;
import com.minecraftonline.nope.setting.SettingLibrary;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class StaticSettingListeners {

  private final Map<UUID, Location<World>> lastTeleportFromLocations = new HashMap<>();
  private final Set<UUID> cancellingDuplicatesSet = new HashSet<>();

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
  @Listener(order = Order.EARLY)
  public void onMoveEntityChain(MoveEntityEvent.Position event, @First Player player) {
    // Run the threshold handler for every player on the vehicle stack
    LinkedList<Entity> entities = new LinkedList<>();
    entities.add(player.getBaseVehicle());
    AtomicReference<Entity> current = new AtomicReference<>();
    while (!entities.isEmpty()) {
      current.set(entities.pop());
      entities.addAll(current.get().getPassengers());
      if (current.get() instanceof Player) {
        if (Nope.getInstance().getPlayerMovementHandler().tryPassThreshold(
            (Player) current.get(),
            event.getFromTransform().getLocation(),
            event.getToTransform().getLocation(),
            true)) {
          // Do both just to be sure
          if (event.getFromTransform().getLocation().getY() > event.getToTransform().getLocation().getY()) {
            // The player's is likely falling and it was cancelled.
            //  They might be stuck! Manually cancel by moving them out and up.
            player.setLocation(event.getFromTransform().getLocation().add(0, 1.5, 0));
            event.setCancelled(false);
          } else {
            event.setToTransform(event.getFromTransform());
            event.setCancelled(true);
          }
        }
        if (current.get().getVehicle().isPresent()
            && !Nope.getInstance().getHostTree().lookup(SettingLibrary.RIDE,
            (Player) current.get(),
            current.get().getLocation())) {
          current.get().setVehicle(null);
        }
      }
    }
  }

  @Listener(order = Order.EARLY)
  public void onTeleport(MoveEntityEvent.Teleport event, @First Player player) {
    // Duplicates -- Consecutive teleports from the same location
    if (cancellingDuplicatesSet.contains(player.getUniqueId())
        && lastTeleportFromLocations.containsKey(player.getUniqueId())
        && event.getFromTransform().getLocation().equals(lastTeleportFromLocations.get(player.getUniqueId()))) {
      // Duplicate elimination
      event.setToTransform(event.getFromTransform());
      event.setCancelled(true);
    } else {
      // Not a duplicate
      PlayerMovementHandler handler = Nope.getInstance().getPlayerMovementHandler();
      if (handler.resolveTeleportCancellation(player.getUniqueId(), event)) {
        // Cancel because it was manually requested by something else
        event.setToTransform(event.getFromTransform());
        event.setCancelled(true);
      }
      // Cancel future duplicate teleport events
      cancellingDuplicatesSet.add(player.getUniqueId());
      lastTeleportFromLocations.put(player.getUniqueId(), event.getFromTransform().getLocation());
      // Allow future duplicates in 20 ticks
      Sponge.getScheduler().createTaskBuilder()
          .delayTicks(20)
          .execute(() -> cancellingDuplicatesSet.remove(player.getUniqueId()))
          .submit(Nope.getInstance());
    }

  }

  @Listener(order = Order.EARLY)
  public void onSendCommand(SendCommandEvent event, @First Player player) {
    for (String command : Nope.getInstance()
        .getHostTree()
        .lookup(SettingLibrary.MOVEMENT_COMMANDS, player, player.getLocation())) {
      String substring = event.getCommand().substring(0, Math.min(event.getCommand().length(), command.length()));
      if (substring.equalsIgnoreCase(command)) {
        Nope.getInstance()
            .getPlayerMovementHandler()
            .cancelNextTeleportIf(player.getUniqueId(),
                teleportEvent ->
                    Nope.getInstance().getPlayerMovementHandler().tryPassThreshold(player,
                        teleportEvent.getFromTransform().getLocation(),
                        teleportEvent.getToTransform().getLocation(),
                        false),
                10000);
      }
    }
  }

  @Listener
  public void onJoin(ClientConnectionEvent.Join event) {
    Nope.getInstance().getPlayerMovementHandler().logIn(event.getTargetEntity().getUniqueId());
  }

  @Listener
  public void onLeave(ClientConnectionEvent.Disconnect event) {
    Nope.getInstance().getCollisionHandler().logOut(event.getTargetEntity());
    Nope.getInstance().getPlayerMovementHandler().logOut(event.getTargetEntity().getUniqueId());
  }

}
