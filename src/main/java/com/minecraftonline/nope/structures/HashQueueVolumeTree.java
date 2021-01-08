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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.Data;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * A class to further optimize lookup time.
 *
 * @param <S> the volume key type
 * @param <T> the volume type
 */
public class HashQueueVolumeTree<S, T extends Volume> extends VolumeTree<S, T> {

  private final Multimap<Query, S> cache = Multimaps.synchronizedSetMultimap(HashMultimap.create());
  private final Queue<Query> history = new ConcurrentLinkedQueue<>();
  private static final int CACHE_MEMORY = 1 << 22;  // 8 MB
  private static final int CACHE_SIZE = CACHE_MEMORY / ((16 * 2) + (4 * 3) * 2);

  public HashQueueVolumeTree() {
    super();
  }

  @Nonnull
  @Override
  public Collection<T> containersOf(int x, int y, int z) {
    if (this.root == null) {
      throw new IllegalStateException("Root of VolumeTree is not initialized");
    }
    Query query = Query.of(x, y, z);
    if (cache.containsKey(query)) {
      return cache.get(query)
          .stream()
          .map(volumes::get)
          .collect(Collectors.toList());
    }
    Collection<S> keys = root.findVolumes(x, y, z);
    cache.putAll(query, keys);
    history.add(query);
    trim();
    return keys.stream().map(volumes::get).collect(Collectors.toList());
  }

  public int getCacheSize() {
    return history.size();
  }

  @Override
  public T push(S key, T volume) {
    cache.clear();
    history.clear();
    return super.push(key, volume);
  }

  @Override
  public void pushAll(Map<S, T> map) {
    cache.clear();
    history.clear();
    super.pushAll(map);
  }

  @Override
  public T remove(S key) {
    cache.clear();
    history.clear();
    return super.remove(key);
  }

  private void trim() {
    if (history.size() > CACHE_SIZE) {
      cache.removeAll(history.remove());
    }
  }

  @Data(staticConstructor = "of")
  private static class Query {
    private final int posX;
    private final int posY;
    private final int posZ;
  }
}
