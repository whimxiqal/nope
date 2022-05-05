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

package me.pietelite.nope.sponge.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Vector3d;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.common.struct.Location;
import me.pietelite.nope.sponge.SpongeNope;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scheduler.ScheduledTaskFuture;
import org.spongepowered.api.scheduler.TaskExecutorService;
import org.spongepowered.api.world.server.ServerLocation;

/**
 * Utility class for minecraft effects.
 */
public final class EffectsUtil {

  public static final ParticleEffect BOUNDARY_PARTICLE = ParticleEffect.builder()
      .type(ParticleTypes.COMPOSTER.get())
      .quantity(1)
      .build();
  /**
   * Number of interior boundary particles per period of allowing particles to go through.
   */
  public static final int INTERIOR_BOUNDARY_LIKELIHOOD = 8;
  public static final ParticleEffect INTERIOR_BOUNDARY_PARTICLE = ParticleEffect.builder()
      .type(ParticleTypes.SMOKE.get())
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
  public static final int PARTICLE_PROXIMITY = 16;
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
  public static final int PARTICLE_SPREAD_DELAY = 800;
  private static final Table<Volume, UUID, TaskGroup> taskGroups = HashBasedTable.create();
  private static TaskExecutorService VOLUME_PARTICLE_TASK_EXECUTOR;

  private static void ensureTaskExecutor() {
    if (VOLUME_PARTICLE_TASK_EXECUTOR == null) {
      VOLUME_PARTICLE_TASK_EXECUTOR = Sponge.server().scheduler()
          .executor(SpongeNope.instance().pluginContainer());
    }
  }

  public static void stopAll(ServerPlayer player) {
    taskGroups.column(player.uniqueId()).forEach((volume, task) -> task.cancel());
  }

  /**
   * Display the boundaries of all {@link Volume}s within a {@link Scene} for
   * a specific {@link ServerPlayer}.
   *
   * @param scene  the scene to show
   * @param player the player to
   * @return true if any particles are shown, false if none
   */
  public static boolean show(Scene scene, ServerPlayer player, EffectInfo info) {
    ensureTaskExecutor();

    Location location = SpongeUtil.reduceLocation(player.serverLocation());
    Vector3d locationVector = Vector3d.of(location.posX(), location.posY(), location.posZ());
    List<Volume> volumes = scene.volumes();

    final Map<Volume, List<Vector3d>> points = new HashMap<>();
    for (Volume volume : volumes) {
      if (!volume.domain().equals(location.domain())) {
        continue;
      }

      List<Vector3d> surfacePoints = volume.surfacePointsNear(
          locationVector,
          info.particleProximity,
          info.particleDensity);
      if (!surfacePoints.isEmpty()) {
        points.put(volume, surfacePoints);
      }
    }

    if (points.size() > info.maxVolumeIntersectionComparisonCount) {
      for (Volume volume : points.keySet()) {
        // Ensure only one animation is happening per user per volume
        if (taskGroups.contains(volume, player.uniqueId())) {
          taskGroups.get(volume, player.uniqueId()).cancel();
        }
        TaskGroup taskGroup = new TaskGroup();
        taskGroups.put(volume, player.uniqueId(), taskGroup);
        return show(points.get(volume).stream()
            .map(point ->
                new BoundaryParticle(point, false))
            .collect(Collectors.toList()), player, taskGroup, info);
      }
    } else {
      // We have few enough volumes that we can check every point with other volumes
      //  to see if they are contained, so we can mark that
      for (Volume volume : points.keySet()) {
        // Ensure only one animation is happening per user per volume
        if (taskGroups.contains(volume, player.uniqueId())) {
          taskGroups.get(volume, player.uniqueId()).cancel();
        }
        TaskGroup taskGroup = new TaskGroup();
        taskGroups.put(volume, player.uniqueId(), taskGroup);
        return show(points.get(volume).stream()
            .map(point -> {
              for (Volume other : points.keySet()) {
                if (!other.equals(volume)
                    && other.containsPoint((float) point.x(), (float) point.y(), (float) point.z())) {
                  return new BoundaryParticle(point, true);
                }
              }
              return new BoundaryParticle(point, false);
            })
            .collect(Collectors.toList()), player, taskGroup, info);
      }
    }

    return false;

  }

