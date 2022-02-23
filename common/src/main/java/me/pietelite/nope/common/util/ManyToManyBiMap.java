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
 *
 */

package me.pietelite.nope.common.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A map which can store keys and values multiple times and is reverse-indexed.
 * Thread-safe.
 *
 * @param <A> the key type
 * @param <B> the value type
 */
public class ManyToManyBiMap<A, B> {

  private final Map<A, Set<B>> forwardsMap = new ConcurrentHashMap<>();
  private final Map<B, Set<A>> backwardsMap = new ConcurrentHashMap<>();

  public boolean isEmpty() {
    return forwardsMap.isEmpty();
  }

  public boolean containsKey(A key) {
    return forwardsMap.containsKey(key);
  }

  public boolean containsValue(B value) {
    return backwardsMap.containsKey(value);
  }

  @NotNull
  public Set<B> getValues(A key) {
    if (key == null) {
      return Collections.emptySet();
    } else {
      return Optional.ofNullable(forwardsMap.get(key)).orElse(Collections.emptySet());
    }
  }

  @NotNull
  public Set<A> getKeys(B value) {
    if (value == null) {
      return Collections.emptySet();
    } else {
      return Optional.ofNullable(backwardsMap.get(value)).orElse(Collections.emptySet());
    }
  }

  @Nullable
  public void put(A key, B value) {
    forwardsMap.computeIfAbsent(key, k -> new HashSet<>()).add(value);
    backwardsMap.computeIfAbsent(value, k -> new HashSet<>()).add(key);
  }

  public void removeKey(A key) {
    removeItem(key, forwardsMap, backwardsMap);
  }

  public void removeValue(B value) {
    removeItem(value, backwardsMap, forwardsMap);
  }

  private <X, Y> void removeItem(X key, Map<X, Set<Y>> f, Map<Y, Set<X>> b) {
    Set<Y> removed = f.remove(key);
    if (removed != null) {
      for (Y value : removed) {
        if (b.containsKey(value)) {
          Set<X> set = b.get(value);
          set.remove(key);
          if (set.isEmpty()) {
            b.remove(value);
          }
        }
      }
    }
  }

  public void clear() {
    forwardsMap.clear();
    backwardsMap.clear();
  }

}
