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
import com.minecraftonline.nope.host.VolumeHost;
import com.minecraftonline.nope.setting.SettingLibrary;
import com.minecraftonline.nope.util.EffectsUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PlayerMovementHandler {

  private static final long POLL_INTERVAL_TICKS = 4;
  private static final long MESSAGE_COOLDOWN_MILLISECONDS = 1000;

  private final Map<UUID, Vector3i> positions = Maps.newConcurrentMap();
  private final Map<UUID, UUID> worldLocations = Maps.newConcurrentMap();
  private final Map<UUID, UUID> tasks = Maps.newConcurrentMap();
  private final Map<UUID, Long> visualsTimer = Maps.newConcurrentMap();
  private final Map<UUID, String> lastSentMessages = Maps.newConcurrentMap();

  private final Set<UUID> viewers = ConcurrentHashMap.newKeySet();

  public void register() {
    Sponge.getEventManager().registerListeners(Nope.getInstance(), this);
  }

  public void updatePreviousLocation(Player player) {
    this.positions.put(player.getUniqueId(), player.getLocation().getBlockPosition());
    this.worldLocations.put(player.getUniqueId(), player.getLocation().getExtent().getUniqueId());
  }

  private Location<World> getPreviousLocation(UUID playerUuid) {
    return new Location<>(Sponge.getServer()
        .getWorld(worldLocations.get(playerUuid))
        .orElseThrow(() ->
            new RuntimeException("World could not be found in Movement Handler")),
        positions.get(playerUuid).toDouble().add(0.5, 0.5, 0.5));
  }

  public boolean addHostViewer(UUID playerUuid) {
    return viewers.add(playerUuid);
  }

  public boolean isHostViewer(UUID playerUuid) {
    return viewers.contains(playerUuid);
  }

  public boolean removeHostViewer(UUID playerUuid) {
    return viewers.remove(playerUuid);
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
            .intervalTicks(POLL_INTERVAL_TICKS)
            .execute(() -> {
              Player player = Sponge.getServer()
                  .getPlayer(uuid).orElseThrow(() ->
                      new RuntimeException("Player cannot be found in Movement Handler. Was the player properly flushed?"));
              Location<World> previousLocation = getPreviousLocation(player.getUniqueId());
              if (!previousLocation.getBlockPosition().equals(player.getLocation().getBlockPosition())
                  || !previousLocation.getExtent().equals(player.getLocation().getExtent())) {
                tryPassThreshold(player,
                    previousLocation,
                    player.getLocation(),
                    false,
                    cancelled -> {
                      if (cancelled) {
                        player.setLocation(getPreviousLocation(uuid));
                      }
                    });
              }
              updatePreviousLocation(player);
            }).submit(Nope.getInstance())
            .getUniqueId());

    /* Player caches */
    visualsTimer.put(uuid, System.currentTimeMillis());
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

    /* Player caches */
    visualsTimer.remove(event.getTargetEntity().getUniqueId());
    lastSentMessages.remove(event.getTargetEntity().getUniqueId());
  }

  public void tryPassThreshold(Player player,
                               Location<World> first,
                               Location<World> last,
                               boolean translate,
                               Consumer<Boolean> canceller) {
    List<Host> exiting = new LinkedList<>(Nope.getInstance()
        .getHostTree()
        .getContainingHosts(first));
    List<Host> entering = new LinkedList<>(Nope.getInstance()
        .getHostTree()
        .getContainingHosts(last));
    Set<Host> unchanged = Sets.newHashSet(exiting);
    unchanged.retainAll(entering);
    exiting.removeAll(unchanged);
    entering.removeAll(unchanged);

    /* Call it quits if we aren't moving anywhere special */
    if (exiting.isEmpty() && entering.isEmpty()) {
      canceller.accept(false);
      return;
    }

    // Ignore sorting by priority for now, it doesn't really matter
//    exiting.sort(Comparator.comparing(Host::getPriority));
//    entering.sort(Comparator.comparing(Host::getPriority));

    boolean cancel = false;
    boolean visual = false;

    /* Find applicable values for exiting or entering */
    SettingLibrary.Movement movementData;
    Text message;
    Text title;
    Text subtitle;
    boolean expired = visualsTimer.get(player.getUniqueId())
        + MESSAGE_COOLDOWN_MILLISECONDS < System.currentTimeMillis();
    String lastSentMessage = lastSentMessages.getOrDefault(player.getUniqueId(), "");

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

      if (!message.isEmpty() && (expired || !lastSentMessage.equals(message.toPlain()))) {
        player.sendMessage(message);
        lastSentMessages.put(player.getUniqueId(), message.toPlain());
        visual = true;
      }
      if (!title.isEmpty() || !subtitle.isEmpty()) {
        player.sendTitle(Title.builder().title(title).subtitle(subtitle).build());
        visual = true;
      }

      if (exiting.get(i) instanceof VolumeHost && isHostViewer(player.getUniqueId()) && expired) {
        EffectsUtil.showVolume((VolumeHost) exiting.get(i), player, 5);
        visual = true;
      }
    }

    /* Entering */
    if (!cancel) {  // Only entering if we could exit from before
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
        if (!message.isEmpty() && (expired || !lastSentMessage.equals(message.toPlain()))) {
          player.sendMessage(message);
          lastSentMessages.put(player.getUniqueId(), message.toPlain());
          visual = true;
        }
        if (!title.isEmpty() || !subtitle.isEmpty()) {
          player.sendTitle(Title.builder().title(title).subtitle(subtitle).build());
          visual = true;
        }

        if (entering.get(i) instanceof VolumeHost && isHostViewer(player.getUniqueId())) {
          EffectsUtil.showVolume((VolumeHost) entering.get(i), player, 5);
          visual = true;
        }
      }
    }

    /* Update message time (for reduced spamming) */
    if (visual) {
      visualsTimer.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /* Perform cancellation behavior */

    // This is a quick fix for managing vehicles
    if (cancel && player.getVehicle().isPresent()) {
      // Dismount so the even can be cancelled properly
      Entity vehicle = player.getVehicle().get();
      player.setVehicle(null);
      // Move the vehicle back to the player so the vehicle doesn't get stuck
      vehicle.setTransform(player.getTransform());
    }
    canceller.accept(cancel);

  }

}
