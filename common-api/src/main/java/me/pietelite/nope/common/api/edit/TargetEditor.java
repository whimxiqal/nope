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

package me.pietelite.nope.common.api.edit;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * An editor for a target. A target is used to determine whether the value of a setting
 * is applied to any given individual.
 */
public interface TargetEditor {

  /**
   * Target all individuals, so a setting with this target will always be used during evaluation.
   * Unrestricted individuals remain unrestricted by restrictive settings, however.
   * This must be bypassed manually with {@link #bypassUnrestricted(boolean)}.
   */
  void targetAll();

  /**
   * Target no individuals, so setting with this target will never be used during evaluation.
   */
  void targetNone();

  /**
   * Add a permission requirement to this target.
   * If an individual is not whitelisted or blacklisted and there are permissions,
   * the individual must satisfy at least one of the permission requirements
   *
   * @param permission the permission to add
   * @param value      the value to match
   * @return true if it was added, false if not
   */
  boolean addPermission(String permission, boolean value);

  /**
   * Removes a permission requirement on this target.
   *
   * @param permission the permission to add
   * @return true if the permission was added
   */
  boolean removePermission(String permission);

  /**
   * Clear all permissions on this target.
   *
   * @return true if any permissions were removed
   */
  boolean clearPermissions();

  /**
   * Get all targeted permissions.
   *
   * @return the permissions
   */
  Map<String, Boolean> permissions();

  /**
   * Add a player.
   *
   * @param player the uuid of the player
   * @return true if the player was added
   */
  boolean addPlayer(UUID player);

  /**
   * Remove a player.
   *
   * @param player the uuid of the player
   * @return true if the player was removed
   */
  boolean removePlayer(UUID player);

  /**
   * Clear all players.
   *
   * @return true if any players were removed
   */
  boolean clearPlayers();

  /**
   * Get all players on this target, either whitelisted or blacklisted.
   *
   * @return all players
   */
  Set<UUID> playerSet();

  /**
   * Set the target type.
   *
   * @param type the type
   * @return true if it was set, false if no change was made
   */
  boolean targetType(Type type);

  Type targetType();

  /**
   * Remove this target from its container.
   *
   * @return true if it was removed
   */
  boolean remove();

  /**
   * Set whether this target should bypass the override.
   * This should be set to true if the intention is to target
   * administrator-type users, who usually have the "unrestricted"
   * override permission.
   *
   * @param bypass true to bypass override
   * @return true if the value was set
   */
  boolean bypassUnrestricted(boolean bypass);

  /**
   * Get whether this target bypasses the override.
   * This should be set to true if the intention is to target
   * administrator-type users, who usually have the "unrestricted"
   * override permission.
   *
   * @return true if this bypasses the override
   */
  boolean bypassUnrestricted();

  /**
   * The type of this target, which determines whether the added components should match
   * or shouldn't match the subject in order for the setting to apply.
   */
  enum Type {
    /**
     * Whitelist means that all components in this target are considered "targeted".
     * Any setting that uses this target will only apply if the subject matches any of the components.
     */
    WHITELIST,
    /**
     * Blacklist means that none of the components in this target are considered "targeted".
     * Any setting that uses this target ail not apply if the subject matches any of the components.
     */
    BLACKLIST
  }

}
