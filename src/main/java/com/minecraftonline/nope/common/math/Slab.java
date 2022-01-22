/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) 2021 Pieter Svenson
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package com.minecraftonline.nope.common.math;

import com.minecraftonline.nope.common.host.Domain;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Slab extends Volume {

  private final Integer minY;
  private final Integer maxY;

  private final Cuboid circumscribed;
  private final Cuboid inscribed;

  public Slab(Domain domain,
              Integer y1,
              Integer y2) {
    super(domain);
    this.minY = Math.min(y1, y2);
    this.maxY = Math.max(y1, y2);

    circumscribed = new Cuboid(domain,
        Integer.MIN_VALUE,
        minY,
        Integer.MIN_VALUE,
        Integer.MAX_VALUE,
        maxY,
        Integer.MAX_VALUE);

    inscribed = circumscribed;
  }

  public int minY() {
    return this.minY;
  }

  public int maxY() {
    return this.maxY;
  }

  @Override
  public @NotNull Cuboid circumscribed() {
    return circumscribed;
  }

  @Override
  public @NotNull Cuboid inscribed() {
    return inscribed;
  }

  @Override
  public boolean containsPoint(double x, double y, double z) {
    return y >= minY
        && y < maxY;
  }

  @Override
  public boolean containsBlock(int x, int y, int z) {
    return y >= minY
        && y < maxY;
  }

  @Override
  public boolean valid() {
    return this.minY != null
        && this.maxY != null
        && this.minY <= this.maxY;
  }

  @Override
  public List<Vector3d> surfacePointsNear(Vector3d point, double proximity, double density) {
    if (proximity <= 0) {
      throw new IllegalArgumentException("Your proximity cannot be negative or 0");
    }
    List<Vector3d> points = new LinkedList<>();
    final double proximitySquared = proximity * proximity;
    final double separation = 1 / density;

    double distance;
    double radius;

    // minY
    distance = Math.abs(point.y() - minY);
    if (distance <= proximity) {
      radius = Math.sqrt(proximitySquared - distance * distance);

      for (int i = (int) -Math.ceil(radius); i < Math.ceil(radius); i++) {
        for (int j = (int) -Math.ceil(radius); j < Math.ceil(radius); j++) {
          tryAddSurfacePoint(points, point.x() + i, minY, point.z() + j);
          for (double p = separation; p < 1; p += separation) {
            tryAddSurfacePoint(points, point.x() + i + p, minY, point.z() + j);
            tryAddSurfacePoint(points, point.x() + i, minY, point.z() + j + p);
          }
        }
      }
    }

    // maxY
    distance = Math.abs(point.y() - maxY);
    if (distance <= proximity) {
      radius = Math.sqrt(proximitySquared - distance * distance);

      for (int i = (int) -Math.ceil(radius); i < Math.ceil(radius); i++) {
        for (int j = (int) -Math.ceil(radius); j < Math.ceil(radius); j++) {
          tryAddSurfacePoint(points, point.x() + i, maxY, point.z() + j);
          for (double p = separation; p < 1; p += separation) {
            tryAddSurfacePoint(points, point.x() + i + p, maxY, point.z() + j);
            tryAddSurfacePoint(points, point.x() + i, maxY, point.z() + j + p);
          }
        }
      }
    }

    return points.stream()
        .filter(p -> p.distanceSquared(point) < proximitySquared)
        .collect(Collectors.toList());
  }

  private void tryAddSurfacePoint(Collection<Vector3d> points,
                                  double x, double y, double z) {
    if ((y == this.minY || y == this.maxY)) {
      points.add(Vector3d.of(x, y, z));
    }
  }

}
