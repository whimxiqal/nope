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

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.minecraftonline.nope.SettingLibrary;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A storage class for setting assignments.
 */
public abstract class Host {

  @Setter
  @Getter
  private Host parent;
  @Setter
  @Getter
  private int priority;
  @Getter
  private final String name;
  private final HashMap<String, Object> assignments = Maps.newHashMap();

  public Host(String name) {
    this.name = name;
  }

  /**
   * Assign a value to a setting for this Host
   *
   * @param setting The setting
   * @param value   The value to assign
   * @param <A>     The type of value this is
   * @return The value which was saved prior
   * @see SettingLibrary
   */
  @Nonnull
  @SuppressWarnings("unchecked")
  public <A> Optional<A> put(SettingLibrary.Setting<A> setting, A value) {
    return Optional.ofNullable((A) assignments.put(setting.getId(), value));
  }

  /**
   * Assigns all the values in the map under the given settings
   *
   * @param assignments all settings
   * @see SettingLibrary
   */
  public void putAll(Map<SettingLibrary.Setting<?>, Object> assignments) {
    assignments.forEach((key, value) -> this.assignments.put(key.getId(), value));
  }

  /**
   * Retrieves the value associated with a setting under this Host.
   *
   * @param setting the setting which keys the value
   * @param <A>     the type of value stored
   * @return the value
   * @see SettingLibrary
   */
  @Nonnull
  @SuppressWarnings("unchecked")
  public <A> Optional<A> get(SettingLibrary.Setting<A> setting) {
    return Optional.ofNullable((A) assignments.get(setting.getId()));
  }

  /**
   * Retrieves all values associated with settings under this Host.
   * The types are hidden, so it is suggested you use {@link #get(SettingLibrary.Setting)}
   * wherever possible.
   *
   * @return a copy of all the setting assignments
   * @see SettingLibrary
   */
  @Nonnull
  public Map<SettingLibrary.Setting<?>, Object> getAll() {
    Map<SettingLibrary.Setting<?>, Object> settings = Maps.newHashMap();
    assignments.forEach((key, value) -> settings.put(SettingLibrary.lookup(key), value));
    return settings;
  }

  /**
   * Check if a setting is assigned for this Host.
   *
   * @param setting the setting to check for existence
   * @return true if exists
   * @see SettingLibrary
   */
  boolean has(SettingLibrary.Setting<?> setting) {
    return assignments.containsKey(setting.getId());
  }

  /**
   * Clears all the {@link SettingLibrary.Setting} assignments.
   */
  void clear() {
    assignments.clear();
  }

  /**
   * Check if a Sponge locatable exists within this host.
   * This is the same as calling this method with the
   * locatable's location with {@link #encompasses(Location)}
   *
   * @param spongeLocatable the locatable
   * @return true if within the host
   */
  final boolean encompasses(Locatable spongeLocatable) {
    return encompasses(spongeLocatable.getLocation());
  }

  /**
   * Check if a Sponge Location exists within this host.
   *
   * @param spongeLocation the location
   * @return true if within the host
   */
  boolean encompasses(Location<World> spongeLocation) {
    return true;
  }

  public interface HostSerializer<T extends Host> {
    JsonElement serialize(T host);

    T deserialize(JsonElement json);
  }

}
