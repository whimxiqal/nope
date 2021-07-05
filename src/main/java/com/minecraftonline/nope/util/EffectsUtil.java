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
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.structures.Volume;
import java.util.Random;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Utility class for minecraft effects.
 */
public class EffectsUtil {

  /**
   * Display a volume in a world given specific tolerances.
   *
   * @param volume    the volume whose walls to display
   * @param player    the viewer of the display
   * @param proximity the distance away from the viewing location to show
   * @return true the volume was close enough to be at least partially displayed
   */
  public static boolean showVolume(Volume volume, Player player, int proximity) {
    final int[][] volumePos = new int[][]{
        {volume.getMinX(), volume.getMinY(), volume.getMinZ()},
        {volume.getMaxX(), volume.getMaxY(), volume.getMaxZ()}
    };
    final int[][] playerPos = new int[][]{
        {
            player.getLocation().getBlockX() - proximity,
            player.getLocation().getBlockY() - proximity,
            player.getLocation().getBlockZ() - proximity
        },
        {
            player.getLocation().getBlockX() + proximity,
            player.getLocation().getBlockY() + proximity,
            player.getLocation().getBlockZ() + proximity
        }
    };
    final int particleCount = 4;
    final double portion = 1.0 / particleCount;
    final int proximitySquared = proximity * proximity;
    final Random random = new Random();
    final double[] vals = new double[3];
    boolean particleDisplayed = false;
    // chooses which dimension is constant for illumination of one face
    for (int i = 0; i < 3; i++) {
      // chooses between the min and max value of the dimension
      for (int j = 0; j < 2; j++) {
        for (int a = Math.max(volumePos[0][(i + 1) % 3], playerPos[0][(i + 1) % 3]);
             a <= Math.min(volumePos[1][(i + 1) % 3], playerPos[1][(i + 1) % 3]);
             a++) {    // chooses one dimension along the constant face
          for (int b = Math.max(volumePos[0][(i + 2) % 3], playerPos[0][(i + 2) % 3]);
               b <= Math.min(volumePos[1][(i + 2) % 3], playerPos[1][(i + 2) % 3]);
               b++) {  // chooses the other dimension along the constant face
            for (int q = 0; q < particleCount; q++) {
              // normal axis dim value, next lateral axis dim value, next lateral axis dim value
              vals[0] = volumePos[j][i] + j;
              vals[1] = a + portion * q;
              vals[2] = b + portion * q;
              Vector3d particleLocation = new Vector3d(vals[(3 - i) % 3],
                  vals[(4 - i) % 3],
                  vals[(5 - i) % 3]);
              if (particleLocation.distanceSquared(player.getLocation().getPosition())
                  < proximitySquared) {
                particleDisplayed = true;
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
    return particleDisplayed;
  }

}