  /**
   * Show the boundaries of a volume for a player.
   *
   * @param volume the volume to show
   * @param player the player for which to show the boundaries
   * @return true if any particles were shown, false if none
   */
  public static boolean show(Volume volume, ServerPlayer player, EffectInfo info) {
    ensureTaskExecutor();

    Location location = SpongeUtil.reduceLocation(player.serverLocation());
    if (!volume.domain().equals(location.domain())) {
      return false;
    }

    // Ensure only one animation is happening per user per volume
    if (taskGroups.contains(volume, player.uniqueId())) {
      taskGroups.get(volume, player.uniqueId()).cancel();
    }
    TaskGroup taskGroup = new TaskGroup();
    taskGroups.put(volume, player.uniqueId(), taskGroup);

    final List<BoundaryParticle> points = volume.surfacePointsNear(
            Vector3d.of(location.posX(), location.posY(), location.posZ()),
            info.particleProximity,
            info.particleDensity)
        .stream()
        .map(point -> new BoundaryParticle(point, false))
        .collect(Collectors.toList());
    return show(points, player, taskGroup, info);
  }

  /**
   * Show a list of particles to a player using a {@link TaskGroup}.
   *
   * @param points    the points to show
   * @param player    the player for which to show the points
   * @param taskGroup the task group to use for scheduling the showing of particles
   * @return true if any particles were shown, false if none
   */
  public static boolean show(final List<BoundaryParticle> points,
                             final ServerPlayer player,
                             final TaskGroup taskGroup,
                             final EffectInfo info) {
    if (points.isEmpty()) {
      return false;
    }
    Location location = SpongeUtil.reduceLocation(player.serverLocation());
    Vector3d playerLocation = Vector3d.of(location.posX(), location.posY(), location.posZ());

    Collections.shuffle(points);
    final double bundleRangeSize = ((double) info.particleProximity) / info.particleScheduleBundles;
    points.sort(Comparator.comparing(point -> point.position.distanceSquared(playerLocation)));
    final double shortestDistance = points.get(0).position.distance(playerLocation);
    // "floored" distance of points in batch, relative to batch range
    double flooredDistance = shortestDistance - shortestDistance % bundleRangeSize;
    final List<BoundaryParticle> batch = new LinkedList<>();
    AtomicInteger interiorCount = new AtomicInteger();
    double bundleRatio = 0;  // the ratio of our progress to finishing all bundles/batches
    final double bundleRatioIncrement = 1 / (double) info.particleScheduleBundles;
    for (BoundaryParticle point : points) {
      if (flooredDistance + bundleRangeSize < playerLocation.distance(point.position)) {
        final List<BoundaryParticle> finalBatch = new ArrayList<>(batch);
        final long delay = (long) Math.floor(bundleRatio * info.particleSpreadDelay);
        final Runnable futureRunnable = () ->
            finalBatch.forEach(p -> {
              if (p.interior) {
                if (interiorCount.get() % info.interiorBoundaryLikelihood == 0) {
                  player.spawnParticles(INTERIOR_BOUNDARY_PARTICLE, SpongeUtil.raiseVector(p.position));
                }
                interiorCount.getAndIncrement();
              } else {
                player.spawnParticles(BOUNDARY_PARTICLE, SpongeUtil.raiseVector(p.position));
              }
            });
        final ScheduledTaskFuture<?> future;
        if (info.particleRepeatPeriod <= 0) {
          future = VOLUME_PARTICLE_TASK_EXECUTOR.schedule(futureRunnable,
              delay,
              TimeUnit.MILLISECONDS);
          taskGroup.add(future);
        } else {
          future = VOLUME_PARTICLE_TASK_EXECUTOR.scheduleAtFixedRate(futureRunnable,
              delay,
              info.particleRepeatPeriod,
              TimeUnit.MILLISECONDS);
          taskGroup.add(future);
          VOLUME_PARTICLE_TASK_EXECUTOR.schedule(() -> future.cancel(true),
              (long) info.particleRepeatPeriod * info.particleRepeatCount + delay,
              TimeUnit.MILLISECONDS);
        }
        bundleRatio += bundleRatioIncrement;
        flooredDistance += bundleRangeSize;
        batch.clear();
      }
      batch.add(point);
    }
    return true;
  }

