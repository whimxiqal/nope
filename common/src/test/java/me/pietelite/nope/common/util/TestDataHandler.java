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

package me.pietelite.nope.common.util;

import java.util.Collection;
import java.util.Collections;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.host.HostSystem;
import me.pietelite.nope.common.host.Universe;
import me.pietelite.nope.common.host.Zone;
import me.pietelite.nope.common.storage.DataHandler;
import me.pietelite.nope.common.storage.DomainDataHandler;
import me.pietelite.nope.common.storage.UniverseDataHandler;
import me.pietelite.nope.common.storage.ZoneDataHandler;
import org.jetbrains.annotations.NotNull;

/**
 * A test {@link DataHandler}.
 */
public class TestDataHandler implements DataHandler {
  @Override
  public UniverseDataHandler universe() {
    return new UniverseDataHandler() {
      @Override
      public void save(Universe universe) {
        // ignore
      }

      @Override
      public Universe load() {
        return new Universe("universe");
      }
    };
  }

  @Override
  public DomainDataHandler domains() {
    return new DomainDataHandler() {
      @Override
      public void save(@NotNull Domain domain) {
        // ignore
      }

      @Override
      public void load(@NotNull Domain domain) {
        // ignore
      }
    };
  }

  @Override
  public ZoneDataHandler zones() {
    return new ZoneDataHandler() {
      @Override
      public void destroy(Zone zone) {
        // ignore
      }

      @Override
      public void save(Zone zone) {
        // ignore
      }

      @Override
      public Collection<Zone> load() {
        return Collections.emptyList();
      }
    };
  }

  @Override
  public HostSystem loadSystem() {
    return Nope.instance().hostSystem();
  }
}
