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

package me.pietelite.nope.common.setting;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.TargetEditor;
import me.pietelite.nope.common.permission.Permissions;

/**
 * The arbiter for determining whether a setting affects a player.
 */
public final class Target {

  private final Set<UUID> users = new HashSet<>();
  private final Map<String, Boolean> permissions = new HashMap<>();
  private boolean indiscriminate = false;
  private boolean whitelist;

  private Target(boolean whitelist) {
    this.whitelist = whitelist;
  }

  /**
   * A static factory for a target that targets all players.
   *
   * @return the target
   */
  public static Target all() {
    return new Target(false);  // use blacklist, and no one is blacklisted
  }

  /**
   * A static factory for a target that targets no one.
   *
   * @return the target
   */
  public static Target none() {
    return new Target(true);  // use whitelist, and no one is whitelisted
  }

  /**
   * A static factory for a target that targets only those identified in the given collection.
   *
   * @param collection the collection
   * @return the target
   */
  public static Target intend(Collection<UUID> collection) {
    Target target = new Target(false);
    target.users.addAll(collection);
    return target;
  }

  /**
   * A static factory for a target that targets only those <i>not</i> identified in the given collection.
   *
   * @param collection the collection
   * @return the target
   */
  public static Target miss(Collection<UUID> collection) {
    Target target = new Target(true);
    target.users.removeAll(collection);
    return target;
  }

  public Set<UUID> users() {
    return users;
  }

  public Map<String, Boolean> permissions() {
    return permissions;
  }

  public boolean indiscriminate() {
    return indiscriminate;
  }

  public void indiscriminate(boolean indiscriminate) {
    this.indiscriminate = indiscriminate;
  }

  public boolean hasWhitelist() {
    return whitelist;
  }

  /**
   * Turn the targeted set of players into a whitelist.
   */
  public void targetAll() {
    this.users.clear();
    this.whitelist = false;
  }

  /**
   * Turn the targeted set of players into a blacklist.
   */
  public void targetNone() {
    this.users.clear();
    this.whitelist = true;
  }

  /**
   * Decides whether a user is targeted.
   *
   * @param userUuid          the user's uuid
   * @param playerRestrictive true if the setting key is restrictive in nature
   * @return true if the user is targeted by this setting value
   */
  public boolean test(UUID userUuid, boolean playerRestrictive) {
    if (playerRestrictive) {
      if (!indiscriminate && Nope.proxy().hasPermission(userUuid, Permissions.UNRESTRICTED)) {
        return false;
      }
    }
    // if whitelisted, then we only *NOT* target if NONE of the criteria are met.
    // in other words, we target if ANY of the criteria are met
    // if blacklisted, then we only target if NONE of the criteria are met.
    // in other words, we do *NOT* target if ANY of the criteria are met
    if (users.contains(userUuid)) {
      return whitelist;
    }
    for (Map.Entry<String, Boolean> permission : permissions.entrySet()) {
      if (Nope.proxy().hasPermission(userUuid, permission.getKey()) == permission.getValue()) {
        return whitelist;
      }
    }
    return !whitelist;
  }

  /**
   * The implementation of the {@link TargetEditor}.
   */
  public static class Editor implements TargetEditor {

    private final Targetable targetable;
    private final Runnable saveFunction;

    public Editor(Targetable targetable, Runnable saveFunction) {
      this.targetable = targetable;
      this.saveFunction = saveFunction;
    }

    @Override
    public void targetAll() {
      targetable.target(Target.all());
      saveFunction.run();
    }

    @Override
    public void targetNone() {
      targetable.target(Target.none());
      saveFunction.run();
    }

    @Override
    public boolean addPermission(String permission, boolean value) {
      Target target = targetable.target();
      if (target == null) {
        target = Target.all();
        targetable.target(target);
      }
      Boolean replaced = target.permissions().put(permission, value);
      saveFunction.run();
      return replaced == null || replaced != value;
    }

    @Override
    public boolean removePermission(String permission) {
      Target target = targetable.target();
      if (target == null) {
        return false;
      }
      if (target.permissions().remove(permission) == null) {
        return false;
      }
      saveFunction.run();
      return true;
    }

    @Override
    public boolean clearPermissions() {
      Target target = targetable.target();
      if (target == null) {
        return false;
      }
      boolean out = !target.permissions().isEmpty();
      target.permissions().clear();
      saveFunction.run();
      return out;
    }

    @Override
    public boolean addPlayer(UUID player) {
      Target target = targetable.target();
      if (target == null) {
        target = Target.none();
        target.users.add(player);
        targetable.target(target);
        saveFunction.run();
        return true;
      }
      if (!target.users().add(player)) {
        return false;
      }
      saveFunction.run();
      return true;
    }

    @Override
    public boolean removePlayer(UUID player) {
      Target target = targetable.target();
      if (target == null) {
        return false;
      }
      if (!target.users().remove(player)) {
        return false;
      }
      saveFunction.run();
      return true;
    }

    @Override
    public boolean clearPlayers() {
      Target target = targetable.target();
      if (target == null) {
        return false;
      }
      boolean out = !target.users.isEmpty();
      target.users.clear();
      saveFunction.run();
      return out;
    }

    @Override
    public boolean remove() {
      if (targetable.target() == null) {
        return false;
      }
      targetable.target(null);
      saveFunction.run();
      return true;
    }

    @Override
    public Set<UUID> playerSet() {
      Target target = targetable.target();
      if (target == null) {
        return Collections.emptySet();
      }
      return Collections.unmodifiableSet(target.users);
    }

    @Override
    public boolean targetType(Type type) {
      Target target = targetable.target();
      if (target == null) {
        switch (type) {
          case WHITELIST:
            targetable.target(Target.none());
            return true;
          case BLACKLIST:
            targetable.target(Target.all());
            return true;
          default:
            throw new RuntimeException();
        }
      }
      boolean setToWhitelist = type == Type.WHITELIST;
      if (target.whitelist == setToWhitelist) {
        return false;
      }
      target.whitelist = !target.whitelist;
      return true;
    }

    @Override
    public Type targetType() {
      Target target = targetable.target();
      if (target == null) {
        throw new NoSuchElementException();
      }
      return target.whitelist ? Type.WHITELIST : Type.BLACKLIST;
    }

    @Override
    public Map<String, Boolean> permissions() {
      Target target = targetable.target();
      if (target == null) {
        return Collections.emptyMap();
      }
      return Collections.unmodifiableMap(target.permissions);
    }

    @Override
    public boolean bypassUnrestricted(boolean bypass) {
      Target target = targetable.target();
      boolean out = false;
      if (target == null) {
        if (bypass) {
          target = Target.all();
          target.indiscriminate(true);
          targetable.target(target);
          out = true;
        }
      } else {
        out = target.indiscriminate != bypass;
        target.indiscriminate(bypass);
      }
      saveFunction.run();
      return out;
    }

    @Override
    public boolean bypassUnrestricted() {
      Target target = targetable.target();
      return target != null && target.indiscriminate;
    }
  }

}

