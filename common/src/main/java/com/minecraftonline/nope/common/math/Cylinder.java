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
import com.minecraftonline.nope.common.math.Cuboid;
import com.minecraftonline.nope.common.math.Vector2d;
import com.minecraftonline.nope.common.math.Vector3d;
import com.minecraftonline.nope.common.math.Volume;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Cylinder extends Volume {

  private final Integer posX;
  private final Integer minY;
  private final Integer maxY;
  private final Integer posZ;
  private final Double radius;

  @Getter
  @Accessors(fluent = true)
  private final double radiusSquared;
  @Getter
  @Accessors(fluent = true)
  private final int lengthY;
  @Getter
  @Accessors(fluent = true)
  private final Vector2d midPoint2d;
  @Getter
  @Accessors(fluent = true)
  private final Vector3d midPoint3d;
  @Getter
  @Accessors(fluent = true)
  private final Vector3d midPoint3dTop;
  @Getter
  @Accessors(fluent = true)
  private final Vector3d midPoint3dBottom;

  private final Cuboid circumscribed;
  private final Cuboid inscribed;

  public Cylinder(Domain domain,
                  Integer x,
                  Integer y1,
                  Integer y2,
                  Integer z,
                  Double radius) {
    super(domain);
    this.posX = x;
    this.minY = Math.min(y1, y2);
    this.maxY = Math.max(y1, y2);
    this.posZ = z;
    this.radius = radius;

    this.radiusSquared = radius * radius;
    this.lengthY = maxY - minY;
    this.midPoint2d = Vector2d.of((double) posX, (double) posZ);
    this.midPoint3d = Vector3d.of((double) posX, (double) lengthY / 2 + minY, (double) posZ);
    this.midPoint3dTop = Vector3d.of((double) posX, maxY, (double) posZ);
    this.midPoint3dBottom = Vector3d.of((double) posX, minY, (double) posZ);

    circumscribed = new Cuboid(domain,
        (int) Math.floor(posX - radius),
        minY,
        (int) Math.floor(posZ - radius),
        (int) Math.ceil(posX + radius),
        maxY,
        (int) Math.ceil(posZ + radius));

    double radiusSqrt2Over2 = radius * Math.sqrt(2) / 2;
    inscribed = new Cuboid(domain,
        (int) Math.ceil(posX - radiusSqrt2Over2),
        minY,
        (int) Math.ceil(posZ - radiusSqrt2Over2),
        (int) Math.floor(posX + radiusSqrt2Over2),
        maxY,
        (int) Math.floor(posZ + radiusSqrt2Over2));
  }

  public int posX() {
    return this.posX;
  }

  public int minY() {
    return this.minY;
  }

  public int maxY() {
    return this.maxY;
  }

  public int posZ() {
    return this.posZ;
  }

  public double radius() {
    return this.radius;
  }

  @Override
  public @NotNull
  Cuboid circumscribed() {
    return circumscribed;
  }

  @Override
  public @NotNull
  Cuboid inscribed() {
    return inscribed;
  }

  @Override
  public boolean containsPoint(double x, double y, double z) {
    return y >= minY
        && y < maxY
        && (posX - x) * (posX - x) + (posZ - z) * (posZ - z) <= radiusSquared;
  }

  @Override
  public boolean containsBlock(int x, int y, int z) {
    int posXsquared = (posX - x) * (posX - x);
    int posXplus1Squared = (posX + 1 - x) * (posX + 1 - x);
    int posZsquared = (posZ - z) * (posZ - z);
    int posZplus1Squared = (posZ + 1 - z) * (posZ + 1 - z);
    return y >= minY
        && y < maxY
        && (
        posXsquared + posZsquared <= radiusSquared
            || posXplus1Squared + posZsquared <= radiusSquared
            || posXplus1Squared + posZplus1Squared <= radiusSquared
            || posXsquared + posZplus1Squared <= radiusSquared);
  }

  @Override
  public boolean valid() {
    return this.posX != null
        && this.minY != null
        && this.maxY != null
        && this.posZ != null
        && this.radius != null
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
    double squarePlaneRadius;

    // minY (bottom)
    distance = Math.abs(point.y() - minY);
    if (distance <= proximity) {
      squarePlaneRadius = Math.sqrt(proximitySquared - distance * distance);

      for (int i = (int) -Math.ceil(squarePlaneRadius); i < Math.ceil(squarePlaneRadius); i++) {
        for (int j = (int) -Math.ceil(squarePlaneRadius); j < Math.ceil(squarePlaneRadius); j++) {
          tryAddFlatSurfacePoint(points, point.x() + i, minY, point.z() + j);
          for (double p = separation; p < 1; p += separation) {
            tryAddFlatSurfacePoint(points, point.x() + i + p, minY, point.z() + j);
            tryAddFlatSurfacePoint(points, point.x() + i, minY, point.z() + j + p);
          }
        }
      }
    }

    // maxY (top)
    distance = Math.abs(point.y() - maxY);
    if (distance <= proximity) {
      squarePlaneRadius = Math.sqrt(proximitySquared - distance * distance);

      for (int i = (int) -Math.ceil(squarePlaneRadius); i < Math.ceil(squarePlaneRadius); i++) {
        for (int j = (int) -Math.ceil(squarePlaneRadius); j < Math.ceil(squarePlaneRadius); j++) {
          tryAddFlatSurfacePoint(points, point.x() + i, maxY, point.z() + j);
          for (double p = separation; p < 1; p += separation) {
            tryAddFlatSurfacePoint(points, point.x() + i + p, maxY, point.z() + j);
            tryAddFlatSurfacePoint(points, point.x() + i, maxY, point.z() + j + p);
          }
        }
      }
    }

    // round side
    distance = Math.sqrt((point.x() - this.posX) * (point.x() - this.posX) + (point.z() - this.posZ) * (point.z() - this.posZ));
    if (proximity + radius >= distance) {
      // the two circles are close enough to be touching
      if (distance + proximity >= radius) {
        // the proximity circle is not so far inside the cylinder circle to be too far away from borders
        double radians;
        if (distance + radius < proximity) {
          // the proximity circle entirely encapsulates the cylinder circle
          radians = Math.PI;
        } else {
          radians = Math.acos((radiusSquared + distance * distance - proximitySquared)
              / (2 * radius * distance));
        }
        double perimeter = 2 * Math.PI * radius;
        // thetaInc (theta increment) is the smallest unit of theta
        double thetaInc = 1 / (radius);  // (2*pi)/(2*pi*r)
        double thetaStart = Math.atan((point.x() - this.posX()) / (point.z() - this.posZ));
        if (point.z() < this.posZ) {
          thetaStart += Math.PI;
        }

        for (double theta = thetaStart - radians; theta < thetaStart + radians; theta += thetaInc) {
          for (int y = (int) Math.floor(point.y() - proximity); y <= Math.ceil(point.y() + proximity); y++) {
            double majorVertZ = posZ + Math.cos(theta) * radius;
            double majorVertX = posX + Math.sin(theta) * radius;
            tryAddCurvedSurfacePoint(points, majorVertX, y, majorVertZ);
            for (double p = separation; p < 1; p += separation) {
              tryAddCurvedSurfacePoint(points, majorVertX, y + p, majorVertZ);
              tryAddCurvedSurfacePoint(points, posX + Math.sin(theta + thetaInc * p) * radius,
                  y,
                  posZ + Math.cos(theta + thetaInc * p) * radius);
            }
          }
        }
      }
    }

    return points.stream()
        .filter(p -> p.distanceSquared(point) < proximitySquared)
        .collect(Collectors.toList());
  }

  private void tryAddFlatSurfacePoint(Collection<Vector3d> points,
                                      double x, double y, double z) {
    if ((y == this.minY || y == this.maxY) && (x - posX) * (x - posX) + (z - posZ) * (z - posZ) <= radiusSquared) {
      points.add(Vector3d.of(x, y, z));
    }
  }

  private void tryAddCurvedSurfacePoint(Collection<Vector3d> points,
                                        double x, double y, double z) {
    if (y >= this.minY && y <= this.maxY) {
      points.add(Vector3d.of(x, y, z));
    }
  }

}
