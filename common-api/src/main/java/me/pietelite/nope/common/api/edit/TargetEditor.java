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
   * This must be bypassed manually with {@link #forceAffect(boolean)}.
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
   * @param permission
   * @param value
   * @return
   */
  boolean addPermission(String permission, boolean value);

  boolean removePermission(String permission);

  boolean clearPermissions();

  Map<String, Boolean> permissions();

  boolean addPlayer(UUID player);

  boolean removePlayer(UUID player);

  boolean clearPlayers();

  Set<UUID> playerSet();

  boolean playerTargetType(Type type);

  Type playerTargetType();

  boolean remove();

  boolean forceAffect(boolean force);

  boolean forceAffect();

  enum Type {
    WHITELIST,
    BLACKLIST
  }

}
