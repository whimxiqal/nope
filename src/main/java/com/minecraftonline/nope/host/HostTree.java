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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
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
   * @return the single global host associated in this tree
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
  Host getWorldHost(final UUID worldUuid);

  /**
   * Get the Region associated with this Region.
   *
   * @param name the name of the region
   * @return the associated region, or null if none exists
   */
  @Nullable
  VolumeHost getRegion(final String name);

  /**
   * Get a map of all hosts keyed by their unique names.
   *
   * @return a map of hosts
   */
  @Nonnull
  Map<String, Host> getHosts();

  /**
   * Get all the region within the world of the given id.
   *
   * @param worldUuid the UUID of the world
   * @return the associated Regions, or null if no world exists with that UUID
   */
  @Nullable
  Collection<VolumeHost> getRegions(final UUID worldUuid);

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
  VolumeHost addRegion(final String name,
                       final UUID worldUuid,
                       final Vector3i pos1,
                       final Vector3i pos2,
                       int priority)
          throws IllegalArgumentException;

  /**
   * Remove a region from the given world. This method fails if it is called
   * with a name which is not in use.
   *
   * @param name the name of the region which to remove
   * @return the removed region
   * @throws IllegalArgumentException If this host cannot be removed or does not exist
   */
  @Nonnull
  VolumeHost removeRegion(final String name) throws IllegalArgumentException;

  /**
   * Check if a given world has a region called a given name.
   *
   * @param name the name of the region for which to check
   * @return true if a region exists, false if not
   */
  boolean hasRegion(final String name);

  /**
   * Check if this SettingKey has been assigned to any hosts in the host tree.
   *
   * @param key the setting key
   * @return true if this key has been assigned to a host, false if not.
   */
  boolean isAssigned(final SettingKey<?> key);

  /**
   * Find the value corresponding to this setting key dependent on whether
   * this location is inside a host, such as a Region or a World,
   * and whether the subject is affected.
   * This is the most important function of the HostTree.
   *
   * @param key      the setting key, obtained from the SettingLibrary
   * @param subject  the subject to check for the setting
   * @param location the location in the world to check for the setting
   * @param <V>      the type of value to retrieve
   * @return the assigned value corresponding to this setting key
   * @see SettingLibrary
   */
  <V> V lookup(@Nonnull final SettingKey<V> key,
               @Nonnull final Subject subject,
               @Nonnull final Location<World> location);

  /**
   * Find the value corresponding to this setting key dependent on whether
   * this location is inside a host, such as a Region or a World.
   * This method ignores targets.
   *
   * @param key the setting key, obtained from the SettingLibrary
   * @param location the location in the world to check for the setting
   * @param <V> the type of value to retrieve
   * @return the assigned value corresponding to this setting key
   * @see SettingLibrary
   */
  <V> V lookupAnonymous(@Nonnull final SettingKey<V> key,
                        @Nonnull final Location<World> location);

}
