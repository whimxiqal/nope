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

package me.pietelite.nope.common.struct;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.math.Vector3d;

/**
 * A generic Minecraft location.
 */
public class Location {

  private final float posX;
  private final float posY;
  private final float posZ;
  private final Domain domain;

  /**
   * Generic constructor.
   *
   * @param x      the X coordinate
   * @param y      the Y coordinate
   * @param z      the Z coordinate
   * @param domain the domain
   */
  public Location(float x, float y, float z, Domain domain) {
    this.posX = x;
    this.posY = y;
    this.posZ = z;
    this.domain = domain;
  }

  public Location(Double x, Double y, Double z, Domain domain) {
    this(x.floatValue(), y.floatValue(), z.floatValue(), domain);
  }

  public float posX() {
    return posX;
  }

  public float posY() {
    return posY;
  }

  public float posZ() {
    return posZ;
  }

  public int blockX() {
    return (int) Math.floor(posX);
  }

  public int blockY() {
    return (int) Math.floor(posY);
  }

  public int blockZ() {
    return (int) Math.floor(posZ);
  }

  public Domain domain() {
    return domain;
  }
}
