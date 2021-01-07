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

import com.google.gson.JsonElement;
import com.minecraftonline.nope.setting.*;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * A storage class for setting assignments.
 */
public abstract class Host {

  public static String nameToContextKey(String name) {
    return "nope.host." + name;
  }

  public static String contextKeyToName(String key) throws IllegalArgumentException {
    try {
      return key.split(".")[2];
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException("Cannot parse context key name");
    }
  }

  public static boolean isContextKey(String key) {
    String[] tokens = key.split(".");
    return tokens.length == 3 && tokens[0].equals("nope") && tokens[1].equals("host");
  }

  @Getter
  private final String name;
  private final SettingMap settings = new SettingMap();
  @Getter
  private final Context context;
  @Setter
  @Getter
  private Host parent;
  @Setter
  @Getter
  private int priority;

  /**
   * Default constructor.
   *
   * @param name     the name for this host; best practice is to have it
   *                 be unique
   * @param priority the priority level for this host for identifying
   *                 conflicting settings
   */
  public Host(String name, int priority) {
    this.name = name;
    this.priority = priority;
    this.context = new Context(nameToContextKey(name), "true");
  }

  public Host(String name) {
    this(name, 0);
  }

  /**
   * Assign a value to a setting for this Host.
   *
   * @param key   The setting
   * @param value The value to assign
   * @param <A>   The type of value this is
   * @return The value which was saved prior
   * @see SettingLibrary
   */
  @Nonnull
  @SuppressWarnings("unchecked")
  public <A> Optional<A> put(SettingKey<A> key, SettingValue<A> value) {
    return Optional.ofNullable((A) settings.put(Setting.of(key, value)));
  }

  /**
   * Assigns all the values in the map under the given settings.
   *
   * @param settings all settings
   * @see SettingLibrary
   */
  public void putAll(SettingMap settings) {
    this.settings.putAll(settings);
  }

  /**
   * Retrieves the value associated with a setting under this Host.
   *
   * @param key the setting which keys the value
   * @param <A> the type of value stored
   * @return the value
   * @see SettingLibrary
   */
  @Nonnull
  public <A> Optional<SettingValue<A>> get(SettingKey<A> key) {
    return Optional.ofNullable(this.settings.get(key));
  }

  /**
   * Retrieves all values associated with settings under this Host.
   * The types are hidden, so it is suggested you use {@link #get(SettingKey)}
   * wherever possible.
   *
   * @return a copy of all the setting assignments
   * @see SettingLibrary
   */
  @Nonnull
  public SettingMap getAll() {
    SettingMap settings = new SettingMap();
    settings.putAll(this.settings);
    return settings;
  }

  /**
   * Check if a setting is assigned for this Host.
   *
   * @param setting the setting to check for existence
   * @return true if exists
   * @see SettingLibrary
   */
  boolean has(SettingKey<?> setting) {
    return this.settings.containsKey(setting);
  }

  /**
   * Removes any value associated with the given
   * {@link SettingKey} from this host.
   *
   * @param key Key to remove the mapping for.
   * @return The no longer associated {@link SettingValue}, or null, if nothing was removed.
   */
  @Nullable
  public <A> SettingValue<A> remove(SettingKey<A> key) {
    return settings.remove(key);
  }

  /**
   * Clears all the {@link Setting} assignments.
   */
  void clear() {
    this.settings.clear();
  }

  /**
   * Check if a Sponge locatable exists within this host.
   * This is the same as calling this method with the
   * locatable's location with {@link #encompasses(Location)}
   *
   * @param spongeLocatable the locatable
   * @return true if within the host
   */
  public final boolean encompasses(Locatable spongeLocatable) {
    return encompasses(spongeLocatable.getLocation());
  }

  /**
   * Check if a Sponge Location exists within this host.
   *
   * @param spongeLocation the location
   * @return true if within the host
   */
  public boolean encompasses(Location<World> spongeLocation) {
    return true;
  }

  /**
   * Get the UUID of the Sponge Minecraft
   * world to which this Host is associated.
   *
   * @return the world's UUID, or null if the host is not associated with a world
   */
  @Nullable
  public abstract UUID getWorldUuid();

  public interface HostSerializer<T extends Host> {
    JsonElement serialize(T host);

    T deserialize(JsonElement json) throws IllegalArgumentException;
  }

}
