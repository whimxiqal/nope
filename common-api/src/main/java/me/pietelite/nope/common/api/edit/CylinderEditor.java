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
 * An editor for a cylinder Zone.
 */
@SuppressWarnings("checkstyle:MethodName")
public interface CylinderEditor extends ZoneEditor {

  /**
   * Sets the dimensions for this cylinder.
   *
   * @param x      the x coordinate of the center of the cylinder
   * @param y      the y coordinate of the center, bottom side of the cylinder
   * @param z      the z coordinate of the center of the cylinder
   * @param radius the radius of the cylinder
   * @param height the height of the cylinder
   */
  void setDimensions(float x, float y, float z, float radius, float height);

  /**
   * The x coordinate of the center of the cylinder.
   *
   * @return the x coordinate
   */
  float x();

  /**
   * The y coordinate of the center of the cylinder.
   *
   * @return the y coordinate
   */
  float y();

  /**
   * The z coordinate of the center of the cylinder.
   *
   * @return the z coordinate
   */
  float z();

  /**
   * The radius of the cylinder.
   *
   * @return the radius
   */
  float radius();

  /**
   * The height of the cylinder.
   *
   * @return the height
   */
  float height();

}
