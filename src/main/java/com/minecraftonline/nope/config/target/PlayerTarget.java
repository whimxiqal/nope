package com.minecraftonline.nope.config.target;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.UUID;

public class PlayerTarget implements Target {
  private UUID uuid;

  public PlayerTarget(String name) throws PlayerNotFoundException {
    this.uuid = Sponge.getServer().getPlayer(name).map(Player::getUniqueId)
        .orElseThrow(PlayerTarget.PlayerNotFoundException::new);
  }

  @Override
  public boolean isTargeted(Player player) {
    return player.getUniqueId().equals(this.uuid);
  }

  @Override
  public String serialize() {
    return uuid.toString();
  }

  static class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException() {
    }
  }
}
