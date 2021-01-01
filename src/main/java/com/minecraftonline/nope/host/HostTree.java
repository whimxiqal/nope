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
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingLibrary;
import com.minecraftonline.nope.setting.SettingValue;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
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

  /**
   * Get the GlobalHost.
   *
   * @return
   */
  @Nonnull
  Host getGlobalHost();

  /**
   * Get the WorldHost associated with this World UUID.
   *
   * @param worldUuid the UUID of the world
   * @return the associated WorldHost, or null if none exists
   */
  @Nullable
  Host getWorldHost(UUID worldUuid);

  /**
   * Get the Region associated with this Region.
   *
   * @param name the name of the region
   * @return the associated region, or null if none exists
   */
  @Nullable
  VolumeHost getRegion(String name);

  /**
   * Get all the region within the world of the given id.
   *
   * @param worldUuid the UUID of the world
   * @return the associated Regions, or null if no world exists with that UUID
   */
  @Nullable
  Collection<VolumeHost> getRegions(UUID worldUuid);

  /**
   * Add a region to the HostTree with the given parameters.
   * The name must be unique. This method fails if it is called with a name
   * which is already in use.
   *
   * @param name      the unique name of this region, which cannot be formatted like a WorldHost name
   * @param worldUuid the uuid of the world in which this region resides
   * @param pos1      a point which defines this region
   * @param pos2      another point which defines this region
   * @param priority  a priority level. The higher the priority, the larger the precedence.
   *                  Two intersecting regions may not have the same priority level.
   * @return the created region
   * @throws IllegalArgumentException if the inputs will lead to an invalid HostTree state,
   *                                  like if the name is not unique or the priority is the same
   *                                  as an overlapping region
   */
  @Nonnull
  Host addRegion(String name, UUID worldUuid, Vector3i pos1, Vector3i pos2, int priority)
      throws IllegalArgumentException;

  /**
   * Updates the information associated with a region.
   * The name must exist. This method fails if it is called with a name
   * which is not in use.
   *
   * @param name      the unique name of this region
   * @param worldUuid the uuid of the world in which this region resides
   * @param pos1      a point which defines this region
   * @param pos2      another point which defines this region
   * @param priority  a priority level. The higher the priority, the larger the precedence.
   * @return the updated host
   * @throws IllegalArgumentException if the inputs will lead to an invalid HostTree state,
   *                                  like if the name is not unique or the priority is the same
   *                                  as an overlapping region
   */
  @Nonnull
  Host updateRegion(String name, UUID worldUuid, Vector3i pos1, Vector3i pos2, int priority)
      throws IllegalArgumentException;

  /**
   * Remove a region from the given world. This method fails if it is called
   * with a name which is not in use.
   *
   * @param name the name of the region which to remove
   * @return the removed region
   * @throws IllegalArgumentException If this host cannot be removed or does not exist
   */
  @Nullable
  Host removeRegion(String name) throws IllegalArgumentException;

  /**
   * Check if a given world has a region called a given name.
   *
   * @param name the name of the region for which to check
   * @return true if a region exists, false if not
   */
  boolean hasRegion(String name);

  /**
   * Find the value corresponding to this setting key dependent on whether
   * this location is inside a host, such as a Region or a World.
   * This is the most important function of the HostTree.
   *
   * @param key      the setting, obtained from the SettingLibrary
   * @param location the location to query
   * @param <V>      the type of value to retrieve
   * @return the value corresponding to this setting
   * @see SettingLibrary
   */
  <V> SettingValue<V> lookup(SettingKey<V> key, Location<World> location);

}
