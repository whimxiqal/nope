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

package me.pietelite.nope.sponge.tool;

import com.google.common.collect.Lists;
import java.util.List;
import me.pietelite.nope.common.math.Cylinder;
import me.pietelite.nope.sponge.util.Formatter;
import net.kyori.adventure.text.Component;

/**
 * A {@link Selection} of a {@link Cylinder}.
 */
public class CylinderSelection extends Selection<Cylinder> {

  @SuppressWarnings("")
  private float radius() {
    // choose a radius that makes it so the entire position block is encompassed
    double distSquaredX1 = (position1.x() - position2.x()) * (position1.x() - position2.x());
    double distSquaredX2 = (position1.x() - position2.x() - 1) * (position1.x() - position2.x() - 1);
    double distSquaredZ1 = (position1.z() - position2.z()) * (position1.z() - position2.z());
    double distSquaredZ2 = (position1.z() - position2.z() - 1) * (position1.z() - position2.z() - 1);
    List<Double> radii = Lists.newArrayList(
        distSquaredX1 + distSquaredZ1, distSquaredX2 + distSquaredZ1,
        distSquaredX1 + distSquaredZ2, distSquaredX2 + distSquaredZ2);
    return (float) Math.sqrt(radii.stream().max(Double::compare).orElse(0d));
  }

  @Override
  protected Component propsWhenValid() {
    double height = Math.abs(position1.y() - position2.y()) + 1;
    double radius = radius();
    return Formatter.info("Volume: ___, Center: {x ___, z ___}, Height: ___, Radius: ___",
        (int) Math.ceil(Math.PI * radius * radius * height),
        position1.x(), position1.z(),
        height,
        (int) Math.ceil(radius));
  }

  @Override
  public Cylinder construct() {
    return new Cylinder(domain,
        position1.x() + 0.5f,
        (float) Math.min(position1.y(), position2.y()),
        Math.max(position1.y(), position2.y()) + 1f,
        position1.z() + 0.5f,
        radius());
  }

}
