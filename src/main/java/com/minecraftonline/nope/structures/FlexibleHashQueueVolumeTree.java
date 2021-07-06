/*
 * MIT License
 *
 * Copyright (c) 2021 MinecraftOnline
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
 *
 */

package com.minecraftonline.nope.structures;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.Data;

/**
 * An extension of {@link VolumeTree} with cached queries to
 * optimize lookup time.
 *
 * @param <S> the volume key type
 * @param <T> the volume type
 */
public class FlexibleHashQueueVolumeTree<S, T extends Volume> extends VolumeTree<S, T> {

  private final Map<Query, Set<S>> cache = Maps.newConcurrentMap();
  private final Queue<Query> history = new ConcurrentLinkedQueue<>();
  private final int size;

  public FlexibleHashQueueVolumeTree(int size) {
    super();
    this.size = size;
  }

  @Nonnull
  @Override
  public Collection<T> containersOf(int x, int y, int z) {
    Query query = Query.of(x, y, z);
    if (cache.containsKey(query)) {
      return cache.get(query)
          .stream()
          .map(volumes::get)
          .collect(Collectors.toList());
    }
    Set<S> keys = root.findVolumes(x, y, z);
    cache.put(query, keys);
    history.add(query);
    return keys.stream().map(volumes::get).collect(Collectors.toList());
  }

  public int getCacheSize() {
    return history.size();
  }

  @Override
  public T add(S key, T volume) {
    cache.clear();
    history.clear();
    return super.add(key, volume);
  }

  @Override
  public void addAll(Map<S, T> map) {
    cache.clear();
    history.clear();
    super.addAll(map);
  }

  @Override
  public T remove(S key) {
    cache.clear();
    history.clear();
    return super.remove(key);
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
