/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

package me.pietelite.nope.common.debug;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.util.ManyToManyBiMap;
import org.jetbrains.annotations.Nullable;

public class DebugManager {

  private final Set<UUID> globalWatchers = ConcurrentHashMap.newKeySet();
  private final ManyToManyBiMap<UUID, String> watchedUsers = new ManyToManyBiMap<>();
  private final ManyToManyBiMap<UUID, SettingKey<?, ?, ?>> watchedKeys = new ManyToManyBiMap<>();
  private final ManyToManyBiMap<UUID, String> watchedTargets = new ManyToManyBiMap<>();

  public void watchEverything(UUID watcher) {
    this.globalWatchers.add(watcher);
  }

  public Set<UUID> globalWatchers() {
    return this.globalWatchers;
  }

  public void watchUser(UUID watcher, String watched) {
    this.watchedUsers.put(watcher, watched);
  }

  public Set<UUID> watchersOfUser(String watched) {
    return this.watchedUsers.getKeys(watched);
  }

  public void watchKey(UUID watcher, SettingKey<?, ?, ?> key) {
    this.watchedKeys.put(watcher, key);
  }

  public Set<UUID> watchersOfKey(SettingKey<?, ?, ?> key) {
    return this.watchedKeys.getKeys(key);
  }

  public void watchTarget(UUID watcher, String target) {
    this.watchedTargets.put(watcher, target);
  }

  public Set<UUID> watchersOfTarget(String target) {
    return this.watchedTargets.getKeys(target);
  }

  public void stopWatching(UUID watcher) {
    this.watchedUsers.removeKey(watcher);
    this.watchedKeys.removeKey(watcher);
    this.watchedTargets.removeKey(watcher);
  }

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

  public Set<UUID> watchersOf(String user, SettingKey<?, ?, ?> settingKey, String target) {
    Set<UUID> watchers = new HashSet<>();
    watchers.addAll(globalWatchers());
    watchers.addAll(watchersOfUser(user));
    watchers.addAll(watchersOfKey(settingKey));
    watchers.addAll(watchersOfTarget(target));
    return watchers;
  }

  public boolean isWatching(UUID watcher) {
    return globalWatchers.contains(watcher)
        || watchedUsers.containsKey(watcher)
        || watchedKeys.containsKey(watcher)
        || watchedTargets.containsKey(watcher);
  }
}
