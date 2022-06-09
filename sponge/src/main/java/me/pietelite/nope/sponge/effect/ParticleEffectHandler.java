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

package me.pietelite.nope.sponge.effect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.util.EffectsUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

/**
 * State handler for particle effects.
 */
public class ParticleEffectHandler {

  public static long DEFAULT_DURATION = 3000;
  /**
   * A map where the keys are volume uuids and the value is another map.
   * The second map has a key of player uuids and a value of an integer count
   * of how many instances using that volume and player have been added.
   */
  private final Map<UUID, Map<UUID, Integer>> volumeToPlayers = new HashMap<>();
  private final LinkedList<RegisteredVolume> volumeQueue = new LinkedList<>();
  private ScheduledTask task;

  public void show(Scene scene, ServerPlayer player) {
    scene.volumes().forEach(v -> show(v, player));
  }

  public void show(Scene scene, ServerPlayer player, long duration) {
    scene.volumes().forEach(v -> show(v, player.uniqueId(), duration));
  }

  /**
   * Show a volume to a player in-game for a default amount of time.
   *
   * @param volume the volume
   * @param player the player
   */
  public void show(Volume volume, ServerPlayer player) {
    show(volume, player.uniqueId(), DEFAULT_DURATION);
  }

  /**
   * Show a volume to a player in-game for a specific amount of time.
   *
   * @param volume     the volume
   * @param playerUuid the uuid of the player
   * @param duration   the amount of time for which to show the volume, in milliseconds
   */
  public void show(Volume volume, UUID playerUuid, long duration) {
    RegisteredVolume registeredVolume = new RegisteredVolume();
    registeredVolume.volume = volume;
    registeredVolume.playerUuid = playerUuid;
    registeredVolume.expiry = System.currentTimeMillis() + duration;
    volumeQueue.add(registeredVolume);
    volumeToPlayers.computeIfAbsent(volume.uuid(), k -> new HashMap<>())
        .compute(playerUuid, (uuid, integer) -> {
          if (integer == null) {
            return 1;
          } else {
            return integer + 1;
          }
        });
  }

  /**
   * Initialize the process that creates the animations.
   */
  public void initialize() {
    task = Sponge.server().scheduler().submit(Task.builder()
            .plugin(SpongeNope.instance().pluginContainer())
            .execute(() -> {
              long currentTime = System.currentTimeMillis();
              Nope.instance().interactiveVolumeHandler().volumes().forEach((playerUuid, volume) ->
                  show(volume, playerUuid, 0));
              Iterator<RegisteredVolume> volumesIterator = volumeQueue.iterator();
              while (volumesIterator.hasNext()) {
                RegisteredVolume registeredVolume = volumesIterator.next();
                Map<UUID, Integer> players = volumeToPlayers.get(registeredVolume.volume.uuid());
                int count = players.get(registeredVolume.playerUuid);
                // remove this from the queue if it has expired or
                // if there has been another item added to the queue with the same volume and player
                boolean remove = registeredVolume.expiry < currentTime
                    || count > 1
                    || registeredVolume.volume.expired();
                if (remove) {
                  volumesIterator.remove();
                  if (count == 1) {
                    players.remove(registeredVolume.playerUuid);
                    if (players.isEmpty()) {
                      volumeToPlayers.remove(registeredVolume.volume.uuid());
                    }
                  } else {
                    players.put(registeredVolume.playerUuid, count - 1);
                  }
                } else {
                  Sponge.server().player(registeredVolume.playerUuid).ifPresent(serverPlayer ->
                      EffectsUtil.ripple(registeredVolume.volume, serverPlayer, EffectsUtil.defaultInfo()));
                }
              }
            }).interval(Ticks.of(20)).build(),
        "Nope Volume Particle Effect Daemon");
  }

  /**
   * Turn off the animation process.
   */
  public void shutDown() {
    if (task != null) {
      task.cancel();
    }
  }

  static class RegisteredVolume {
    Volume volume;
    UUID playerUuid;
    long expiry;
  }

}
