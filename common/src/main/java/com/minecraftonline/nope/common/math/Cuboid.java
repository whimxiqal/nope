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

import com.minecraftonline.nope.common.host.Domain;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

public class Cuboid extends Volume {

  private final Integer minX;
  private final Integer minY;
  private final Integer minZ;
  private final Integer maxX;
  private final Integer maxY;
  private final Integer maxZ;
  @Getter
  @Accessors(fluent = true)
  private final int lengthX;
  @Getter
  @Accessors(fluent = true)
  private final int lengthY;
  @Getter
  @Accessors(fluent = true)
  private final int lengthZ;
  @Getter
  @Accessors(fluent = true)
  private final Vector2d midPointYZPlane;
  @Getter
  @Accessors(fluent = true)
  private final Vector2d midPointXZPlane;
  @Getter
  @Accessors(fluent = true)
  private final Vector2d midPointXYPlane;
  @Getter
  @Accessors(fluent = true)
  private final Vector3d midPoint3d;
  @Getter
  @Accessors(fluent = true)
  private final double radiusXYPlane;
  @Getter
  @Accessors(fluent = true)
  private final double radiusXZPlane;
  @Getter
  @Accessors(fluent = true)
  private final double radiusYZPlane;
  @Getter
  @Accessors(fluent = true)
  private final double radiusCircumscribedSphere;

