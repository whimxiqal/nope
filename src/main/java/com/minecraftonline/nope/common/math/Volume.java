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
import com.minecraftonline.nope.common.host.Domained;
import com.minecraftonline.nope.common.struct.Location;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Volume implements Domained {

  @Getter
  @Accessors(fluent = true)
  protected final Domain domain;
  @Getter
  @Accessors(fluent = true)
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

  public final boolean contains(Location location) {
    return domain.equals(location.domain()) && this.contains(
        location.getBlockX(),
        location.getBlockY(),
        location.getBlockZ());
  }

  public abstract boolean contains(int x, int y, int z);

  public abstract boolean valid();

  public abstract List<Vector3d> surfacePointsNear(Vector3d point, double proximity, double density);

}
