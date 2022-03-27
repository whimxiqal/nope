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

import java.util.Objects;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

/**
 * A manifestation of the mathematical phenomenon.
 */
public class Plane {

  private static final double EPSILON = 0.00001;

  @Getter
  @Accessors(fluent = true)
  private final Vector3d origin;
  @Getter
  @Accessors(fluent = true)
  private final Vector3d normal;

  /**
   * Generic constructor.
   *
   * @param origin a point on the plane
   * @param normal a normal vector of the plane
   */
  public Plane(@NotNull Vector3d origin, @NotNull Vector3d normal) {
    this.origin = origin;
    this.normal = normal;
  }

  /**
   * Determine whether the plane is referring to the geometric shape as another plane.
   *
   * @param other the other plane
   * @return true if they refer to the same
   */
  public boolean equivalentTo(Plane other) {
    return this.origin.cross(other.origin).magnitude() < EPSILON
        && this.normal.dot(this.origin.plus(other.origin.negative())) < EPSILON;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Plane plane = (Plane) o;
    return origin.equals(plane.origin) && normal.equals(plane.normal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(origin, normal);
  }
}
