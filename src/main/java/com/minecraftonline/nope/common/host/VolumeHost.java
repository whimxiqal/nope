/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
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

package com.minecraftonline.nope.common.host;

import com.minecraftonline.nope.common.struct.Volume;

/**
 * A {@link Host} that is also a {@link Volume}.
 */
public abstract class VolumeHost extends Host implements Volume {

  protected final int minX;
  protected final int maxX;
  protected final int minY;
  protected final int maxY;
  protected final int minZ;
  protected final int maxZ;

  /**
   * Default constructor.
   *
   * @param name the name
   * @param minX the minimum x value, inclusive
   * @param maxX the maximum x value, inclusive
   * @param minY the minimum y value, inclusive
   * @param maxY the maximum y value, inclusive
   * @param minZ the minimum z value, inclusive
   * @param maxZ the maximum z value, inclusive
   */
  public VolumeHost(String name,
                    int minX,
                    int maxX,
                    int minY,
                    int maxY,
                    int minZ,
                    int maxZ) {
    super(name);
    if (minX > maxX || minY > maxY || minZ > maxZ) {
      throw new IllegalArgumentException("Minimum values must be less than "
          + "or equal to maximum values");
    }
    this.minX = minX;
    this.maxX = maxX;
    this.minY = minY;
    this.maxY = maxY;
    this.minZ = minZ;
    this.maxZ = maxZ;
  }

  @Override
  public int getMinX() {
    return minX;
  }

  @Override
  public int getMaxX() {
    return maxX;
  }

  @Override
  public int getMinY() {
    return minY;
  }

  @Override
  public int getMaxY() {
    return maxY;
  }

  @Override
  public int getMinZ() {
    return minZ;
  }

  @Override
  public int getMaxZ() {
    return maxZ;
  }

}
