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
 * A type of Zone.
 */
public enum ZoneType {
  /**
   * A <a href="https://en.wikipedia.org/wiki/Cuboid#Rectangular_cuboid">rectangular cuboid</a>.
   */
  CUBOID,
  /**
   * A <a href="https://en.wikipedia.org/wiki/Cylinder">cylinder</a>.
   */
  CYLINDER,
  /**
   * A subset of a three-dimensional space bounded by two y-values, which form two bounding planes
   * in the x and z axes.
   * This is essentially <a href="https://en.wikipedia.org/wiki/Cylinder">cylinder</a> that is
   * unbounded in the x and z directions.
   */
  SLAB,
  /**
   * A <a href="https://en.wikipedia.org/wiki/Sphere">sphere</a>.
   */
  SPHERE,
}
