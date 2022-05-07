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

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.math.Cuboid;
import me.pietelite.nope.common.math.Cylinder;
import me.pietelite.nope.common.math.Geometry;
import me.pietelite.nope.common.math.Slab;
import me.pietelite.nope.common.math.Sphere;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.common.util.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VolumeTreeTest {

  VolumeTree volumeTree;
  Domain domain;
  Volume volume1;
  Volume volume2;
  Scene scene1;
  Volume volume3;
  Volume volume4;

  @BeforeEach
  void setUp() {
    Nope.instance(new TestNope());
    volumeTree = new VolumeTree();
    domain = new Domain("domain", 100);
    volume1 = new Cuboid(domain, 0f, 0f, 0f, 10f, 10f, 10f);
    volume2 = new Sphere(domain, 20f, 20f, 20f, 5f);
    scene1 = new Scene(Nope.NOPE_SCOPE, "scene1", 0);
    volume3 = new Slab(domain, -1f, 1f);
    volume4 = new Cylinder(domain, 0f, 30f, 40f, 0f, 5f);
    volumeTree.put(volume1, scene1, false);
    volumeTree.put(volume2, scene1, false);
    volumeTree.construct();
  }

  @Test
  void intersecting() {
    assert Geometry.intersects(volume1, volume3);
    assert volumeTree.intersecting(volume3).contains(scene1);
    assert !volumeTree.intersecting(volume4).contains(scene1);
  }

  @Test
  void containing() {
    assert volumeTree.containing(1, 1, 1).contains(scene1);
    assert !volumeTree.containing(-1, 1, 1).contains(scene1);
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