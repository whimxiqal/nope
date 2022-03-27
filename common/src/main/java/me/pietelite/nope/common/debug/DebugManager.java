/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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
 */

package me.pietelite.nope.common.debug;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.util.ManyToManyBiMap;
import org.jetbrains.annotations.Nullable;

/**
 * A manager for all internal state for in-game debugging purposes.
 */
public class DebugManager {

  private final Set<UUID> globalWatchers = ConcurrentHashMap.newKeySet();
  private final ManyToManyBiMap<UUID, String> watchedUsers = new ManyToManyBiMap<>();
  private final ManyToManyBiMap<UUID, SettingKey<?, ?, ?>> watchedKeys = new ManyToManyBiMap<>();
  private final ManyToManyBiMap<UUID, String> watchedTargets = new ManyToManyBiMap<>();

  /**
   * Make this user receive every debug message.
   *
   * @param watcher the one to receive messages
   */
  public void watchEverything(UUID watcher) {
    this.globalWatchers.add(watcher);
  }

  /**
   * Get the people watching all debug messages.
   *
   * @return the set of uuids of people watching all debug messages
   */
  public Set<UUID> globalWatchers() {
    return this.globalWatchers;
  }

  /**
   * Have a user watch debug messages involved with another player.
   *
   * @param watcher the uuid of the watcher
   * @param watched the name of the watched person
   */
  public void watchUser(UUID watcher, String watched) {
    this.watchedUsers.put(watcher, watched);
  }

  /**
   * Get all the watchers of a specific user.
   *
   * @param watched the name of the watched user
   * @return a set of uuids of the people watching that user
   */
  public Set<UUID> watchersOfUser(String watched) {
    return this.watchedUsers.getKeys(watched);
  }

  /**
   * Have a user watch debug messages involved with a {@link SettingKey}.
   *
   * @param watcher the uuid of the watcher
   * @param key     the setting key
   */
  public void watchKey(UUID watcher, SettingKey<?, ?, ?> key) {
    this.watchedKeys.put(watcher, key);
  }

  /**
   * Get all the watchers of a {@link SettingKey}.
   *
   * @param key the setting key
   * @return a set of uuids of the people watching that user
   */
  public Set<UUID> watchersOfKey(SettingKey<?, ?, ?> key) {
    return this.watchedKeys.getKeys(key);
  }

  /**
   * Have a user watch debug messages involved with a target.
   * A target generally means whatever the event's user was interacting with
   * at the time of the event.
   *
   * @param watcher the uuid of the watcher
   * @param target  the name of the target
   */
  public void watchTarget(UUID watcher, String target) {
    this.watchedTargets.put(watcher, target);
  }

  /**
   * Get all the watchers of a specific target.
   * A target generally means whatever the event's user was interacting with
   * at the time of the event.
   *
   * @param target the name of the watched user
   * @return a set of uuids of the people watching that user
   */
  public Set<UUID> watchersOfTarget(String target) {
    return this.watchedTargets.getKeys(target);
  }

  /**
   * Have a user stop watching all debug messages.
   *
   * @param watcher the uuid of the watcher
   */
  public void stopWatching(UUID watcher) {
    this.globalWatchers.remove(watcher);
    this.watchedUsers.removeKey(watcher);
    this.watchedKeys.removeKey(watcher);
    this.watchedTargets.removeKey(watcher);
  }

  /**
   * Watch a specific group of parameters so the watcher can get debug messages.
   *
   * @param watcher        the uuid of the user to receive debug messages
   * @param watchedUsers   names of users who trigger events to watch
   * @param watchedKeys    keys in events to watch
   * @param watchedTargets targets of events to watch, usually the thing the user was interacting with
   */
  public void watch(UUID watcher,
                    @Nullable Collection<? extends String> watchedUsers,
                    @Nullable Collection<? extends SettingKey<?, ?, ?>> watchedKeys,
                    @Nullable Collection<? extends String> watchedTargets) {
    stopWatching(watcher);
    if (watchedUsers != null && !watchedUsers.isEmpty()) {
      watchedUsers.forEach(u -> watchUser(watcher, u));
    }
    if (watchedKeys != null && !watchedKeys.isEmpty()) {
      watchedKeys.forEach(k -> watchKey(watcher, k));
    }
    if (watchedTargets != null && !watchedTargets.isEmpty()) {
      watchedTargets.forEach(t -> watchTarget(watcher, t));
    }
    if ((watchedUsers == null || watchedUsers.isEmpty())
        && (watchedKeys == null || watchedKeys.isEmpty())
        && (watchedTargets == null || watchedTargets.isEmpty())) {
      // if we haven't specified anything, then watch everything
      watchEverything(watcher);
    }
  }

  /**
   * Get a set of all watchers for any of the given parameters.
   *
   * @param user       the name of a user
   * @param settingKey a setting key
   * @param target     a target, usually the thing the user was interacting with
   * @return a set of all watcher uuids
   */
  public Set<UUID> watchersOf(String user, SettingKey<?, ?, ?> settingKey, String target) {
    Set<UUID> watchers = new HashSet<>();
    watchers.addAll(globalWatchers());
    watchers.addAll(watchersOfUser(user));
    watchers.addAll(watchersOfKey(settingKey));
    watchers.addAll(watchersOfTarget(target));
    return watchers;
  }

  /**
   * Check if someone is watching anything and therefore receives debug messages
   * under some conditions.
   *
   * @param watcher the uuid of the watcher
   * @return true if he/she is watching anything
   */
  public boolean isWatching(UUID watcher) {
    return globalWatchers.contains(watcher)
        || watchedUsers.containsKey(watcher)
        || watchedKeys.containsKey(watcher)
        || watchedTargets.containsKey(watcher);
  }
}
