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
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

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

  @Builder(builderClassName = "Selection",
      buildMethodName = "solidify",
      builderMethodName = "selection",
      toBuilder = true)
  public Sphere(Domain domain,
                Integer posX,
                Integer posY,
                Integer posZ,
                Double radius) {
    super(domain);
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
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
  public boolean contains(int x, int y, int z) {
    return (posX - x) * (posX - x) + (posY - y) * (posY - y) + (posZ - z) * (posZ - z) <= radiusSquared;
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
    return null; // TODO implement
  }

}
