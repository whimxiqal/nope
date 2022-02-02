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

package com.minecraftonline.nope.sponge.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.minecraftonline.nope.common.host.Zone;
import com.minecraftonline.nope.common.math.Vector3d;
import com.minecraftonline.nope.common.math.Volume;
import com.minecraftonline.nope.common.struct.Location;
import com.minecraftonline.nope.sponge.SpongeNope;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.experimental.Accessors;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.ScheduledTaskFuture;
import org.spongepowered.api.scheduler.TaskExecutorService;

/**
 * Utility class for minecraft effects.
 */
public final class EffectsUtil {

  public static final ParticleEffect BOUNDARY_PARTICLE = ParticleEffect.builder().type(ParticleTypes.COMPOSTER.get())
      .quantity(1)
      .build();
  /**
   * Number of interior boundary particles per period of allowing particles to go through.
   */
  public static final int INTERIOR_BOUNDARY_LIKELIHOOD = 8;
  public static final ParticleEffect INTERIOR_BOUNDARY_PARTICLE = ParticleEffect.builder().type(ParticleTypes.SMOKE.get())
      .quantity(1)
      .build();
  /**
   * The maximum number of volumes for which we will alter the particles in intersecting regions.
   */
  public static final int MAX_VOLUME_INTERSECTION_COMPARISON_COUNT = 8;
  /**
   * How many particles to display linearly per block distance.
   */
  public static final int PARTICLE_DENSITY = 5;
  /**
   * Maximum distance from player to display particles.
   */
  public static final int PARTICLE_PROXIMITY = 20;
  /**
   * How many times to repeat ripple animation.
   */
  public static final int PARTICLE_REPEAT_COUNT = 16;
  /**
   * (Millisecond) period to repeat ripple animation.
   */
  public static final int PARTICLE_REPEAT_PERIOD = 800;
  /**
   * Total bundles of particles per animation, each scheduling its own animations.
   */
  public static final int PARTICLE_SCHEDULE_BUNDLES = 100;
  /**
   * Multiplier on particle spawn time delay.
   */
  public static final int PARTICLE_SPREAD_DELAY = 120;
  private static final Table<Volume, Player, TaskGroup> taskGroups = HashBasedTable.create();
  private static TaskExecutorService VOLUME_PARTICLE_TASK_EXECUTOR;

  private static void ensureTaskExecutor() {
    if (VOLUME_PARTICLE_TASK_EXECUTOR == null) {
      VOLUME_PARTICLE_TASK_EXECUTOR = Sponge.asyncScheduler()
          .executor(SpongeNope.instance().pluginContainer());
    }
  }

  public static boolean show(Zone zone, Player player) {
    ensureTaskExecutor();

    Location location = SpongeUtil.reduceLocation(player.serverLocation());
    Vector3d locationVector = location.vector3d();
    List<Volume> volumes = zone.volumes();

    final Map<Volume, List<Vector3d>> points = new HashMap<>();
    for (Volume volume : volumes) {
      if (!volume.domain().equals(location.domain())) {
        continue;
      }

      List<Vector3d> surfacePoints = volume.surfacePointsNear(
          locationVector,
          PARTICLE_PROXIMITY,
          PARTICLE_DENSITY);
      if (!surfacePoints.isEmpty()) {
        points.put(volume, surfacePoints);
      }
    }

    if (points.size() > MAX_VOLUME_INTERSECTION_COMPARISON_COUNT) {
      for (Volume volume : points.keySet()) {
        // Ensure only one animation is happening per user per volume
        if (taskGroups.contains(volume, player)) {
          taskGroups.get(volume, player).cancel();
        }
        TaskGroup taskGroup = new TaskGroup();
        taskGroups.put(volume, player, taskGroup);
        return show(points.get(volume).stream()
            .map(point ->
                new BoundaryParticle(point, false))
            .collect(Collectors.toList()), player, taskGroup);
      }
    } else {
      // We have few enough volumes that we can check every point with other volumes
      //  to see if they are contained, so we can mark that
      for (Volume volume : points.keySet()) {
        // Ensure only one animation is happening per user per volume
        if (taskGroups.contains(volume, player)) {
          taskGroups.get(volume, player).cancel();
        }
        TaskGroup taskGroup = new TaskGroup();
        taskGroups.put(volume, player, taskGroup);
        return show(points.get(volume).stream()
            .map(point -> {
              for (Volume other : points.keySet()) {
                if (!other.equals(volume) && other.containsPoint(point)) {
                  return new BoundaryParticle(point, true);
                }
              }
              return new BoundaryParticle(point, false);
            })
            .collect(Collectors.toList()), player, taskGroup);
      }
    }

    return false;

  }

