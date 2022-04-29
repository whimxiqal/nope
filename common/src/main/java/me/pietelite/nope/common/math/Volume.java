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

import java.util.List;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.pietelite.nope.common.api.edit.ZoneType;
import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.host.Domained;
import me.pietelite.nope.common.storage.Destructible;
import me.pietelite.nope.common.struct.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A <a href="https://en.wikipedia.org/wiki/Volume">Volume</a>.
 */
@Accessors(fluent = true)
public abstract class Volume implements Domained, Destructible {

  @Getter
  protected final Domain domain;
  @Getter
  @Nullable
  private String name;
  private boolean destroyed;

  public Volume(Domain domain) {
    this(null, domain);
  }

  public Volume(@Nullable String name, Domain domain) {
    this.name = name;
    this.domain = domain;
  }

  public void name(String name) {
    this.name = name;
  }

  @NotNull
  public abstract Cuboid circumscribed();

  @NotNull
  public abstract Cuboid inscribed();

  public abstract ZoneType zoneType();

  /**
   * Whether this volume contains a point within it.
   *
   * @param location the location
   * @return true if the volume contains it
   */
  public final boolean containsPoint(Location location) {
    return domain.equals(location.domain()) && this.containsPoint(
        location.posX(),
        location.posY(),
        location.posZ());
  }

  /**
   * Whether this volume contains a point within it.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param z the z coordinate
   * @return true if the volume contains it
   */
  public abstract boolean containsPoint(float x, float y, float z);

  /**
   * Whether this volume contains an entire 1-unit-cubed block within it.
   *
   * @param minX the minimum X value
   * @param minY the minimum Y value
   * @param minZ the minimum Z value
   * @param maxX the maximum X value
   * @param maxY the maximum Y value
   * @param maxZ the maximum Z value
   * @param maxInclusive true if we want to include the maximum values as "inside" the volume
   * @return true if it contains the block
   */
  public boolean containsCuboid(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, boolean maxInclusive) {
    return containsPoint(minX, minY, minZ)
            && containsPoint(maxX, maxY, maxZ)
            && containsPoint(minX, minY, maxZ)
            && containsPoint(minX, maxY, minZ)
            && containsPoint(maxX, minY, maxZ)
            && containsPoint(maxX, maxY, minZ)
            && containsPoint(minX, maxY, maxZ)
            && containsPoint(maxX, minY, minZ);
  }

  /**
   * Whether this volume is internally configured correctly to represent
   * the mathematical construct it's supposed to.
   *
   * @return true if valid
   */
  public abstract boolean valid();

  /**
   * Get points on the surface of the volume, but only ones within a given proximity to the given point.
   * This is for the purpose of visualizing the volume in-game.
   *
   * @param point     the point to which the returned points must be near
   * @param proximity the maximum proximity of the returned points to the input point
   * @param density   the line-density of points
   * @return the list of points
   */
  public abstract List<Vector3d> surfacePointsNear(Vector3d point, double proximity, double density);

  @Override
  public void markDestroyed() {
    this.destroyed = true;
  }

  @Override
  public boolean destroyed() {
    return destroyed;
  }

  @Override
  public void verifyExistence() throws NoSuchElementException {
    if (destroyed()) {
      throw new IllegalStateException("Volume (" + zoneType().name() + ") is destroyed");
    }
  }
}
