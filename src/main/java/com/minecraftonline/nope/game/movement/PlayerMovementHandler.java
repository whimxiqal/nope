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
import lombok.Data;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PlayerMovementHandler {

  @Data
  private static class PlayerMovementData {
    private final UUID taskId;
    private Vector3i position;
    private UUID worldId;
    private long visualsTimeStamp;
    private String lastSentMessage;
    private boolean viewing;
    private boolean nextUnnaturalTeleportRestricted;
    private Predicate<MoveEntityEvent.Teleport> nextTeleportCanceller;

    PlayerMovementData(@Nonnull UUID taskId, @Nonnull Entity entity) {
      this.taskId = taskId;
      this.position = entity.getLocation().getBlockPosition();
      this.worldId = entity.getLocation().getExtent().getUniqueId();
      this.visualsTimeStamp = System.currentTimeMillis();
      this.lastSentMessage = "A fox flew farther than Florence";
      this.viewing = false;
      this.nextUnnaturalTeleportRestricted = false;
      this.nextTeleportCanceller = event -> false;
    }
  }

  private static final long POLL_INTERVAL_TICKS = 4;
  private static final long MESSAGE_COOLDOWN_MILLISECONDS = 1000;

  private final Map<UUID, PlayerMovementData> movementDataMap = Maps.newConcurrentMap();

  public void register() {
    Sponge.getEventManager().registerListeners(Nope.getInstance(), this);
  }

  public void updatePreviousLocation(Player player) {
    this.movementDataMap.get(player.getUniqueId()).setPosition(player.getLocation().getBlockPosition());
    this.movementDataMap.get(player.getUniqueId()).setWorldId(player.getLocation().getExtent().getUniqueId());
  }

  private Location<World> getPreviousLocation(UUID playerUuid) {
    return new Location<>(Sponge.getServer()
        .getWorld(movementDataMap.get(playerUuid).getWorldId())
        .orElseThrow(() ->
            new RuntimeException("World could not be found in Movement Handler")),
        movementDataMap.get(playerUuid).getPosition().toDouble().add(0.5, 0.5, 0.5));
  }

  public void addHostViewer(UUID playerUuid) {
    movementDataMap.get(playerUuid).setViewing(true);
  }

  public boolean isHostViewer(UUID playerUuid) {
    return movementDataMap.get(playerUuid).isViewing();
  }

  public void removeHostViewer(UUID playerUuid) {
    movementDataMap.get(playerUuid).setViewing(false);
  }

  public void restrictNextUnnaturalTeleport(UUID playerUuid, boolean restricted) {
    movementDataMap.get(playerUuid).setNextUnnaturalTeleportRestricted(restricted);
  }

  public boolean isNextUnnaturalTeleportRestricted(UUID playerUuid) {
    return movementDataMap.get(playerUuid).isNextUnnaturalTeleportRestricted();
  }

  public void cancelNextTeleport(UUID playerUuid, Predicate<MoveEntityEvent.Teleport> canceller) {
    movementDataMap.get(playerUuid).setNextTeleportCanceller(canceller);
  }

  public Predicate<MoveEntityEvent.Teleport> isNextTeleportCancelled(UUID playerUuid) {
    return movementDataMap.get(playerUuid).getNextTeleportCanceller();
  }

  @Listener
  public void onJoin(ClientConnectionEvent.Join event) {
    UUID uuid = event.getTargetEntity().getUniqueId();

    UUID taskId = Sponge.getScheduler().createTaskBuilder()
        .intervalTicks(POLL_INTERVAL_TICKS)
        .execute(() -> {
          Player player = Sponge.getServer()
              .getPlayer(uuid).orElseThrow(() ->
                  new RuntimeException("Player cannot be found in Movement Handler. Was the player properly flushed?"));
          Location<World> previousLocation = getPreviousLocation(player.getUniqueId());
          if (!previousLocation.getBlockPosition().equals(player.getLocation().getBlockPosition())
              || !previousLocation.getExtent().equals(player.getLocation().getExtent())) {
            if (isNextUnnaturalTeleportRestricted(player.getUniqueId())) {
              tryPassThreshold(player,
                  previousLocation,
                  player.getLocation(),
                  false,
                  cancelled -> {
                    if (cancelled) {
                      player.setLocation(getPreviousLocation(uuid));
                    }
                  });
              restrictNextUnnaturalTeleport(player.getUniqueId(), false);
            }
          }
          updatePreviousLocation(player);
        }).submit(Nope.getInstance())
        .getUniqueId();

    movementDataMap.put(uuid, new PlayerMovementData(taskId, event.getTargetEntity()));
  }

  @Listener
  public void onLeave(ClientConnectionEvent.Disconnect event) {
    UUID entityUuid = event.getTargetEntity().getUniqueId();

    /* Clean Up */
    UUID taskUuid = movementDataMap.get(entityUuid).getTaskId();
    if (taskUuid == null) {
      throw new RuntimeException("Player could not be removed from the Movement Handler properly.");
    }
    Sponge.getScheduler().getTaskById(taskUuid).ifPresent(Task::cancel);

    movementDataMap.remove(entityUuid);
  }

  public void tryPassThreshold(Player player,
                               Location<World> first,
                               Location<World> last,
                               boolean natural,
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

    /* Find these applicable values for exiting or entering */
    SettingLibrary.Movement movementData;
    Text message;
    Text title;
    Text subtitle;
    boolean expired = movementDataMap.get(player.getUniqueId()).getVisualsTimeStamp()
        + MESSAGE_COOLDOWN_MILLISECONDS < System.currentTimeMillis();
    String lastSentMessage = movementDataMap.get(player.getUniqueId()).getLastSentMessage();

    /* Exiting */
    for (int i = exiting.size() - 1; i >= 0; i--) {
      movementData = exiting.get(i).getData(SettingLibrary.EXIT, player);
      if (movementData.equals(SettingLibrary.Movement.NONE)
          || (movementData.equals(SettingLibrary.Movement.UNNATURAL) && natural)) {
        Nope.getInstance().getLogger().info("Cancelling...");
        Nope.getInstance().getLogger().info("Movement Data: " + movementData);
        Nope.getInstance().getLogger().info("Natural: " + natural);
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
        movementDataMap.get(player.getUniqueId()).setLastSentMessage(message.toPlain());
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
            || (movementData.equals(SettingLibrary.Movement.UNNATURAL) && natural)) {
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
          movementDataMap.get(player.getUniqueId()).setLastSentMessage(message.toPlain());
          visual = true;
        }
        if (!title.isEmpty() || !subtitle.isEmpty()) {
          player.sendTitle(Title.builder().title(title).subtitle(subtitle).build());
          visual = true;
        }

        if (entering.get(i) instanceof VolumeHost && isHostViewer(player.getUniqueId()) && expired) {
          EffectsUtil.showVolume((VolumeHost) entering.get(i), player, 5);
          visual = true;
        }
      }
    }

    /* Update message time (for reduced spamming) */
    if (visual) {
      movementDataMap.get(player.getUniqueId()).setVisualsTimeStamp(System.currentTimeMillis());
    }

    /* Perform cancellation behavior */

    // This is a quick fix for managing cancellation while riding vehicles
    if (cancel && player.getVehicle().isPresent()) {
      // Dismount so the even can be cancelled properly
      Entity vehicle = player.getVehicle().get();
      player.setVehicle(null);
      // Move the vehicle back to the player so the vehicle doesn't get stuck
      vehicle.setTransform(player.getTransform());
    }

    if (!cancel) {
      updatePreviousLocation(player);
    }

    canceller.accept(cancel);

  }

}
