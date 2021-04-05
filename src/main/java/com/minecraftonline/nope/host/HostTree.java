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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A generic interface for all Host interaction. Generally, in the
 * context of Nope, this means managing all data which has to
 */
public interface HostTree {

  /**
   * Load all data from some specified storage.
   *
   * @param location the location information to load from
   */
  void load(String location) throws Exception;

  /**
   * Save all data to some specified storage.
   *
   * @param location the location information to save to
   */
  void save(String location) throws Exception;

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
   * Get the Zone associated with this Zone.
   *
   * @param name the name of the zone
   * @return the associated zone, or null if none exists
   */
  @Nullable
  VolumeHost getZone(final String name);

  /**
   * Get a map of all hosts keyed by their unique names.
   *
   * @return a map of hosts
   */
  @Nonnull
  Map<String, Host> getHosts();

  /**
   * Get all the zone within the world of the given id.
   *
   * @param worldUuid the UUID of the world
   * @return the associated Zones
   * @throws IllegalArgumentException if no world exists with that UUID
   */
  @Nonnull
  @SuppressWarnings("unused")
  Collection<VolumeHost> getZones(final UUID worldUuid) throws IllegalArgumentException;

  /**
   * Add a zone to the HostTree with the given parameters.
   * The name must be unique. This method fails if it is called with a name
   * which is already in use.
   *
   * @param name      the unique name of this zone,
   *                  which cannot be formatted like a WorldHost name
   * @param worldUuid the uuid of the world in which this zone resides
   * @param pos1      a point which defines this zone
   * @param pos2      another point which defines this zone
   * @param priority  a priority level. The higher the priority, the larger the precedence.
   *                  Two intersecting zones may not have the same priority level.
   * @return the created zone, or null if it can't be created
   * @throws IllegalArgumentException if the inputs will lead to an invalid HostTree state,
   *                                  like if the name is not unique or the priority is the same
   *                                  as an overlapping zone
   */
  @Nullable
  VolumeHost addZone(final String name,
                     final UUID worldUuid,
                     final Vector3i pos1,
                     final Vector3i pos2,
                     int priority)
      throws IllegalArgumentException;

  /**
   * Remove a zone from the given world. This method fails if it is called
   * with a name which is not in use.
   *
   * @param name the name of the zone which to remove
   * @return the removed zone
   * @throws IllegalArgumentException If this host cannot be removed or does not exist
   */
  @Nonnull
  VolumeHost removeZone(final String name) throws IllegalArgumentException;

  /**
   * Check if a given world has a zone called a given name.
   *
   * @param name the name of the zone for which to check
   * @return true if a zone exists, false if not
   */
  boolean hasZone(final String name);

  /**
   * Gets all the hosts which contain this location.
   *
   * @param location the location which to check
   * @return a list of host containers
   */
  @Nonnull
  Collection<Host> getContainingHosts(@Nonnull Location<World> location);

  /**
   * Check if this SettingKey has been assigned to any hosts in the host tree.
   *
   * @param key the setting key
   * @return true if this key has been assigned to a host, false if not.
   */
  boolean isAssigned(final SettingKey<?> key);

  /**
   * Determines if a SettingKey is redundant. That is, see if a setting key
   * is set to the same value as a Host such that the host has a greater
   * priority and the Host completely encapsulates the original one.
   *
   * @param host the host which may have a redundant key
   * @param key  a setting key which may be redundant
   * @return the host which causes the key to be redundant on the input Host.
   * Returns null if it is not redundant and returns the original host
   * if it is redundant because of the plugin's default value
   */
  @Nullable
  Host isRedundant(Host host, SettingKey<?> key);

  /**
   * Find the value corresponding to this setting key dependent on whether
   * this location is inside a host, such as a Zone or a World,
   * and whether the subject is affected.
   * This is the most important function of the HostTree.
   *
   * @param key      the setting key, obtained from the SettingLibrary
   * @param user     the subject to check for the setting
   * @param location the location in the world to check for the setting
   * @param <V>      the type of value to retrieve
   * @return the assigned value corresponding to this setting key
   * @see SettingLibrary
   */
  <V> V lookup(@Nonnull final SettingKey<V> key,
               @Nullable final User user,
               @Nonnull final Location<World> location);

  /**
   * Find the value corresponding to this setting key dependent on whether
   * this location is inside a host, such as a Zone or a World.
   * This method ignores targets.
   *
   * @param key      the setting key, obtained from the SettingLibrary
   * @param location the location in the world to check for the setting
   * @param <V>      the type of value to retrieve
   * @return the assigned value corresponding to this setting key
   * @see SettingLibrary
   */
  <V> V lookupAnonymous(@Nonnull final SettingKey<V> key,
                        @Nonnull final Location<World> location);

  /**
   * Find the appropriate host corresponding to this setting key dependent on whether
   * this location is inside the host, such as a Zone or a World,
   * and whether the subject is affected.
   *
   * @param key      the setting key, obtained from the SettingLibrary
   * @param user     the subject to check for the setting
   * @param location the location in the world to check for the setting
   * @return the relevant host, or null if no host dictates this setting
   */
  @Nullable
  Host lookupDictator(@Nonnull final SettingKey<?> key,
                      @Nullable final User user,
                      @Nonnull final Location<World> location);

  /**
   * Find the host corresponding to this setting key dependent on whether
   * this location is inside a host, such as a Zone or a World.
   * This method ignores targets.
   *
   * @param key      the setting key, obtained from the SettingLibrary
   * @param location the location in the world to check for the setting
   * @return the relevant host, or null if no host dictates this setting
   * @see SettingLibrary
   */
  @Nullable
  Host lookupDictatorAnonymous(@Nonnull final SettingKey<?> key,
                               @Nullable final User user,
                               @Nonnull final Location<World> location);

}
