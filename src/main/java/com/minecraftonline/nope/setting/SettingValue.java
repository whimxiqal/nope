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

package com.minecraftonline.nope.setting;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.permission.Permissions;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.api.entity.living.player.User;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * The value component of a {@link Setting}.
 * A SettingValue is designed to be matched with a {@link SettingKey}
 * and placed into a {@link SettingMap} inside a {@link Host}.
 *
 * @param <T> the type of data to store
 */
public class SettingValue<T> {

  /**
   * The important configurable value which determines the behavior
   * of the plugin for the corresponding {@link SettingKey}.
   * This field is called data but its called value in the configuration.
   */
  @Getter
  private final T data;

  /**
   * The targeted subject for the game behavior alteration made by
   * the {@link #data} value respective to the {@link SettingKey}.
   */
  @Getter
  private final Target target;

  private SettingValue(@Nonnull T data, @Nonnull Target target) {
    this.data = Objects.requireNonNull(data);
    this.target = Objects.requireNonNull(target);
  }

  /**
   * Static factory. The target is set to null.
   *
   * @param data the core data
   * @param <X>  the type of data stored
   * @return the setting value
   */
  public static <X> SettingValue<X> of(@Nonnull X data) {
    return new SettingValue<>(data, Target.all());
  }

  /**
   * Full static factory.
   *
   * @param data   the core data
   * @param target the intended target of the setting
   * @param <X>    the type of raw data stored
   * @return the setting
   */
  public static <X> SettingValue<X> of(@Nonnull X data, @Nonnull Target target) {
    return new SettingValue<>(data, target);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SettingValue<?> that = (SettingValue<?>) o;

    if (!data.equals(that.data)) return false;
    return target.equals(that.target);
  }

  @Override
  public int hashCode() {
    int result = data.hashCode();
    result = 31 * result + target.hashCode();
    return result;
  }

  /**
   * A class to manage the subjects to which an instance of
   * a {@link Setting} applies.
   */
  public static class Target extends HashMap<String, Boolean>
      implements BiPredicate<SettingKey<?>, User> {

    private Set<UUID> users = Sets.newHashSet();
    private boolean whitelist = true;
    @Getter @Setter
    private boolean forceAffect = false;

    private Target() {
    }

    public static JsonElement toJson(Target target) {
      Map<String, Object> map = Maps.newHashMap();
      if (!target.isEmpty()) {
        Map<String, Boolean> permissions = Maps.newHashMap();
        permissions.putAll(target);
        map.put("permissions", permissions);
      }
      if (!target.users.isEmpty()) {
        Set<String> users = Sets.newHashSet();
        target.users.stream().map(UUID::toString).forEach(users::add);
        if (target.whitelist) {
          map.put("whitelist", users);
        } else {
          map.put("blacklist", users);
        }
      }
      if (target.isForceAffect()) {
        map.put("force_affect", true);
      }
      return new Gson().toJsonTree(map);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static Target fromJson(JsonElement json) {
      if (json == null) {
        return new Target();
      }
      Target target = new Target();
      JsonObject map = json.getAsJsonObject();
      if (map.has("permissions")) {
        for (Map.Entry<String, JsonElement> entry : map.get("permissions").getAsJsonObject().entrySet()) {
          target.put(entry.getKey(), entry.getValue().getAsBoolean());
        }
      }
      boolean hasUsers = false;
      if (map.has("whitelist")) {
        target.whitelist = true;
        hasUsers = true;
      }
      if (map.has("blacklist")) {
        target.whitelist = false;
        hasUsers = true;
      }
      if (hasUsers) {
        for (JsonElement elem : map.get(target.whitelist ? "whitelist" : "blacklist").getAsJsonArray()) {
          target.users.add(UUID.fromString(elem.getAsString()));
        }
      }
      if (map.has("force_affect")) {
        target.setForceAffect(map.get("force_affect").getAsBoolean());
      }
      return target;
    }

    /**
     * Creates a new target that simply targets everyone.
     *
     * @return the new target
     */
    public static Target all() {
      return new Target();
    }

    /**
     * Creates a new target of whitelisted user ids.
     *
     * @param collection the user ids
     * @return the new target
     */
    public static Target whitelisted(Collection<UUID> collection) {
      Target target = new Target();
      target.users.addAll(collection);
      target.whitelist = true;
      return target;
    }

    /**
     * Creates a new target of blacklisted user ids.
     *
     * @param collection the user ids
     * @return the new target
     */
    public static Target blacklisted(Collection<UUID> collection) {
      Target target = new Target();
      target.users.addAll(collection);
      target.whitelist = false;
      return target;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      Target target = (Target) o;

      if (whitelist != target.whitelist) return false;
      if (forceAffect != target.forceAffect) return false;
      return users.equals(target.users);
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + users.hashCode();
      result = 31 * result + (whitelist ? 1 : 0);
      result = 31 * result + (forceAffect ? 1 : 0);
      return result;
    }

    /**
     * Add a user to the list of whitelisted
     * or blacklisted users.
     *
     * @param user the id of a user
     * @return whether this user has been added
     */
    public boolean addUser(UUID user) {
      return this.users.add(user);
    }

    /**
     * Checks if a user is currently targeted.
     *
     * @param user the user's id
     * @return whether this user is targeted
     */
    public boolean hasUser(UUID user) {
      return this.users.contains(user);
    }

    /**
     * Enable the whitelist on this Target.
     * If previously using blacklist, all users
     * are cleared.
     *
     * @return false if it was already using whitelist
     */
    public boolean setWhitelist() {
      if (whitelist) {
        return false;
      } else {
        this.users.clear();
        this.whitelist = true;
        return true;
      }
    }

    /**
     * Check if this Target is already using whitelist.
     *
     * @return true if whitelist is enabled
     */
    public boolean hasWhitelist() {
      return whitelist;
    }

    /**
     * Enable the blacklist on this Target.
     * If previously using whitelist, all users
     * are cleared.
     *
     * @return false if it was already using blacklist
     */
    public boolean setBlacklist() {
      if (whitelist) {
        this.users.clear();
        this.whitelist = false;
        return true;
      } else {
        return false;
      }
    }

    /**
     * Check if this Target is already using blacklist.
     *
     * @return true if blacklist is enabled
     */
    public boolean hasBlacklist() {
      return !whitelist;
    }

    /**
     * Decide whether this subject is targeted.
     *
     * @param user the permission subject
     * @return true if the subject is targeted
     */
    @Override
    public boolean test(SettingKey<?> key, User user) {
      if (key.isPlayerRestrictive()) {
        if (!forceAffect && user.hasPermission(Permissions.UNRESTRICTED.get())) {
          return false;
        }
      }
      if (!users.isEmpty()) {
        if (whitelist && !users.contains(user.getUniqueId())) {
          return false;
        }
        if (!whitelist && users.contains(user.getUniqueId())) {
          return false;
        }
      }
      return this.entrySet().stream().allMatch(entry ->
          user.hasPermission(entry.getKey()) == entry.getValue());
    }

    public Set<UUID> getUsers() {
      return Sets.newHashSet(this.users);
    }

    /**
     * Return a targeted user.
     *
     * @param uniqueId the user id
     * @return whether this id was removed
     */
    public boolean removePlayer(UUID uniqueId) {
      return this.users.remove(uniqueId);
    }
  }

}