  // TODO Build a "selection" class in the tool/selection folders, not in the structure class (same with other volumes)
  public Cuboid(Domain domain,
                Integer x1,
                Integer y1,
                Integer z1,
                Integer x2,
                Integer y2,
                Integer z2) {
    super(domain);
    this.minX = Math.min(x1, x2);
    this.minY = Math.min(y1, y2);
    this.minZ = Math.min(z1, z2);
    this.maxX = Math.max(x1, x2);
    this.maxY = Math.max(y1, y2);
    this.maxZ = Math.max(z1, z2);
    this.lengthX = maxX - minX;
    this.lengthY = maxY - minY;
    this.lengthZ = maxZ - minZ;
    this.midPointXYPlane = Vector2d.of(
        maxX - ((double) lengthX) / 2,
        maxY - ((double) lengthY) / 2);
    this.midPointXZPlane = Vector2d.of(
        maxX - ((double) lengthX) / 2,
        maxZ - ((double) lengthZ) / 2);
    this.midPointYZPlane = Vector2d.of(
        maxY - ((double) lengthY) / 2,
        maxZ - ((double) lengthZ) / 2);
    this.midPoint3d = Vector3d.of(
        maxX - ((double) lengthX) / 2,
        maxY - ((double) lengthY) / 2,
        maxZ - ((double) lengthZ) / 2);
    this.radiusXYPlane = Math.sqrt(lengthX * lengthX + lengthY * lengthY);
    this.radiusXZPlane = Math.sqrt(lengthX * lengthX + lengthZ * lengthZ);
    this.radiusYZPlane = Math.sqrt(lengthY * lengthY + lengthZ * lengthZ);
    this.radiusCircumscribedSphere = Math.sqrt(lengthX * lengthX + lengthY * lengthY + lengthZ * lengthZ);
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
  public @NotNull
  Cuboid circumscribed() {
    return this;
  }

  @Override
  public @NotNull
  Cuboid inscribed() {
    return this;
  }

  @Override
  public boolean containsPoint(double x, double y, double z) {
    return x >= this.minX
        && x < this.maxX
        && y >= this.minY
        && y < this.maxY
        && z >= this.minZ
        && z < this.maxZ;
  }

  @Override
  public boolean containsBlock(int x, int y, int z) {
    return x >= this.minX
        && x < this.maxX
        && y >= this.minY
        && y < this.maxY
        && z >= this.minZ
        && z < this.maxZ;
  }

  @Override
  public boolean valid() {
    return this.minX != null
        && this.minY != null
        && this.minZ != null
        && this.maxX != null
        && this.maxY != null
        && this.maxZ != null
        && this.minX <= this.maxX
        && this.minY <= this.maxY
        && this.minZ <= this.maxZ;
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

    // minX
    distance = Math.abs(point.x() - minX);
    if (distance <= proximity) {
      radius = Math.sqrt(proximitySquared - distance * distance);

      for (int i = (int) -Math.ceil(radius); i < Math.ceil(radius); i++) {
        for (int j = (int) -Math.ceil(radius); j < Math.ceil(radius); j++) {
          tryAddSurfacePoint(points, Dimension.X, minX, point.y() + i, point.z() + j);
          for (double p = separation; p < 1; p += separation) {
            tryAddSurfacePoint(points, Dimension.X, minX, point.y() + i + p, point.z() + j);
            tryAddSurfacePoint(points, Dimension.X, minX, point.y() + i, point.z() + j + p);
          }
        }
      }
    }

    // maxX
    distance = Math.abs(point.x() - maxX);
    if (distance <= proximity) {
      radius = Math.sqrt(proximitySquared - distance * distance);

      for (int i = (int) -Math.ceil(radius); i < Math.ceil(radius); i++) {
        for (int j = (int) -Math.ceil(radius); j < Math.ceil(radius); j++) {
          tryAddSurfacePoint(points, Dimension.X, maxX, point.y() + i, point.z() + j);
          for (double p = separation; p < 1; p += separation) {
            tryAddSurfacePoint(points, Dimension.X, maxX, point.y() + i + p, point.z() + j);
            tryAddSurfacePoint(points, Dimension.X, maxX, point.y() + i, point.z() + j + p);
          }
        }
      }
    }

    // minY
    distance = Math.abs(point.y() - minY);
    if (distance <= proximity) {
      radius = Math.sqrt(proximitySquared - distance * distance);

      for (int i = (int) -Math.ceil(radius); i < Math.ceil(radius); i++) {
        for (int j = (int) -Math.ceil(radius); j < Math.ceil(radius); j++) {
          tryAddSurfacePoint(points, Dimension.Y, point.x() + i, minY, point.z() + j);
          for (double p = separation; p < 1; p += separation) {
            tryAddSurfacePoint(points, Dimension.Y, point.x() + i + p, minY, point.z() + j);
            tryAddSurfacePoint(points, Dimension.Y, point.x() + i, minY, point.z() + j + p);
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
          tryAddSurfacePoint(points, Dimension.Y, point.x() + i, maxY, point.z() + j);
          for (double p = separation; p < 1; p += separation) {
            tryAddSurfacePoint(points, Dimension.Y, point.x() + i + p, maxY, point.z() + j);
            tryAddSurfacePoint(points, Dimension.Y, point.x() + i, maxY, point.z() + j + p);
          }
        }
      }
    }

    // minZ
    distance = Math.abs(point.z() - minZ);
    if (distance <= proximity) {
      radius = Math.sqrt(proximitySquared - distance * distance);

      for (int i = (int) -Math.ceil(radius); i < Math.ceil(radius); i++) {
        for (int j = (int) -Math.ceil(radius); j < Math.ceil(radius); j++) {
          tryAddSurfacePoint(points, Dimension.Z, point.x() + i, point.y() + j, minZ);
          for (double p = separation; p < 1; p += separation) {
            tryAddSurfacePoint(points, Dimension.Z, point.x() + i + p, point.y() + j, minZ);
            tryAddSurfacePoint(points, Dimension.Z, point.x() + i, point.y() + j + p, minZ);
          }
        }
      }
    }

    // minZ
    distance = Math.abs(point.z() - maxZ);
    if (distance <= proximity) {
      radius = Math.sqrt(proximitySquared - distance * distance);

      for (int i = (int) -Math.ceil(radius); i < Math.ceil(radius); i++) {
        for (int j = (int) -Math.ceil(radius); j < Math.ceil(radius); j++) {
          tryAddSurfacePoint(points, Dimension.Z, point.x() + i, point.y() + j, maxZ);
          for (double p = separation; p < 1; p += separation) {
            tryAddSurfacePoint(points, Dimension.Z, point.x() + i + p, point.y() + j, maxZ);
            tryAddSurfacePoint(points, Dimension.Z, point.x() + i, point.y() + j + p, maxZ);
          }
        }
      }
    }

    return points.stream()
        .filter(p -> p.distanceSquared(point) < proximitySquared)
        .collect(Collectors.toList());
  }

  private void tryAddSurfacePoint(Collection<Vector3d> points,
                                  Dimension dimension,
                                  double x, double y, double z) {
    switch (dimension) {
      case X:
        if ((x == this.minX || x == this.maxX) && y >= minY && y < maxY && z >= minZ && z < maxZ) {
          points.add(Vector3d.of(x, y, z));
        }
        return;
      case Y:
        if ((y == this.minY || y == this.maxY) && x >= minX && x < maxX && z >= minZ && z < maxZ) {
          points.add(Vector3d.of(x, y, z));
        }
        return;
      case Z:
        if ((z == this.minZ || z == this.maxZ) && x >= minX && x < maxX && y >= minY && y < maxY) {
          points.add(Vector3d.of(x, y, z));
        }
        return;
      default:
    }
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

  @Override
  public String toString() {
    return "Cuboid{"
        + "minX=" + minX
        + ", minY=" + minY
        + ", minZ=" + minZ
        + ", maxX=" + maxX
        + ", maxY=" + maxY
        + ", maxZ=" + maxZ
        + '}';
  }
}
