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

package com.minecraftonline.nope.structures;

public interface Volume {

  /**
   * Get minimum X value, inclusive.
   *
   * @return x min
   */
  int getMinX();

  /**
   * Get maximum X value, inclusive.
   *
   * @return x max
   */
  int getMaxX();

  /**
   * Get minimum Y value, inclusive.
   *
   * @return y min
   */
  int getMinY();

  /**
   * Get maximum Y value, inclusive.
   *
   * @return y max
   */
  int getMaxY();

  /**
   * Get minimum Z value, inclusive.
   *
   * @return z min
   */
  int getMinZ();

  /**
   * Get maximum Z value, inclusive.
   *
   * @return z max
   */
  int getMaxZ();

  /**
   * Check if this volume contains a point, given three
   * cartesian coordinates.
   *
   * @param x x value
   * @param y y value
   * @param z z value
   * @return true if the point is contained
   */
  default boolean contains(int x, int y, int z) {
    return x >= this.getMinX()
        && x <= this.getMaxX()
        && y >= this.getMinY()
        && y <= this.getMaxY()
        && z >= this.getMinZ()
        && z <= this.getMaxZ();
  }

  default boolean contains(Volume other) {
    return other.getMinX() >= this.getMinX()
        && other.getMaxX() <= this.getMaxX()
        && other.getMinY() >= this.getMinY()
        && other.getMaxY() <= this.getMaxY()
        && other.getMinZ() >= this.getMinZ()
        && other.getMaxZ() <= this.getMaxZ();
  }

  /**
   * Check if this volume intersects with another volume
   * or shares a face.
   *
   * @param other another volume
   * @return true if intersects
   */
  default boolean intersects(Volume other) {
    return (this.getMinX() <= other.getMaxX() && this.getMaxX() >= other.getMinX())
        && (this.getMinY() <= other.getMaxY() && this.getMaxY() >= other.getMinY())
        && (this.getMinZ() <= other.getMaxZ() && this.getMaxZ() >= other.getMinZ());
  }

}
