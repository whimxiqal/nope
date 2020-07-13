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
