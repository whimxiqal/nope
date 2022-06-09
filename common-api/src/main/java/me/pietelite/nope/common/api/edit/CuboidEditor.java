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

package me.pietelite.nope.common.api.edit;

/**
 * An editor for a cuboid Zone.
 */
public interface CuboidEditor extends ZoneEditor {

  /**
   * Sets the dimensions of this cuboid.
   *
   * @param x1 one side of the x-axis range
   * @param y1 one side of the y-axis range
   * @param z1 one side of the z-axis range
   * @param x2 another side of the x-axis range
   * @param y2 another side of the y-axis range
   * @param z2 another side of the z-axis range
   * @throws IllegalArgumentException if the length of the cuboid in any dimension is 0
   */
  void setDimensions(float x1, float y1, float z1, float x2, float y2, float z2)
      throws IllegalArgumentException;

  /**
   * The minimum x-axis boundary of the cuboid.
   *
   * @return the minimum x value
   */
  float minX();

  /**
   * The minimum y-axis boundary of the cuboid.
   *
   * @return the minimum y value
   */
  float minY();

  /**
   * The minimum z-axis boundary of the cuboid.
   *
   * @return the minimum z value
   */
  float minZ();

  /**
   * The maximum x-axis boundary of the cuboid.
   *
   * @return the maximum x value
   */
  float maxX();

  /**
   * The maximum y-axis boundary of the cuboid.
   *
   * @return the maximum y value
   */
  float maxY();

  /**
   * The maximum z-axis boundary of the cuboid.
   *
   * @return the maximum z value
   */
  float maxZ();

}