  public static boolean ripple(Volume volume, ServerPlayer player, EffectInfo info) {
    ensureTaskExecutor();
    Location location = SpongeUtil.reduceLocation(player.serverLocation());
    Vector3d playerLocation = Vector3d.of(location.posX(), location.posY(), location.posZ());
    if (!volume.domain().equals(location.domain())) {
      return false;
    }

    final List<BoundaryParticle> points = volume.surfacePointsNear(
            playerLocation,
            info.particleProximity,
            info.particleDensity)
        .stream()
        .map(point -> new BoundaryParticle(point, false))
        .collect(Collectors.toList());

    if (points.isEmpty()) {
      return false;
    }

    Collections.shuffle(points);
    final double bundleRangeSize = ((double) info.particleProximity) / info.particleScheduleBundles;
    points.sort(Comparator.comparing(point -> point.position.distanceSquared(playerLocation)));
    final double shortestDistance = points.get(0).position.distance(playerLocation);
    // "floored" distance of points in batch, relative to batch range
    double flooredDistance = shortestDistance - shortestDistance % bundleRangeSize;
    final List<BoundaryParticle> batch = new LinkedList<>();
    AtomicInteger interiorCount = new AtomicInteger();
    double bundleRatio = 0;  // the ratio of our progress to finishing all bundles/batches
    final double bundleRatioIncrement = 1 / (double) info.particleScheduleBundles;
    for (BoundaryParticle point : points) {
      if (flooredDistance + bundleRangeSize < playerLocation.distance(point.position)) {
        final List<BoundaryParticle> finalBatch = new ArrayList<>(batch);
        final long delay = (long) Math.floor(bundleRatio * info.particleSpreadDelay);
        final Runnable futureRunnable = () ->
            finalBatch.forEach(p -> {
              if (p.interior) {
                if (interiorCount.get() % info.interiorBoundaryLikelihood == 0) {
                  player.spawnParticles(INTERIOR_BOUNDARY_PARTICLE, SpongeUtil.raiseVector(p.position));
                }
                interiorCount.getAndIncrement();
              } else {
                player.spawnParticles(BOUNDARY_PARTICLE, SpongeUtil.raiseVector(p.position));
              }
            });
        VOLUME_PARTICLE_TASK_EXECUTOR.schedule(futureRunnable,
              delay,
              TimeUnit.MILLISECONDS);
        bundleRatio += bundleRatioIncrement;
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

  public static EffectInfo defaultInfo() {
    return EffectInfo.builder().build();
  }

  @Data
  @Accessors(fluent = true)
  @Builder
  public static class EffectInfo {
    @Builder.Default
    public int interiorBoundaryLikelihood = INTERIOR_BOUNDARY_LIKELIHOOD;
    @Builder.Default
    public int maxVolumeIntersectionComparisonCount = MAX_VOLUME_INTERSECTION_COMPARISON_COUNT;
    @Builder.Default
    public int particleDensity = PARTICLE_DENSITY;
    @Builder.Default
    public int particleProximity = PARTICLE_PROXIMITY;
    @Builder.Default
    public int particleRepeatCount = PARTICLE_REPEAT_COUNT;
    @Builder.Default
    public int particleRepeatPeriod = PARTICLE_REPEAT_PERIOD;
    @Builder.Default
    public int particleScheduleBundles = PARTICLE_SCHEDULE_BUNDLES;
    @Builder.Default
    public int particleSpreadDelay = PARTICLE_SPREAD_DELAY;
  }
}
