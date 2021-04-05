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

package com.minecraftonline.nope.host;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.minecraftonline.nope.structures.Volume;

import java.util.Collection;

public abstract class VolumeHost extends Host implements Volume {

  protected final int xmin;
  protected final int xmax;

  protected final int ymin;
  protected final int ymax;

  protected final int zmin;
  protected final int zmax;

  /**
   * Default constructor.
   * @param name the name
   * @param xmin the minimum x value, inclusive
   * @param xmax the maximum x value, inclusive
   * @param ymin the minimum y value, inclusive
   * @param ymax the maximum y value, inclusive
   * @param zmin the minimum z value, inclusive
   * @param zmax the maximum z value, inclusive
   */
  public VolumeHost(String name,
                    int xmin,
                    int xmax,
                    int ymin,
                    int ymax,
                    int zmin,
                    int zmax) {
    super(name);
    if (xmin > xmax || ymin > ymax || zmin > zmax) {
      throw new IllegalArgumentException("Minimum values must be less than "
          + "or equal to maximum values");
    }
    this.xmin = xmin;
    this.xmax = xmax;
    this.ymin = ymin;
    this.ymax = ymax;
    this.zmin = zmin;
    this.zmax = zmax;
  }

  @Override
  public int getMinX() {
    return xmin;
  }

  @Override
  public int getMaxX() {
    return xmax;
  }

  @Override
  public int getMinY() {
    return ymin;
  }

  @Override
  public int getMaxY() {
    return ymax;
  }

  @Override
  public int getMinZ() {
    return zmin;
  }

  @Override
  public int getMaxZ() {
    return zmax;
  }

}
