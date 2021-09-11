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

import lombok.Getter;
import lombok.experimental.Accessors;

public class Vector3d {
  @Getter
  @Accessors(fluent = true)
  private final double posX;
  @Getter
  @Accessors(fluent = true)
  private final double posY;
  @Getter
  @Accessors(fluent = true)
  private final double posZ;

  private Vector3d(double posX, double posY, double posZ) {
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
  }

  public static Vector3d of(double x, double y, double z) {
    return new Vector3d(x, y, z);
  }

  public double distanceSquared(Vector3d other) {
    double lengthX = this.posX - other.posX;
    double lengthY = this.posY - other.posY;
    double lengthZ = this.posZ - other.posZ;
    return lengthX * lengthX + lengthY * lengthY + lengthZ * lengthZ;
  }

  public double distance(Vector3d other) {
    return Math.sqrt(distanceSquared(other));
  }

  public Vector3d plus(Vector2d vector2d) {
    return new Vector3d(posX + vector2d.posX(), posY, posZ + vector2d.posZ());
  }

  public double magnitude() {
    return Math.sqrt(posX * posX + posY * posY + posZ * posZ);
  }

  public Vector3d normalize() {
    double magnitude = magnitude();
    return new Vector3d(posX / magnitude, posY / magnitude, posZ / magnitude);
  }



}
