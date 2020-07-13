package com.minecraftonline.nope.config.target;

import org.spongepowered.api.entity.living.player.Player;

/**
 * Represents a target, this could be a specific player,
 * or a group (permission based)
 */
public interface Target {

  static Target fromString(String s) throws InvalidTargetException, PlayerTarget.PlayerNotFoundException {
    if (!s.contains(":")) {
      return new PlayerTarget(s);
    } else {
      String[] strings = s.split(":");
      if ("g".equals(strings[0])) {
        return new GroupTarget(strings[1]);
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
   * Serialise this Target into a string
   *
   * @return String serialised version
   */
  String serialize();

  class InvalidTargetException extends Exception {
    public InvalidTargetException() {
    }
  }
}
