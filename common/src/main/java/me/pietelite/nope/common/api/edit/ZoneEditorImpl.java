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

import java.util.NoSuchElementException;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Volume;

public abstract class ZoneEditorImpl<V extends Volume> implements ZoneEditor {

  protected final Scene scene;
  protected final int index;
  protected final V volume;

  ZoneEditorImpl(Scene scene, int index, Class<V> volumeClass) {
    scene.verifyExistence();
    this.scene = scene;
    this.index = index;
    Volume v = scene.volumes().get(index);
    if (!volumeClass.isInstance(v)) {
      throw new ClassCastException("The requested volume type was not found at index " + index);
    }
    this.volume = volumeClass.cast(v);
  }

  @Override
  public final String domain() {
    volume.verifyExistence();
    return volume.domain().name();
  }

  @Override
  public final Alteration domain(String domainName) {
    volume.verifyExistence();
    Domain domain = Nope.instance().system().domains().get(domainName);
    if (domain == null) {
      throw new NoSuchElementException("There is no domain with name " + domainName);
    }
    return setDomainObject(domain);
  }

  protected abstract Alteration setDomainObject(Domain domain);

  protected final void update(Volume newVolume) {
    scene.verifyExistence();
    volume.verifyExistence();
    volume.domain().volumes().remove(volume, false);
    volume.domain().volumes().put(newVolume, scene, true);
    volume.markDestroyed();
    scene.volumes().set(index, newVolume);
    scene.save();
  }

}
