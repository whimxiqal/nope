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

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Data
public class Vector2d {
  @Getter
  @Accessors(fluent = true)
  private final double posX;
  @Getter
  @Accessors(fluent = true)
  private final double posZ;

  private Vector2d(double posX, double posZ) {
    this.posX = posX;
    this.posZ = posZ;
  }

  public static Vector2d of(double posX, double posZ) {
    return new Vector2d(posX, posZ);
  }

  public double distanceSquared(Vector2d other) {
    double lengthX = this.posX - other.posX;
    double lengthZ = this.posZ - other.posZ;
    return lengthX * lengthX + lengthZ * lengthZ;
  }

  public double length() {
    return Math.sqrt(posX * posX + posZ * posZ);
  }

  public Vector2d normalize() {
    double length = length();
    return new Vector2d(posX / length, posZ / length);
  }

  public Vector2d plus(Vector2d other) {
    return new Vector2d(posX + other.posX, posZ + other.posZ);
  }

  public Vector2d minus(Vector2d other) {
    return new Vector2d(posX - other.posX, posZ - other.posZ);
  }

  public Vector2d times(double value) {
    return new Vector2d(posX * value, posZ * value);
  }
}
