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

package com.minecraftonline.nope.sponge.movement;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.host.VolumeHost;
import com.minecraftonline.nope.common.setting.SettingLibrary;
import com.minecraftonline.nope.sponge.util.EffectsUtil;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A handler for player movement as it pertains to Nope's behavior and control.
 */
public class PlayerMovementHandler {

  private static final long MESSAGE_COOLDOWN_MILLISECONDS = 1000;
  private final Map<UUID, PlayerMovementData> movementDataMap = Maps.newConcurrentMap();

  public void addHostViewer(UUID playerUuid) {
    movementDataMap.get(playerUuid).setViewing(true);
  }

  public boolean isHostViewer(UUID playerUuid) {
    return movementDataMap.get(playerUuid).isViewing();
  }

  public void removeHostViewer(UUID playerUuid) {
    movementDataMap.get(playerUuid).setViewing(false);
  }

  /**
   * Set up cancellation of the next teleport, only if the canceller test
   * is true. Whether or not the canceller resolves true or false,
   * the subsequent teleport is allowed without any checks.
   * (Only one check on one teleport event is made.)
   *
   * @param playerUuid    the uuid of the player
   * @param canceller     the cancellation test
   * @param timeoutMillis the timeout in milliseconds, after which the
   *                      any cancellation behavior is ignored (used in case
   *                      an expected teleport event actually is never thrown
   *                      for whatever reason)
   */
  public void cancelNextTeleportIf(@Nonnull UUID playerUuid,
                                   @Nonnull Predicate<MoveEntityEvent.Teleport> canceller,
                                   int timeoutMillis) {
    PlayerMovementData data = movementDataMap.get(playerUuid);
    data.setNextTeleportVerificationNeeded(true);
    data.setNextTeleportCanceller(canceller);
    data.setNextTeleportCancellationExpiry(System.currentTimeMillis() + timeoutMillis);
  }

  /**
   * Identify if this teleport event should be cancelled based on
   * previous set cancellation behavior. If special behavior is needed,
   * it also prepares the user's system for the next event.
   *
   * @param playerUuid the player uuid
   * @param event      the event that was thrown
   * @return true if it should be cancelled
   */
  public boolean resolveTeleportCancellation(@Nonnull UUID playerUuid,
                                             @Nonnull MoveEntityEvent.Teleport event) {
    PlayerMovementData data = movementDataMap.get(playerUuid);
    if (!data.isNextTeleportVerificationNeeded()) {
      return false;
    }
    movementDataMap.get(playerUuid).setNextTeleportVerificationNeeded(false);
    if (System.currentTimeMillis() < data.getNextTeleportCancellationExpiry()) {
      return movementDataMap.get(playerUuid).nextTeleportCanceller.test(event);
    } else {
      return false;
    }
  }

  public void logIn(UUID playerUuid) {
    movementDataMap.put(playerUuid, new PlayerMovementData());
  }

  public void logOut(UUID playerUuid) {
    movementDataMap.remove(playerUuid);
  }

  /**
   * Attempt to pass a player from the first location to the second location.
   *
   * @param player  the player to move
   * @param first   the first location
   * @param last    the last location
   * @param natural whether the movement is considered natural
   * @return true if the player may move
   */
  public boolean tryPassThreshold(Player player,
                                  Location<World> first,
                                  Location<World> last,
                                  boolean natural) {
    List<Host> exiting = new LinkedList<>(SpongeNope.getInstance()
        .getHostTreeAdapter()
        .getContainingHosts(first));
    List<Host> entering = new LinkedList<>(SpongeNope.getInstance()
        .getHostTreeAdapter()
        .getContainingHosts(last));
    Set<Host> unchanged = Sets.newHashSet(exiting);
    unchanged.retainAll(entering);
    exiting.removeAll(unchanged);
    entering.removeAll(unchanged);

    /* Call it quits if we aren't moving anywhere special */
    if (exiting.isEmpty() && entering.isEmpty()) {
      return false;
    }

    // Ignore sorting by priority for now, it doesn't really matter

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
          || (movementData.equals(SettingLibrary.Movement.NATURAL) && !natural)
          || (movementData.equals(SettingLibrary.Movement.UNNATURAL) && natural)) {
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
            || (movementData.equals(SettingLibrary.Movement.NATURAL) && !natural)
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

        if (entering.get(i) instanceof VolumeHost
            && isHostViewer(player.getUniqueId()) && expired) {
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

    return cancel;
  }

  @Data
  @NoArgsConstructor
  private static class PlayerMovementData {
    private long visualsTimeStamp = System.currentTimeMillis();
    private String lastSentMessage = "Few foxes fly farther than Florence";
    private boolean viewing = false;
    private boolean nextTeleportVerificationNeeded = false;
    private Predicate<MoveEntityEvent.Teleport> nextTeleportCanceller = event -> false;
    private long nextTeleportCancellationExpiry = System.currentTimeMillis();
  }

}
