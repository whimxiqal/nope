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

import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Slab;

public class SlabEditorImpl extends ZoneEditorImpl<Slab> implements SlabEditor {
  public SlabEditorImpl(Scene scene, int index) {
    super(scene, index, Slab.class);
  }

  @Override
  public Alteration setDimensions(float y, float height) {
    update(new Slab(volume.domain(), y, y + height));
    return AlterationImpl.success("Updated the dimensions of the slab at index "
        + index + " for host " + scene.name());
  }

  @Override
  public float y() {
    return 0;
  }

  @Override
  public float height() {
    return 0;
  }

  @Override
  protected Alteration setDomainObject(Domain domain) {
    update(new Slab(domain, volume.minY(), volume.maxY()));
    return AlterationImpl.success("Updated the domain of the slab at index "
        + index + " for host " + scene.name());
  }
}
