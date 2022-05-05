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

package me.pietelite.nope.common.api.edit;

import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Cuboid;

public class CuboidEditorImpl extends ZoneEditorImpl<Cuboid> implements CuboidEditor {

  public CuboidEditorImpl(Scene scene, int index) {
    super(scene, index, Cuboid.class);
  }

  @Override
  public void setDimensions(float x1, float y1, float z1, float x2, float y2, float z2) {
    update(new Cuboid(volume.domain(), x1, y1, z1, x2, y2, z2));
  }

  @Override
  public float minX() {
    return volume().minX();
  }

  @Override
  public float minY() {
    return volume().minY();
  }

  @Override
  public float minZ() {
    return volume().minZ();
  }

  @Override
  public float maxX() {
    return volume().maxX();
  }

  @Override
  public float maxY() {
    return volume().maxY();
  }

  @Override
  public float maxZ() {
    return volume().maxZ();
  }

}
