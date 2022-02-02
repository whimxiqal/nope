/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

package com.minecraftonline.nope.common.math;

import com.minecraftonline.nope.common.math.Dimension;
import com.minecraftonline.nope.common.math.Plane;
import com.minecraftonline.nope.common.math.Vector3d;
import lombok.Getter;
import lombok.experimental.Accessors;

public class UnitNormalPlane extends Plane {

  @Getter
  @Accessors(fluent = true)
  private final Dimension axis;
  @Getter
  @Accessors(fluent = true)
  private final double location;

  public UnitNormalPlane(Dimension axis, double location) {
    super(calcOrigin(axis, location), calcNormal(axis));
    this.axis = axis;
    this.location = location;
  }

  private static Vector3d calcOrigin(Dimension axis, double location) {
    switch (axis) {
      case X:
        return Vector3d.of(location, 0, 0);
      case Y:
        return Vector3d.of(0, location, 0);
      case Z:
        return Vector3d.of(0, 0, location);
      default:
        throw new IllegalArgumentException("Dimension is unrecognized: " + axis);
    }
  }

  private static Vector3d calcNormal(Dimension axis) {
    switch (axis) {
      case X:
        return Vector3d.of(1, 0, 0);
      case Y:
        return Vector3d.of(0, 1, 0);
      case Z:
        return Vector3d.of(0, 0, 1);
      default:
        throw new IllegalArgumentException("Dimension is unrecognized: " + axis);
    }
  }
}
