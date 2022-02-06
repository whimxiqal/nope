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

import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.host.Domained;
import me.pietelite.nope.common.struct.Location;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

  public final boolean containsPoint(Location location) {
    return domain.equals(location.domain()) && this.containsPoint(
        location.getBlockX(),
        location.getBlockY(),
        location.getBlockZ());
  }

  public abstract boolean containsPoint(double x, double y, double z);

  public final boolean containsPoint(@NotNull Vector3d vector3d) {
    return containsPoint(vector3d.x(), vector3d.y(), vector3d.z());
  }

  public abstract boolean containsBlock(int x, int y, int z);

  public abstract boolean valid();

  public abstract List<Vector3d> surfacePointsNear(Vector3d point, double proximity, double density);

}
