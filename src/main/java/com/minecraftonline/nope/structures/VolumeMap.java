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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The data structure for holding information about 3D volumes.
 *
 * @param <S> the type of key with which to store the volumes
 * @param <T> the type of volume
 */
public interface VolumeMap<S, T extends Volume> {

  /**
   * Get all volumes that contain this point in 3D space,
   * including the boundaries of the volumes.
   *
   * @param x x value
   * @param y y value
   * @param z z value
   * @return a list of volumes
   */
  @Nonnull
  Collection<T> containersOf(int x, int y, int z);

  /**
   * Add a new volume into the structure.
   * Currently, this reconstructs the entire tree because
   * figuring out how to do it dynamically is too complicated.
   *
   * @param key    the key under which to store and retrieve this volume
   * @param volume the volume
   * @return the volume that was previously under the key, or null if none existed
   */
  @Nullable
  T add(S key, T volume);

  /**
   * Add many volumes into the structure.
   *
   * @param map the keys and volumes
   * @see #add(Object, Volume)
   */
  void addAll(Map<S, T> map);

  /**
   * Remove the key and volume associated with the key.
   *
   * @param key the key to remove from the tree
   * @return the removed key, or null if none existed
   */
  @Nullable
  T remove(S key);

  /**
   * As in {@link Map#keySet()}.
   *
   * @return all keys
   */
  @Nonnull
  @SuppressWarnings("unused")
  Set<S> keySet();

  /**
   * As in {@link Map#get(Object)}.
   *
   * @param key the key with which to retrieve a volume
   * @return the volume, or null if none exists
   */
  @Nullable
  T get(S key);

  /**
   * Return all volumes stored in the structure.
   *
   * @return all volumes
   */
  @Nonnull
  Collection<T> volumes();

  /**
   * Check if the structure contains a mapping with this key.
   *
   * @param key the key to check
   * @return true if contains, false if not
   */
  @SuppressWarnings("unused")
  boolean containsKey(S key);

  /**
   * Return the number of stored volumes.
   *
   * @return how many volumes
   */
  int size();

}
