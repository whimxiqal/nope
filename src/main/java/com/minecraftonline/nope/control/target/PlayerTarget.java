package com.minecraftonline.nope.control.target;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.UUID;

public class PlayerTarget implements Target {
  private UUID uuid;

  public PlayerTarget(String name) throws PlayerNotFoundException {
    this.uuid = Sponge.getServer().getPlayer(name).map(Player::getUniqueId)
        .orElseThrow(PlayerTarget.PlayerNotFoundException::new);
  }

<<<<<<< HEAD:src/main/java/com/minecraftonline/nope/config/target/PlayerTarget.java
  @Override
  public boolean isTargeted(Player player) {
    return player.getUniqueId().equals(this.uuid);
  }
=======
  public PlayerTarget(UUID uuid) {
    this.uuid = uuid;
  }

  @Override
  public boolean isTargeted(Player player) {
    return player.getUniqueId().equals(this.uuid);
  }
>>>>>>> 9e0f8e2... Reworked region config:src/main/java/com/minecraftonline/nope/control/target/PlayerTarget.java

  @Override
  public String serialize() {
    return uuid.toString();
  }

<<<<<<< HEAD:src/main/java/com/minecraftonline/nope/config/target/PlayerTarget.java
  static class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException() {
    }
  }
=======
  @Override
  public TargetType getTargetType() {
    return TargetType.PLAYER;
  }

  public static PlayerTarget deserialize(String s) {
    return new PlayerTarget(UUID.fromString(s));
  }

  static class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException() {
    }
  }
>>>>>>> 9e0f8e2... Reworked region config:src/main/java/com/minecraftonline/nope/control/target/PlayerTarget.java
}
