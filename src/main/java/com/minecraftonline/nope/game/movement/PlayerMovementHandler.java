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

package com.minecraftonline.nope.game.movement;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.setting.SettingLibrary;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PlayerMovementHandler {

  private static final long POLL_INTERVAL_MILLISECONDS = 250;
  private static final long MESSAGE_COOLDOWN_MILLISECONDS = 1000;

  private Map<UUID, Vector3i> positions = Maps.newConcurrentMap();
  private Map<UUID, UUID> worldLocations = Maps.newConcurrentMap();
  private Map<UUID, UUID> tasks = Maps.newConcurrentMap();
  private Map<UUID, Long> messageTime = Maps.newConcurrentMap();

  public void register() {
    Sponge.getEventManager().registerListeners(Nope.getInstance(), this);
  }

  private Location<World> getPreviousLocation(UUID playerUuid) {
    return new Location<>(Sponge.getServer()
        .getWorld(worldLocations.get(playerUuid))
        .orElseThrow(() ->
            new RuntimeException("World could not be found in Movement Handler")),
        positions.get(playerUuid));
  }

  @Listener
  public void onJoin(ClientConnectionEvent.Join event) {
    UUID uuid = event.getTargetEntity().getUniqueId();

    /* Location cache */
    positions.put(uuid, event.getTargetEntity().getLocation().getBlockPosition());
    worldLocations.put(uuid, event.getTargetEntity().getLocation().getExtent().getUniqueId());

    /* Tasks */
    tasks.put(uuid,
        Sponge.getScheduler().createTaskBuilder()
            .interval(POLL_INTERVAL_MILLISECONDS, TimeUnit.MILLISECONDS)
            .execute(() -> {
              Player player = Sponge.getServer()
                  .getPlayer(uuid).orElseThrow(() ->
                      new RuntimeException("Player cannot be found in Movement Handler. Was the player properly flushed?"));
              tryPassThreshold(player,
                  getPreviousLocation(player.getUniqueId()),
                  player.getLocation(),
                  false,
                  cancelled -> {
                    if (cancelled) {
                      player.setLocation(getPreviousLocation(uuid));
                    } else {
                      positions.put(uuid, player.getLocation().getBlockPosition());
                      worldLocations.put(uuid, player.getLocation().getExtent().getUniqueId());
                    }
                  });
            }).submit(Nope.getInstance())
            .getUniqueId());

    /* Messaging time cache */
    messageTime.put(uuid, System.currentTimeMillis());
  }

  @Listener
  public void onLeave(ClientConnectionEvent.Disconnect event) {
    /* Location cache */
    positions.remove(event.getTargetEntity().getUniqueId());
    worldLocations.remove(event.getTargetEntity().getUniqueId());

    /* Tasks */
    UUID taskUuid = tasks.remove(event.getTargetEntity().getUniqueId());
    if (taskUuid == null) {
      throw new RuntimeException("Player could not be removed from the Movement Handler properly.");
    }
    Sponge.getScheduler().getTaskById(taskUuid).ifPresent(Task::cancel);

    /* Messaging time cache */
    messageTime.remove(event.getTargetEntity().getUniqueId());
  }

  public void tryPassThreshold(Player player,
                               Location<World> first,
                               Location<World> last,
                               boolean translate,
                               Consumer<Boolean> canceller) {
    List<Host> exiting = Nope.getInstance()
        .getHostTree()
        .getContainingHosts(first);
    List<Host> entering = Nope.getInstance()
        .getHostTree()
        .getContainingHosts(last);
    Set<Host> unchanged = Sets.newHashSet(exiting);
    unchanged.retainAll(entering);
    exiting.removeAll(unchanged);
    entering.removeAll(unchanged);

    exiting.sort(Comparator.comparing(Host::getPriority));
    entering.sort(Comparator.comparing(Host::getPriority));

    boolean cancel = false;
    boolean messaged = false;

    /* Find applicable values for exiting or entering */
    SettingLibrary.Movement movementData;
    Text message;
    Text title;
    Text subtitle;
    boolean canMessage = messageTime.get(player.getUniqueId())
        + MESSAGE_COOLDOWN_MILLISECONDS < System.currentTimeMillis();

    /* Exiting */
    for (int i = exiting.size() - 1; i >= 0; i--) {
      movementData = exiting.get(i).getData(SettingLibrary.EXIT, player);
      if (movementData.equals(SettingLibrary.Movement.NONE)
          || (movementData.equals(SettingLibrary.Movement.UNNATURAL) && translate)) {
        cancel = true;
        message = exiting.get(i).getData(SettingLibrary.EXIT_DENY_MESSAGE, player);
        title = exiting.get(i).getData(SettingLibrary.EXIT_DENY_TITLE, player);
        subtitle = exiting.get(i).getData(SettingLibrary.EXIT_DENY_SUBTITLE, player);
      } else {
        message = exiting.get(i).getData(SettingLibrary.FAREWELL, player);
        title = exiting.get(i).getData(SettingLibrary.FAREWELL_TITLE, player);
        subtitle = exiting.get(i).getData(SettingLibrary.FAREWELL_SUBTITLE, player);
      }
      if (!message.isEmpty() && canMessage) {
        player.sendMessage(message);
        messaged = true;
      }
      if ((!title.isEmpty() || !subtitle.isEmpty()) && canMessage) {
        player.sendTitle(Title.builder().title(title).subtitle(subtitle).build());
        messaged = true;
      }
    }

    /* Entering */
    for (int i = entering.size() - 1; i >= 0; i--) {
      movementData = entering.get(i).getData(SettingLibrary.ENTRY, player);
      if (movementData.equals(SettingLibrary.Movement.NONE)
          || (movementData.equals(SettingLibrary.Movement.UNNATURAL) && translate)) {
        cancel = true;
        message = entering.get(i).getData(SettingLibrary.ENTRY_DENY_MESSAGE, player);
        title = entering.get(i).getData(SettingLibrary.ENTRY_DENY_TITLE, player);
        subtitle = entering.get(i).getData(SettingLibrary.ENTRY_DENY_SUBTITLE, player);
      } else {
        message = entering.get(i).getData(SettingLibrary.GREETING, player);
        title = entering.get(i).getData(SettingLibrary.GREETING_TITLE, player);
        subtitle = entering.get(i).getData(SettingLibrary.GREETING_SUBTITLE, player);
      }
      if (!message.isEmpty() && canMessage) {
        player.sendMessage(message);
        messaged = true;
      }
      if ((!title.isEmpty() || !subtitle.isEmpty()) && canMessage) {
        player.sendTitle(Title.builder().title(title).subtitle(subtitle).build());
        messaged = true;
      }
    }

    /* Update message time (for reduced spamming) */
    if (messaged) {
      messageTime.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /* Perform cancellation behavior */
    canceller.accept(cancel);

  }
}
