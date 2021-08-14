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

package com.minecraftonline.nope.common.struct;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

/**
 * An interface for any prismatic volume.
 */
@EqualsAndHashCode
public class Cuboid extends Volume {

  private final int minX;
  private final int minY;
  private final int minZ;
  private final int maxX;
  private final int maxY;
  private final int maxZ;

  public Cuboid(int minX,
                int minY,
                int minZ,
                int maxX,
                int maxY,
                int maxZ) {
    this.minX = minX;
    this.minY = minY;
    this.minZ = minZ;
    this.maxX = maxX;
    this.maxY = maxY;
    this.maxZ = maxZ;
  }

  public int minX() {
    return minX;
  }

  public int minY() {
    return minY;
  }

  public int minZ() {
    return minZ;
  }

  public int maxX() {
    return maxX;
  }

  public int maxY() {
    return maxY;
  }

  public int maxZ() {
    return maxZ;
  }

  @Override
  public @NotNull Cuboid circumscribed() {
    return this;
  }

  @Override
  public @NotNull Cuboid inscribed() {
    return this;
  }

  @Override
  public boolean contains(int x, int y, int z) {
    return x >= this.minX
        && x <= this.maxX
        && y >= this.minY
        && y <= this.maxY
        && z >= this.minZ
        && z <= this.maxZ;
  }

  /**
   * Determine whether another volume is entirely contained within
   * this volume.
   *
   * @param other the other volume
   * @return true if the other volume is contained in this one
   */
  public boolean contains(Cuboid other) {
    return other.minX >= this.minX
        && other.maxX <= this.maxX
        && other.minY >= this.minY
        && other.maxY <= this.maxY
        && other.minZ >= this.minZ
        && other.maxZ <= this.maxZ;
  }

  /**
   * Check if this volume intersects with another volume
   * or shares a face.
   *
   * @param other another cuboid
   * @return true if intersects
   */
  public boolean intersects(Cuboid other) {
    return (this.minX <= other.maxX && this.maxX >= other.minX)
        && (this.minY <= other.maxY && this.maxY >= other.minY)
        && (this.minZ <= other.maxZ && this.maxZ >= other.minZ);
  }

}
