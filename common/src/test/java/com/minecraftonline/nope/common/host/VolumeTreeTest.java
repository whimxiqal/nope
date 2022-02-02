/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

package com.minecraftonline.nope.common.host;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.host.Domain;
import com.minecraftonline.nope.common.host.VolumeTree;
import com.minecraftonline.nope.common.host.Zone;
import com.minecraftonline.nope.common.math.Cuboid;
import com.minecraftonline.nope.common.math.Cylinder;
import com.minecraftonline.nope.common.math.Geometry;
import com.minecraftonline.nope.common.math.Slab;
import com.minecraftonline.nope.common.math.Sphere;
import com.minecraftonline.nope.common.math.Volume;
import com.minecraftonline.nope.common.util.Logger;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VolumeTreeTest {

  VolumeTree volumeTree;
  Domain domain;
  Volume volume1;
  Volume volume2;
  Zone zone1;
  Volume volume3;
  Volume volume4;

  @BeforeEach
  void setUp() {
    Nope.instance(new TestNope());
    volumeTree = new VolumeTree();
    domain = new Domain("domain", 100);
    volume1 = new Cuboid(domain, 0, 0, 0, 10, 10, 10);
    volume2 = new Sphere(domain, 20, 20, 20, 5d);
    zone1 = new Zone("zone1", null, 0, volume1, volume2);
    volume3 = new Slab(domain, -1, 1);
    volume4 = new Cylinder(domain, 0, 30, 40, 0, 5d);
    volumeTree.put(volume1, zone1, false);
    volumeTree.put(volume2, zone1, false);
    volumeTree.construct();
  }

  @Test
  void intersecting() {
    assert Geometry.intersects(volume1, volume3);
    assert volumeTree.intersecting(volume3).contains(zone1);
    assert !volumeTree.intersecting(volume4).contains(zone1);
  }

  @Test
  void containing() {
    assert volumeTree.containing(1, 1, 1).contains(zone1);
    assert !volumeTree.containing(-1, 1, 1).contains(zone1);
  }

  private static class TestNope extends Nope {

    public TestNope() {
      super(new TestLogger());
    }

    @Override
    public boolean hasPermission(UUID playerUuid, String permission) {
      return true;
    }

    @Override
    public void scheduleAsyncIntervalTask(Runnable runnable, int interval, TimeUnit intervalUnit) {
      // ignore
    }
  }

  private static class TestLogger implements Logger {

    @Override
    public void error(String string) {
      System.out.println("ERROR: " + string);
    }

    @Override
    public void warn(String string) {
      System.out.println("WARN: " + string);
    }

    @Override
    public void info(String string) {
      System.out.println("INFO: " + string);
    }
  }
}