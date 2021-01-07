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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface VolumeCollection<S, T extends Volume> {

  /**
   * Get all volumes that contain this point in 3D space,
   * including the boundary.
   *
   * @param x x value
   * @param y y value
   * @param z z value
   * @return a list of volumes
   */
  @Nonnull
  Collection<T> containersOf(int x, int y, int z);

  /**
   * Push a new volume into the tree.
   * Currently, this reconstructs the entire tree because
   * figuring out how to do it dynamically is too complicated.
   *
   * @param key    the key under which to store and retrieve this volume
   * @param volume the volume
   * @return the volume that was previously under the key, or null if none existed
   */
  @Nullable
  T push(S key, T volume);

  void pushAll(Map<S, T> map);

  /**
   * Remove the key and volume associated with the key.
   *
   * @param key the key to remove from the tree
   * @return the removed key, or null if none existed
   */
  @Nullable
  T remove(S key);

  @Nonnull
  Set<S> keySet();

  @Nullable
  T get(S key);

  @Nonnull
  Collection<T> volumes();

  boolean containsKey(S key);

  int getSize();

  public enum Dimension {
    X, Z
  }

  public enum Comparison {
    MIN, MAX
  }

}
