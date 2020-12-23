/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
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

package com.minecraftonline.nope.host;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.nope.SettingLibrary;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * A generic interface for all Host interaction. Generally, in the
 * context of Nope, this means managing all data which has to
 */
public interface HostTree {

  /**
   * Load all data from some specified storage.
   */
  void load();

  /**
   * Save all data to some specified storage.
   */
  void save();

  @Nonnull
  Host getGlobalHost();

  @Nullable
  Host getWorldHost(UUID worldUuid);

  @Nullable
  VolumeHost getRegion(String name);

  /**
   * Add a region to the HostTree with the given parameters.
   * The name must be unique. This method fails if it is called with a name
   * which is already in use.
   *
   * @param worldUuid the uuid of the world in which this region resides
   * @param name      the unique name of this region, which cannot be formatted like a WorldHost name
   * @param pos1      a point which defines this region
   * @param pos2      another point which defines this region
   * @param priority  a priority level. The higher the priority, the larger the precedence.
   *                  Two intersecting regions may not have the same priority level.
   * @return the created region
   * @throws IllegalArgumentException if the inputs will lead to an invalid HostTree state,
   *                                  like if the name is not unique or the priority is the same
   *                                  as an overlapping region
   */
  @Nullable
  Host addRegion(UUID worldUuid, String name, Vector3i pos1, Vector3i pos2, int priority)
          throws IllegalStateException;

  /**
   * Remove a region from the given world. This method fails if it is called
   * with a name which is not in use.
   *
   * @param worldUuid the uuid of the world from which to remove
   * @param name      the name of the region which to remove
   * @return the removed region
   */
  @Nullable
  Host removeRegion(String name);

  /**
   * Check if a given world has a region called a given name.
   *
   * @param name the name of the region for which to check
   * @return true if a region exists, false if not
   */
  boolean hasRegion(String name);

  /**
   * Find the value corresponding to this setting dependent on whether
   * this location is inside a host, such as a Region or a World.
   * This is the most important function of the HostTree.
   *
   * @param setting  the setting, obtained from the SettingLibrary
   * @param location the location to query
   * @param <V>      the type of value to retrieve
   * @return the value corresponding to this setting
   * @see SettingLibrary
   */
  <V> V lookup(SettingLibrary.Setting<V> setting, Location<World> location);

}
