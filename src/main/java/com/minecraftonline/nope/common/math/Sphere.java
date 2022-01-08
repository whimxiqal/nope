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
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Sphere extends Volume {

  private final Integer posX;
  private final Integer posY;
  private final Integer posZ;
  private final Double radius;

  @Getter
  @Accessors(fluent = true)
  private final Double radiusSquared;

  @Getter
  @Accessors(fluent = true)
  private final Vector2d midPoint2d;

  @Getter
  @Accessors(fluent = true)
  private final Vector3d midPoint3d;

  private final Cuboid circumscribed;
  private final Cuboid inscribed;

  public Sphere(Domain domain,
                Integer x,
                Integer y,
                Integer z,
                Double radius) {
    super(domain);
    this.posX = x;
    this.posY = y;
    this.posZ = z;
    this.radius = radius;

    this.radiusSquared = radius * radius;
    this.midPoint2d = Vector2d.of(posX, posZ);
    this.midPoint3d = Vector3d.of(posX, posY, posZ);

    circumscribed = new Cuboid(domain,
        (int) Math.floor(posX - radius),
        (int) Math.floor(posY - radius),
        (int) Math.floor(posZ - radius),
        (int) Math.ceil(posX + radius),
        (int) Math.ceil(posY + radius),
        (int) Math.ceil(posZ + radius));

    double radiusSqrt3Over3 = radius * Math.sqrt(3) / 3;
    inscribed = new Cuboid(domain,
        (int) Math.ceil(posX - radiusSqrt3Over3),
        (int) Math.ceil(posY - radiusSqrt3Over3),
        (int) Math.ceil(posZ - radiusSqrt3Over3),
        (int) Math.floor(posX + radiusSqrt3Over3),
        (int) Math.floor(posY + radiusSqrt3Over3),
        (int) Math.floor(posZ + radiusSqrt3Over3));

  }

  private static Vector3d sphericalToCartesian(double x, double y, double z, double rho, double theta, double phi) {
    return Vector3d.of(x + rho * Math.sin(theta) * Math.sin(phi),
        y + rho * Math.cos(phi),
        z + rho * Math.cos(theta) * Math.sin(phi));
  }

  public int posX() {
    return this.posX;
  }

  public int posY() {
    return this.posY;
  }

  public int posZ() {
    return this.posZ;
  }

  public double radius() {
    return this.radius;
  }

  @Override
  public @NotNull Cuboid circumscribed() {
    return circumscribed;
  }

  @Override
  public @NotNull Cuboid inscribed() {
    return null;
  }

  @Override
  public boolean containsPoint(double x, double y, double z) {
    return (posX - x) * (posX - x) + (posY - y) * (posY - y) + (posZ - z) * (posZ - z) <= radiusSquared;
  }

  @Override
  public boolean containsBlock(int x, int y, int z) {
    int posXplus1Squared = (posX + 1 - x) * (posX + 1 - x);
    int posYplus1Squared = (posY + 1 - y) * (posY + 1 - y);
    int posZplus1Squared = (posZ + 1 - z) * (posZ + 1 - z);
    return (posX - x) * (posX - x) + (posY - y) * (posY - y) + (posZ - z) * (posZ - z) <= radiusSquared
        || posXplus1Squared + (posY - y) * (posY - y) + (posZ - z) * (posZ - z) <= radiusSquared
        || (posX - x) * (posX - x) + posYplus1Squared + (posZ - z) * (posZ - z) <= radiusSquared
        || (posX - x) * (posX - x) + (posY - y) * (posY - y) + posZplus1Squared <= radiusSquared
        || posXplus1Squared + posYplus1Squared + (posZ - z) * (posZ - z) <= radiusSquared
        || posXplus1Squared + (posY - y) * (posY - y) + posZplus1Squared <= radiusSquared
        || (posX - x) * (posX - x) + posYplus1Squared + posZplus1Squared <= radiusSquared
        || posXplus1Squared + posYplus1Squared + posZplus1Squared <= radiusSquared;
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
        for (double theta = thetaStart - halfThetaRange; theta < thetaStart + halfThetaRange; theta += angleInc) {
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
