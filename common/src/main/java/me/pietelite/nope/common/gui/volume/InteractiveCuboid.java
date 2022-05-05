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

package me.pietelite.nope.common.gui.volume;

import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Cuboid;
import me.pietelite.nope.common.struct.Direction;

public class InteractiveCuboid extends InteractiveVolume<Cuboid> {

  public InteractiveCuboid(Scene scene, Cuboid volume, InteractiveVolumeInfo info, int minimumDimension) {
    super(scene, volume, info, minimumDimension);
  }

  @Override
  protected void expand(Direction direction, float count) {
    switch (direction) {
      case X_NEG:
        update(new Cuboid(volume().domain(), volume().minX() - count, volume().minY(), volume().minZ(),
            volume().maxX(), volume().maxY(), volume().maxZ()));
        break;
      case Y_NEG:
        update(new Cuboid(volume().domain(), volume().minX(), volume().minY() - count, volume().minZ(),
            volume().maxX(), volume().maxY(), volume().maxZ()));
        break;
      case Z_NEG:
        update(new Cuboid(volume().domain(), volume().minX(), volume().minY(), volume().minZ() - count,
            volume().maxX(), volume().maxY(), volume().maxZ()));
        break;
      case X_POS:
        update(new Cuboid(volume().domain(), volume().minX(), volume().minY(), volume().minZ(),
            volume().maxX() + count, volume().maxY(), volume().maxZ()));
        break;
      case Y_POS:
        update(new Cuboid(volume().domain(), volume().minX(), volume().minY(), volume().minZ(),
            volume().maxX(), volume().maxY() + count, volume().maxZ()));
        break;
      case Z_POS:
        update(new Cuboid(volume().domain(), volume().minX(), volume().minY(), volume().minZ(),
            volume().maxX(), volume().maxY(), volume().maxZ() + count));
        break;
      default:
        //ignore
    }
  }

  @Override
  protected void contract(Direction direction, float count) {
    switch (direction) {
      case X_NEG:
        update(new Cuboid(volume().domain(),
            volume().minX(),
            volume().minY(),
            volume().minZ(),
            Math.max(volume().minX() + minimumDimension, volume().maxX() - count),
            volume().maxY(),
            volume().maxZ()));
        break;
      case Y_NEG:
        update(new Cuboid(volume().domain(),
            volume().minX(),
            volume().minY(),
            volume().minZ(),
            volume().maxX(),
            Math.max(volume().minY() + minimumDimension, volume().maxY() - count),
            volume().maxZ()));
        break;
      case Z_NEG:
        update(new Cuboid(volume().domain(),
            volume().minX(),
            volume().minY(),
            volume().minZ(),
            volume().maxX(),
            volume().maxY(),
            Math.max(volume().minZ() + minimumDimension, volume().maxZ() - count)));
        break;
      case X_POS:
        update(new Cuboid(volume().domain(),
            Math.min(volume().maxX() - minimumDimension, volume().minX() + count),
            volume().minY(),
            volume().minZ(),
            volume().maxX(),
            volume().maxY(),
            volume().maxZ()));
        break;
      case Y_POS:
        update(new Cuboid(volume().domain(),
            volume().minX(),
            Math.min(volume().maxY() - minimumDimension, volume().minY() + count),
            volume().minZ(),
            volume().maxX(),
            volume().maxY(),
            volume().maxZ()));
        break;
      case Z_POS:
        update(new Cuboid(volume().domain(),
            volume().minX(),
            volume().minY(),
            Math.min(volume().maxZ() - minimumDimension, volume().minZ() + count),
            volume().maxX(),
            volume().maxY(),
            volume().maxZ()));
        break;
      default:
        //ignore
    }
  }

}
