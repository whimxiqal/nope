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

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.pietelite.nope.common.api.edit.ZoneType;
import me.pietelite.nope.common.host.Domain;
import org.jetbrains.annotations.NotNull;

/**
 * A volume representing a mathematical sphere.
 */
public class Sphere extends Volume {

  private final Float posX;
  private final Float posY;
  private final Float posZ;
  private final Float radius;

  @Getter
  @Accessors(fluent = true)
  private final float radiusSquared;

  @Getter
  @Accessors(fluent = true)
  private final Vector2d midPoint2d;

  @Getter
  @Accessors(fluent = true)
  private final Vector3d midPoint3d;

  private final Cuboid circumscribed;
  private final Cuboid inscribed;

  /**
   * Generic constructor.
   *
   * @param domain the domain in which this volume resides
   * @param x      the x coordinate of the center of the sphere
   * @param y      the y coordinate of the center of the sphere
   * @param z      the z coordinate of the center of the sphere
   * @param radius the sphere's radius
   */
  public Sphere(@NotNull Domain domain,
                Float x, Float y, Float z,
                Float radius) {
    super(domain);
    this.posX = x;
    this.posY = y;
    this.posZ = z;
    this.radius = radius;

    this.radiusSquared = radius * radius;
    this.midPoint2d = Vector2d.of(posX, posZ);
    this.midPoint3d = Vector3d.of(posX, posY, posZ);

    circumscribed = new Cuboid(domain,
        posX - radius,
        posY - radius,
        posZ - radius,
        posX + radius,
        posY + radius,
        posZ + radius);

    float radiusSqrt3Over3 = radius * ((float) Math.sqrt(3)) / 3;
    inscribed = new Cuboid(domain,
        posX - radiusSqrt3Over3,
        posY - radiusSqrt3Over3,
        posZ - radiusSqrt3Over3,
        posX + radiusSqrt3Over3,
        posY + radiusSqrt3Over3,
        posZ + radiusSqrt3Over3);

  }

  private static Vector3d sphericalToCartesian(double x, double y, double z,
                                               double rho, double theta, double phi) {
    return Vector3d.of(x + rho * Math.sin(theta) * Math.sin(phi),
        y + rho * Math.cos(phi),
        z + rho * Math.cos(theta) * Math.sin(phi));
  }

  public float posX() {
    return this.posX;
  }

  public float posY() {
    return this.posY;
  }

  public float posZ() {
    return this.posZ;
  }

  public float radius() {
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
    return null;
  }

  @Override
  public ZoneType zoneType() {
    return ZoneType.SPHERE;
  }

  @Override
  public boolean containsPoint(float x, float y, float z) {
    return (posX - x) * (posX - x) + (posY - y) * (posY - y) + (posZ - z) * (posZ - z) <= radiusSquared;
  }

  @Override
  public boolean containsCuboid(float minX, float minY, float minZ,
                                float maxX, float maxY, float maxZ, boolean maxInclusive) {
    final float distSquaredX1 = (posX - minX) * (posX - minX);
    final float distSquaredY1 = (posX - minY) * (posX - minY);
    final float distSquaredZ1 = (posX - minZ) * (posX - minZ);
    final float distSquaredX2 = (posX - maxX) * (posX - maxX);
    final float distSquaredY2 = (posX - maxY) * (posX - maxY);
    final float distSquaredZ2 = (posX - maxZ) * (posX - maxZ);
    return distSquaredX1 + distSquaredY1 + distSquaredZ1 <= radiusSquared
        || distSquaredX2 + distSquaredY1 + distSquaredZ1 <= radiusSquared
        || distSquaredX1 + distSquaredY2 + distSquaredZ1 <= radiusSquared
        || distSquaredX1 + distSquaredY1 + distSquaredZ2 <= radiusSquared
        || distSquaredX2 + distSquaredY2 + distSquaredZ1 <= radiusSquared
        || distSquaredX2 + distSquaredY1 + distSquaredZ2 <= radiusSquared
        || distSquaredX1 + distSquaredY2 + distSquaredZ2 <= radiusSquared
        || distSquaredX2 + distSquaredY2 + distSquaredZ2 <= radiusSquared;
  }

  @Override
  public boolean valid() {
    return this.posX != null
        && this.posY != null
        && this.posZ != null
        && this.radius != null;
  }

  @Override
  public List<Vector3d> surfacePointsNear(Vector3d point, double proximity, double density) {
    if (proximity <= 0) {
      throw new IllegalArgumentException("Your proximity cannot be negative or 0");
    }
    List<Vector3d> points = new LinkedList<>();
    final double proximitySquared = proximity * proximity;
    final double separation = 1 / density;
    final double distance = Math.sqrt((point.x() - this.posX) * (point.x() - this.posX)
        + (point.y() - this.posY) * (point.y() - this.posY)
        + (point.z() - this.posZ) * (point.z() - this.posZ));
    final double distToAxisY = Math.sqrt((point.x() - this.posX) * (point.x() - this.posX)
        + (point.z() - this.posZ) * (point.z() - this.posZ));
    if (proximity + radius >= distance) {
      // the two spheres are close enough to be touching
      if (distance + proximity >= radius) {
        // the proximity bubble is not so far inside the sphere to be too far away from the surface
        double perimeter = 2 * Math.PI * radius;
        // angleInc (angle increment) is the smallest angle unit in radians
        double angleInc = 1 / (radius);  // (2*pi)/(2*pi*r)
        double thetaStart = Math.atan((point.x() - this.posX()) / (point.z() - this.posZ));
        double phiStart = Math.atan(distToAxisY / (point.y() - this.posY));
        if (point.z() < this.posZ) {
          thetaStart += Math.PI;
        }
        if (phiStart < 0) {
          phiStart += Math.PI; // keep it positive
        }

        final double halfThetaRange = Math.min(4 * proximity * Math.PI / radius, 2 * Math.PI) / 2;
        final double halfPhiRange = Math.min(4 * proximity * Math.PI / radius, Math.PI) / 2;
        final double minPhi = Math.max(0, phiStart - halfPhiRange);
        final double maxPhi = Math.min(Math.PI - angleInc, phiStart + halfPhiRange);
        for (double theta = thetaStart - halfThetaRange;
             theta < thetaStart + halfThetaRange;
             theta += angleInc) {
          for (double phi = minPhi; phi < maxPhi; phi += angleInc) {
            points.add(sphericalToCartesian(posX, posY, posZ, radius, theta, phi));
            for (double p = separation; p < 1; p += separation) {
              points.add(sphericalToCartesian(posX, posY, posZ, radius, theta + angleInc * p, phi));
              points.add(sphericalToCartesian(posX, posY, posZ, radius, theta, phi + angleInc * p));
            }
          }
        }
      }
    }

    return points.stream()
        .filter(p -> p.distanceSquared(point) < proximitySquared)
        .collect(Collectors.toList());
  }

}
