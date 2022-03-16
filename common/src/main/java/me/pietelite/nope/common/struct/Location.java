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
@Getter
@Accessors(fluent = true)
public class Location {

  private final double posX;
  private final double posY;
  private final double posZ;
  private final Domain domain;

  public Location(int x, int y, int z, Domain domain) {
    this.posX = x;
    this.posY = y;
    this.posZ = z;
    this.domain = domain;
  }

  public Location(double x, double y, double z, Domain domain) {
    this.posX = x;
    this.posY = y;
    this.posZ = z;
    this.domain = domain;
  }

  public int getBlockX() {
    return (int) Math.floor(posX);
  }

  public int getBlockY() {
    return (int) Math.floor(posY);
  }

  public int getBlockZ() {
    return (int) Math.floor(posZ);
  }

  public Vector3d vector3d() {
    return Vector3d.of(posX, posY, posZ);
  }
}
