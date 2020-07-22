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
 */

package com.minecraftonline.nope.control.target;

import org.spongepowered.api.entity.living.player.Player;

import java.util.function.Function;

/**
 * Represents a target, this could be a specific player,
 * or a group (permission based)
 */
public interface Target {

  enum TargetType {
    PLAYER("unique-ids", PlayerTarget::deserialize),
    GROUP("groups", GroupTarget::deserialize);

    private String key;
    private Function<String, Target> function;
    TargetType(String key, Function<String, Target> function) {
      this.key = key;
      this.function = function;
    }

    public String getKey() {
      return key;
    }

    public Target deserialize(String string) {
      return function.apply(string);
    }
  }

  static Target fromStrings(String s) throws InvalidTargetException, PlayerTarget.PlayerNotFoundException {
    if (!s.contains(":")) {
      return new PlayerTarget(s);
    } else {
      String[] split = s.split(":");
      if ("g".equals(split[0])) {
        return new GroupTarget(split[1]);
      }
      throw new InvalidTargetException();
    }
  }

  /**
   * Checks if a player is refered to by this
   *
   * @param player Player to check
   * @return Whether the player should be targeted by this
   */
  boolean isTargeted(Player player);

  /**
   * Serializes the Target into a String
   * @return String serialized Target
   */
  String serialize();

  TargetType getTargetType();

  class InvalidTargetException extends Exception {
    public InvalidTargetException() {
    }
  }
}
