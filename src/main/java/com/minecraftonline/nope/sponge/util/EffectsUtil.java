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

package com.minecraftonline.nope.sponge.util;

import com.minecraftonline.nope.common.math.Vector3d;
import com.minecraftonline.nope.common.math.Volume;
import com.minecraftonline.nope.common.struct.Location;
import com.minecraftonline.nope.sponge.SpongeNope;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.ScheduledTaskFuture;
import org.spongepowered.api.scheduler.TaskExecutorService;

/**
 * Utility class for minecraft effects.
 */
public class EffectsUtil {

  public static final long PARTICLE_PROXIMITY = 20;
  public static final long PARTICLE_DENSITY = 5;
  public static final long PARTICLE_SPREAD_DELAY = 120;
  public static final long PARTICLE_REPEAT_PERIOD = 2000;
  public static final long PARTICLE_REPEAT_COUNT = 8;

  private static TaskExecutorService VOLUME_PARTICLE_TASK_EXECUTOR;

  public static void show(Volume volume, Player player) {
    if (VOLUME_PARTICLE_TASK_EXECUTOR == null) {
      VOLUME_PARTICLE_TASK_EXECUTOR = Sponge.asyncScheduler()
          .createExecutor(SpongeNope.instance().pluginContainer());
    }

    Location location = SpongeUtil.reduceLocation(player.serverLocation());
    if (!volume.domain().equals(location.domain())) {
      return;
    }

    List<Vector3d> points = volume.surfacePointsNear(
        Vector3d.of(location.posX(), location.posY(), location.posZ()),
        PARTICLE_PROXIMITY,
        PARTICLE_DENSITY);
    Collections.shuffle(points);
    for (Vector3d point : points) {
      ScheduledTaskFuture<?> future = VOLUME_PARTICLE_TASK_EXECUTOR.scheduleAtFixedRate(() ->
          player.spawnParticles(ParticleEffect.builder().type(ParticleTypes.COMPOSTER)
                  .quantity(1)
                  .build(),
              SpongeUtil.raiseVector(point)),
          (long) Math.floor(location.vector3d().distance(point) * PARTICLE_SPREAD_DELAY),
          PARTICLE_REPEAT_PERIOD,
          TimeUnit.MILLISECONDS);
      VOLUME_PARTICLE_TASK_EXECUTOR.schedule(() -> future.cancel(true),
          PARTICLE_REPEAT_PERIOD * PARTICLE_REPEAT_COUNT,
          TimeUnit.MILLISECONDS);
    }
  }
}
