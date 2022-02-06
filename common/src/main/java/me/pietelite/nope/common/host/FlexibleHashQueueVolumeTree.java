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

import me.pietelite.nope.common.math.Volume;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * An extension of {@link VolumeTree} with cached queries to
 * optimize lookup time.
 */
public class FlexibleHashQueueVolumeTree extends VolumeTree {

  private final Map<Query, Set<Zone>> cache = new ConcurrentHashMap<>();
  private final Queue<Query> history = new ConcurrentLinkedQueue<>();
  private final int size;

  public FlexibleHashQueueVolumeTree(int size) {
    super();
    this.size = size;
  }

  @NotNull
  @Override
  public Set<Zone> containing(int x, int y, int z) {
    Query query = Query.of(x, y, z);
    if (cache.containsKey(query)) {
      return cache.get(query);
    }
    Set<Zone> zones = super.containing(x, y, z);
    cache.put(query, zones);
    history.add(query);
    return zones;
  }

  public int getCacheSize() {
    return history.size();
  }

  @Override
  public void put(Volume volume, Zone zone, boolean construct) {
    cache.clear();
    history.clear();
    super.put(volume, zone, construct);
  }

  @Override
  public Volume remove(Volume volume, boolean construct) {
    cache.clear();
    history.clear();
    return super.remove(volume, construct);
  }

  /**
   * Remove values from this data structure until
   * the size is at its soft maximum.
   */
  public void trim() {
    Queue<Query> deletionStage = new ConcurrentLinkedQueue<>();
    while (history.size() > size) {
      deletionStage.add(history.remove());
    }
    while (!deletionStage.isEmpty()) {
      cache.remove(deletionStage.remove());
    }
  }

  @Data(staticConstructor = "of")
  private static class Query {
    private final int posX;
    private final int posY;
    private final int posZ;

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
      int result = posX ^ (posX >>> 16);
      result = 31 * result + (posY ^ (posY >>> 16));
      result = 31 * result + (posZ ^ (posZ >>> 16));
      return result;
    }

  }
}
