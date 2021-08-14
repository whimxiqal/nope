package com.minecraftonline.nope.common.setting;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.permission.Permissions;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The arbiter for determining whether a setting affects a player.
 */
public final class Target {

  @Getter
  @Accessors(fluent = true)
  private final Set<UUID> users = Sets.newHashSet();

  @Getter
  @Accessors(fluent = true)
  private final Map<String, Boolean> permissions = Maps.newHashMap();

  @Getter
  @Setter
  private boolean indiscriminate = false;

  private boolean whitelist = true;

  private Target() {
  }

  public static Target all() {
    return new Target();
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
      this.users.clear();
      this.whitelist = false;
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
   * @param userUuid the user's uuid
   * @param playerRestrictive true if the setting key is restrictive in nature
   * @return true if the user is targeted by this setting value
   */
  public boolean test(UUID userUuid, boolean playerRestrictive) {
    if (playerRestrictive) {
      if (!indiscriminate && Nope.instance().hasPermission(userUuid, Permissions.UNRESTRICTED)) {
        return false;
      }
    }
    if (!users.isEmpty()) {
      if (whitelist && !users.contains(userUuid)) {
        return false;
      }
      if (!whitelist && users.contains(userUuid)) {
        return false;
      }
    }
    return this.permissions.entrySet().stream().allMatch(entry ->
        Nope.instance().hasPermission(userUuid, entry.getKey()) == entry.getValue());
  }

}

