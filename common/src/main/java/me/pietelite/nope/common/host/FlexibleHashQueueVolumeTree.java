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

import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Data;
import me.pietelite.nope.common.math.Volume;
import org.jetbrains.annotations.NotNull;

/**
 * An extension of {@link VolumeTree} with cached queries to
 * optimize lookup time.
 */
public class FlexibleHashQueueVolumeTree extends VolumeTree {

  private final Map<Query, Set<Scene>> cache = new ConcurrentHashMap<>();
  private final Queue<Query> history = new ConcurrentLinkedQueue<>();
  private final int size;
  private final ReentrantLock lock = new ReentrantLock();

  /**
   * Generic constructor.
   *
   * @param size the maximum size of the queue
   */
  public FlexibleHashQueueVolumeTree(int size) {
    super();
    this.size = size;
  }

  @NotNull
  @Override
  public Set<Scene> containing(float x, float y, float z) {
    Query query = Query.of(x, y, z);
    synchronized (lock) {
      if (cache.containsKey(query)) {
        return cache.get(query);
      }
      Set<Scene> scenes = super.containing(x, y, z);
      cache.put(query, scenes);
      history.add(query);
      return scenes;
    }
  }

  @Override
  public void put(Volume volume, Scene scene, boolean construct) {
    synchronized (lock) {
      cache.clear();
      history.clear();
      super.put(volume, scene, construct);
    }
  }

  @Override
  public Volume remove(Volume volume, boolean construct) {
    synchronized (lock) {
      cache.clear();
      history.clear();
      return super.remove(volume, construct);
    }
  }

  /**
   * Remove values from this data structure until
   * the size is at its soft maximum.
   */
  public void trim() {
    synchronized (lock) {
      Queue<Query> deletionStage = new ConcurrentLinkedQueue<>();
      while (history.size() > size) {
        deletionStage.add(history.remove());
      }
      while (!deletionStage.isEmpty()) {
        cache.remove(deletionStage.remove());
      }
    }
  }

  @Data(staticConstructor = "of")
  private static class Query {
    private final float posX;
    private final float posY;
    private final float posZ;

    @Override
    public boolean equals(Object other) {
      if (other == null || getClass() != other.getClass()) {
        return false;
      }

      return (this.posX == ((Query) other).posX)
          && (this.posY == ((Query) other).posY)
          && (this.posZ == ((Query) other).posZ);
    }

    @Override
    public int hashCode() {
      return Objects.hash(posX, posY, posZ);
    }

  }
}
