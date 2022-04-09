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
import lombok.Getter;
import lombok.experimental.Accessors;
import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.host.Domained;
import me.pietelite.nope.common.struct.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A <a href="https://en.wikipedia.org/wiki/Volume">Volume</a>.
 */
@Accessors(fluent = true)
public abstract class Volume implements Domained {

  @Getter
  protected final Domain domain;
  @Getter
  @Nullable
  private String name;

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

  /**
   * Whether this volume contains a point within it.
   *
   * @param location the location
   * @return true if the volume contains it
   */
  public final boolean containsPoint(Location location) {
    return domain.equals(location.domain()) && this.containsPoint(
        location.getBlockX(),
        location.getBlockY(),
        location.getBlockZ());
  }

  /**
   * Whether this volume contains a point within it.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param z the z coordinate
   * @return true if the volume contains it
   */
  public abstract boolean containsPoint(double x, double y, double z);

  /**
   * Whether this volume contains a point within it,
   * assuming that the domain is the same.
   *
   * @param vector3d the point
   * @return true if the volume contains it
   */
  public final boolean containsPoint(@NotNull Vector3d vector3d) {
    return containsPoint(vector3d.x(), vector3d.y(), vector3d.z());
  }

  /**
   * Whether this volume contains an entire 1-unit-cubed block within it.
   *
   * @param x the (starting) x coordinate
   * @param y the (starting) y coordinate
   * @param z the (starting) z coordinate
   * @return true if it contains the block
   */
  public abstract boolean containsBlock(int x, int y, int z);

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

}
