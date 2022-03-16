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
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.permission.Permissions;

/**
 * The arbiter for determining whether a setting affects a player.
 */
public final class Target {

  @Getter
  @Accessors(fluent = true)
  private final Set<UUID> users = new HashSet<>();

  @Getter
  @Accessors(fluent = true)
  private final Map<String, Boolean> permissions = new HashMap<>();

  @Getter
  @Setter
  private boolean indiscriminate = false;

  private boolean whitelist;

  private Target() {
  }

  public static Target all() {
    return Target.blacklisted(Collections.emptyList());
  }

  public static Target none() {
    return Target.whitelisted(Collections.emptyList());
  }

  public static Target whitelisted(Collection<UUID> collection) {
    Target target = new Target();
    target.users.addAll(collection);
    target.whitelist = true;
    return target;
  }

  public static Target blacklisted(Collection<UUID> collection) {
    Target target = new Target();
    target.users.addAll(collection);
    target.whitelist = false;
    return target;
  }

  public void whitelist() {
    if (!whitelist) {
      this.whitelist = true;
      this.users.clear();
    }
  }

  public void blacklist() {
    if (whitelist) {
      this.whitelist = false;
      this.users.clear();
    }
  }

  public boolean isWhitelist() {
    return whitelist;
  }

  public boolean isBlacklist() {
    return !whitelist;
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
      if (!indiscriminate && Nope.instance().hasPermission(userUuid, Permissions.UNRESTRICTED)) {
        return false;
      }
    }
    if (whitelist && !users.contains(userUuid)) {
      return false;
    }
    if (!whitelist && users.contains(userUuid)) {
      return false;
    }
    return this.permissions.entrySet().stream().allMatch(entry ->
        Nope.instance().hasPermission(userUuid, entry.getKey()) == entry.getValue());
  }

}

