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

package com.minecraftonline.nope.util;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.structures.Volume;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.Random;

public class EffectsUtil {

  /**
   * Display a volume in a world given specific tolerances.
   *
   * @param volume    the volume whose walls to display
   * @param player    the viewer of the display
   * @param proximity the distance away from the viewing location to show
   */
  public static void showVolume(Volume volume, Player player, int proximity) {
    int[][] pos = new int[][]{
        {volume.getMinX(), volume.getMinY(), volume.getMinZ()},
        {volume.getMaxX(), volume.getMaxY(), volume.getMaxZ()}
    };
    int particleCount = 4;
    double portion = 1.0 / particleCount;
    int proximitySquared = proximity * proximity;
    Random random = new Random();
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 2; j++) {
        for (int a = pos[0][(i + 1) % 3]; a <= pos[1][(i + 1) % 3]; a++) {
          for (int b = pos[0][(i + 2) % 3]; b <= pos[1][(i + 2) % 3]; b++) {
            for (int q = 0; q < particleCount; q++) {
              // normal axis dim value, next lateral axis dim value, next lateral axis dim value
              double[] vals = {pos[j][i] + j, a + portion * q, b + portion * q};
              Vector3d particleLocation = new Vector3d(vals[(3 - i) % 3], vals[(4 - i) % 3], vals[(5 - i) % 3]);
              if (particleLocation.distanceSquared(player.getLocation().getPosition()) < proximitySquared) {
                Sponge.getScheduler().createTaskBuilder()
                    .async()
                    .delayTicks(random.nextInt(60))
                    .execute(() ->
                        player.spawnParticles(ParticleEffect.builder().type(ParticleTypes.CLOUD)
                                .quantity(1)
                                .build(),
                            particleLocation))
                    .submit(Nope.getInstance());
              }
            }
          }
        }
      }
    }
  }

}
