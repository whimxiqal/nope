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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A class to map objects into groups.
 */
public class GroupMap<G, T> implements Multimap<G, T> {

  private final HashMultimap<G, T> multimap = HashMultimap.create();
  private final Map<T, G> backwardsMap = new HashMap<>();

  /**
   * Immutable of set.
   *
   * @param t an element of the group
   * @return an immutable set of the entire group, or null if it doesn't exist
   */
  @Nullable
  public G getGroupOf(T t) {
    return backwardsMap.get(t);
  }

  /**
   * Groups both elements into the same group and returns the group.
   * The first input's group will take priority if these elements
   * are both already grouped in separate groups.
   *
   * @param t1        the first element to group, which takes priority
   * @param t2        the second element to group
   * @param generator the generator for a group key, which is used if
   *                  a new group is generated as a result of neither
   *                  elements being grouped yet.
   * @return the group that both elements are now in,
   *         or null if both elements were already grouped to the same group.
   */
  @Nullable
  public G group(T t1, T t2, Supplier<G> generator) {
    G g1 = getGroupOf(t1);
    G g2 = getGroupOf(t2);
    if (g1 != null && g1.equals(g2)) {
      return null;
    }
    if (g1 == null) {
      if (g2 == null) {
        // Neither have a group. Create a new one.
        g1 = generator.get();  // Reuse g1 because it wasn't being used
        put(g1, t1);
        put(g1, t2);
        return g1;
      } else {
        // Only the second has a group. Add the first
        put(g2, t1);
        return g2;
      }
    } else {
      if (g2 == null) {
        // Only the first has a group. Add the second
        put(g1, t2);
      } else {
        // Both have a group. Merge the second into the first
        Collection<T> elements2 = removeAll(g2);
        putAll(g1, elements2);
      }
      return g1;
    }
  }

  @Override
  public int size() {
    return multimap.size();
  }

  @Override
  public boolean isEmpty() {
    return multimap.isEmpty();
  }

  @Override
  public boolean containsKey(@Nullable Object o) {
    return multimap.containsKey(o);
  }

  @Override
  public boolean containsValue(@Nullable Object o) {
    return backwardsMap.containsKey(o);
  }

  @Override
  public boolean containsEntry(@Nullable Object o, @Nullable Object o1) {
    return multimap.containsEntry(o, o1);
  }

  @Override
  public boolean put(@Nullable G g, @Nullable T t) {
    backwardsMap.put(t, g);
    return multimap.put(g, t);
  }

  @Override
  public boolean remove(@Nullable Object o, @Nullable Object o1) {
    backwardsMap.remove(o1, o);
    return multimap.remove(o, o1);
  }

  @Override
  public boolean putAll(@Nullable G g, @Nonnull Iterable<? extends T> iterable) {
    iterable.forEach(t -> backwardsMap.put(t, g));
    return multimap.putAll(g, iterable);
  }

  @Override
  public boolean putAll(@Nonnull Multimap<? extends G, ? extends T> multimap) {
    multimap.forEach((g, t) -> backwardsMap.put(t, g));
    return this.multimap.putAll(multimap);
  }

  @Override
  public Collection<T> replaceValues(@Nullable G g, @Nonnull Iterable<? extends T> iterable) {
    Collection<T> replaced = multimap.replaceValues(g, iterable);
    replaced.forEach(backwardsMap::remove);
    iterable.forEach(t -> backwardsMap.put(t, g));
    return replaced;
  }

  @Override
  public Collection<T> removeAll(@Nullable Object o) {
    Collection<T> removed = multimap.removeAll(o);
    removed.forEach(backwardsMap::remove);
    return removed;
  }

  @Override
  public void clear() {
    backwardsMap.clear();
    multimap.clear();
  }

  @Override
  public Collection<T> get(@Nullable G g) {
    return multimap.get(g);
  }

  /**
   * Immutable of keySet().
   *
   * @return set of keys
   */
  @Override
  public Set<G> keySet() {
    return ImmutableSet.copyOf(multimap.keySet());
  }

  /**
   * Immutable of keys().
   *
   * @return keys
   */
  @Override
  public Multiset<G> keys() {
    return ImmutableMultiset.copyOf(multimap.keys());
  }

  /**
   * Immutable of values.
   *
   * @return values
   */
  @Override
  public Collection<T> values() {
    return ImmutableList.copyOf(multimap.values());
  }

  /**
   * Immutable of entries.
   *
   * @return entries
   */
  @Override
  public Collection<Map.Entry<G, T>> entries() {
    return ImmutableList.copyOf(multimap.entries());
  }

  /**
   * Immutable of map.
   *
   * @return map
   */
  @Override
  public Map<G, Collection<T>> asMap() {
    return new HashMap<>(multimap.asMap());
  }

}
