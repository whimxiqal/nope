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

package me.pietelite.nope.common.host;

import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.struct.Location;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Class for managing the few WorldHosts in this HostTree.
 */
public class Domain extends Host implements Domained {

  @Getter()
  @Accessors(fluent = true)
  private final VolumeTree volumes;

  public Domain(String name, int cacheSize) {
    super(name, -1);
    if (cacheSize < 0) {
      throw new RuntimeException("The cache size must be greater than 0");
    } else if (cacheSize == 0) {
      this.volumes = new VolumeTree(true);
    } else {
      FlexibleHashQueueVolumeTree flexVolumeTree = new FlexibleHashQueueVolumeTree(cacheSize);
      Nope.instance().scheduleAsyncIntervalTask(flexVolumeTree::trim, 1, TimeUnit.SECONDS);
      this.volumes = flexVolumeTree;
    }
  }

  @Override
  public boolean contains(Location location) {
    return this.equals(location.domain());
  }

  @Override
  public void save() {
    Nope.instance().data().domains().save(this);
  }

  @Override
  public Domain domain() {
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    Domain domain = (Domain) o;
    return name().equals(domain.name());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), name());
  }
}
