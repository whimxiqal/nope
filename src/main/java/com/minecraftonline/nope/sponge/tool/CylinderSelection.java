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

package com.minecraftonline.nope.sponge.tool;

import com.minecraftonline.nope.common.math.Cylinder;
import com.minecraftonline.nope.sponge.util.Formatter;
import net.kyori.adventure.text.Component;

public class CylinderSelection extends Selection<Cylinder> {

  private double radius() {
    return Math.sqrt((position1.x() - position2.x()) * (position1.x() - position2.x())
        + (position1.z() - position2.z()) * (position1.z() - position2.z()));
  }

  @Override
  protected Component propsWhenValid() {
    double height = Math.abs(position1.y() - position2.y()) + 1;
    double radius = radius();
    return Formatter.info("Volume: ~___, Center: {x ___, z ___}, Height: ___, Radius: ___",
        (int) Math.ceil(Math.PI * radius * radius * height),
        position1.x(), position1.z(),
        height,
        (int) Math.ceil(radius));
  }

  @Override
  public Cylinder construct() {
    return new Cylinder(domain,
        position1.x(),
        Math.min(position1.y(), position2.y()),
        Math.max(position1.y(), position2.y()) + 1,
        position1.z(),
        radius());
  }

}
