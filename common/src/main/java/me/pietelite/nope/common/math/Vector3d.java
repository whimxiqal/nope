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

package me.pietelite.nope.common.math;

import lombok.Value;
import lombok.experimental.Accessors;

import java.util.Objects;

@Value
@Accessors(fluent = true)
public class Vector3d {
  double x;
  double y;
  double z;

  private Vector3d(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public static Vector3d of(double x, double y, double z) {
    return new Vector3d(x, y, z);
  }

  public double distanceSquared(Vector3d other) {
    double lengthX = this.x - other.x;
    double lengthY = this.y - other.y;
    double lengthZ = this.z - other.z;
    return lengthX * lengthX + lengthY * lengthY + lengthZ * lengthZ;
  }

  public double distance(Vector3d other) {
    return Math.sqrt(distanceSquared(other));
  }

  public Vector3d plus(Vector2d vector2d) {
    return new Vector3d(x + vector2d.posX(), y, z + vector2d.posZ());
  }

  public double magnitude() {
    return Math.sqrt(x * x + y * y + z * z);
  }

  public Vector3d normalize() {
    double magnitude = magnitude();
    return new Vector3d(x / magnitude, y / magnitude, z / magnitude);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Vector3d vector3d = (Vector3d) o;
    return Double.compare(vector3d.x, x) == 0
        && Double.compare(vector3d.y, y) == 0
        && Double.compare(vector3d.z, z) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, z);
  }

  @Override
  public String toString() {
    return "Vector3d{"
        + "x=" + x
        + ", y=" + y
        + ", z=" + z
        + '}';
  }
}
