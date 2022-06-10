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
import me.pietelite.nope.common.math.Sphere;
import me.pietelite.nope.sponge.util.Formatter;
import net.kyori.adventure.text.Component;

/**
 * A {@link Selection} for a {@link Sphere}.
 */
public class SphereSelection extends Selection<Sphere> {

  @SuppressWarnings("checkstyle:LocalVariableName")
  private float radius() {
    // choose a radius that makes it so the entire position block is encompassed
    double xDistSquared1 = (position1.x() - position2.x()) * (position1.x() - position2.x());
    double xDistSquared2 = (position1.x() - position2.x() - 1) * (position1.x() - position2.x() - 1);
    double yDistSquared1 = (position1.y() - position2.y()) * (position1.y() - position2.y());
    double yDistSquared2 = (position1.y() - position2.y() - 1) * (position1.y() - position2.y() - 1);
    double zDistSquared1 = (position1.z() - position2.z()) * (position1.z() - position2.z());
    double zDistSquared2 = (position1.z() - position2.z() - 1) * (position1.z() - position2.z() - 1);
    List<Double> radii = Lists.newArrayList(
        xDistSquared1 + yDistSquared1 + zDistSquared1,
        xDistSquared2 + yDistSquared1 + zDistSquared1,
        xDistSquared1 + yDistSquared2 + zDistSquared1,
        xDistSquared1 + yDistSquared1 + zDistSquared2,
        xDistSquared2 + yDistSquared2 + zDistSquared1,
        xDistSquared2 + yDistSquared1 + zDistSquared2,
        xDistSquared1 + yDistSquared2 + zDistSquared2,
        xDistSquared2 + yDistSquared2 + zDistSquared2);
    return (float) Math.sqrt(radii.stream().max(Double::compare).orElse(0d));
  }

  @Override
  protected Component propsWhenValid() {
    double radius = radius();
    return Formatter.info("Volume: ~___, Center: {x ___, y ___, z ___}, Radius: ~___",
        (int) Math.ceil(Math.PI * radius * radius * radius * 4 / 3),
        position1.x(), position1.y(), position1.z(),
        (int) Math.ceil(radius));
  }

  @Override
  public Sphere construct() {
    return new Sphere(domain,
        (float) position1.x(), (float) position1.y(), (float) position1.z(),
        radius());
  }
}