  public static boolean show(Volume volume, Player player) {
    ensureTaskExecutor();

    Location location = SpongeUtil.reduceLocation(player.serverLocation());
    if (!volume.domain().equals(location.domain())) {
      return false;
    }

    // Ensure only one animation is happening per user per volume
    if (taskGroups.contains(volume, player)) {
      taskGroups.get(volume, player).cancel();
    }
    TaskGroup taskGroup = new TaskGroup();
    taskGroups.put(volume, player, taskGroup);

    final List<BoundaryParticle> points = volume.surfacePointsNear(
            Vector3d.of(location.posX(), location.posY(), location.posZ()),
            PARTICLE_PROXIMITY,
            PARTICLE_DENSITY)
        .stream()
        .map(point -> new BoundaryParticle(point, false))
        .collect(Collectors.toList());
    return show(points, player, taskGroup);
  }

  public static boolean show(final List<BoundaryParticle> points,
                             final Player player,
                             final TaskGroup taskGroup) {
    if (points.isEmpty()) {
      return false;
    }
    Vector3d playerLocation = SpongeUtil.reduceLocation(player.serverLocation()).vector3d();

    Collections.shuffle(points);
    final double bundleRangeSize = ((double) PARTICLE_PROXIMITY) / PARTICLE_SCHEDULE_BUNDLES;
    points.sort(Comparator.comparing(point -> point.position.distanceSquared(playerLocation)));
    final double shortestDistance = points.get(0).position.distance(playerLocation);
    double flooredDistance = shortestDistance - shortestDistance % bundleRangeSize;  // "floored" distance of points in batch, relative to batch range
    final List<BoundaryParticle> batch = new LinkedList<>();
    AtomicInteger interiorCount = new AtomicInteger();
    for (BoundaryParticle point : points) {
      if (flooredDistance + bundleRangeSize < playerLocation.distance(point.position)) {
        final List<BoundaryParticle> finalBatch = new ArrayList<>(batch);
        ScheduledTaskFuture<?> future = VOLUME_PARTICLE_TASK_EXECUTOR.scheduleAtFixedRate(() ->
                finalBatch.forEach(p -> {
                  if (p.interior) {
                    if (interiorCount.get() % INTERIOR_BOUNDARY_LIKELIHOOD == 0) {
                      player.spawnParticles(INTERIOR_BOUNDARY_PARTICLE, SpongeUtil.raiseVector(p.position));
                    }
                    interiorCount.getAndIncrement();
                  } else {
                    player.spawnParticles(BOUNDARY_PARTICLE, SpongeUtil.raiseVector(p.position));
                  }
                }),
            (long) Math.floor((flooredDistance) * PARTICLE_SPREAD_DELAY),
            PARTICLE_REPEAT_PERIOD,
            TimeUnit.MILLISECONDS);
        taskGroup.add(future);
        VOLUME_PARTICLE_TASK_EXECUTOR.schedule(() -> future.cancel(true),
            PARTICLE_REPEAT_PERIOD * PARTICLE_REPEAT_COUNT,
            TimeUnit.MILLISECONDS);
        flooredDistance += bundleRangeSize;
        batch.clear();
      }
      batch.add(point);
    }
    return true;
  }

  private static class TaskGroup {
    final List<ScheduledTaskFuture<?>> tasks = new LinkedList<>();

    void add(ScheduledTaskFuture<?> task) {
      this.tasks.add(task);
    }

    void cancel() {
      tasks.forEach(task -> task.cancel(true));
    }
  }

  @Data
  @Accessors(fluent = true)
  private static class BoundaryParticle {
    private final Vector3d position;
    private final boolean interior;
  }
}
