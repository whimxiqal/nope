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

import me.pietelite.nope.common.math.Sphere;
import me.pietelite.nope.sponge.util.Formatter;
import net.kyori.adventure.text.Component;

public class SphereSelection extends Selection<Sphere>  {

  private double radius() {
    return Math.sqrt((position1.x() - position2.x()) * (position1.x() - position2.x())
        + (position1.y() - position2.y()) * (position1.y() - position2.y())
        + (position1.z() - position2.z()) * (position1.z() - position2.z()));
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
        position1.x(), position1.y(), position1.z(),
        radius());
  }
}
