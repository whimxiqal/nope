package com.minecraftonline.nope;

import com.minecraftonline.nope.util.CollisionUtil;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * A handler to disable and re-enable collision for players.
 */
public class CollisionHandler {
  private final Set<Player> disabledCollision = new HashSet<>();

  public void disableCollision(Player player) {
    if (disabledCollision.add(player)) {
      CollisionUtil.disableCollision(player);
    }
  }

  public void enableCollision(Player player) {
    if (disabledCollision.remove(player)) {
      CollisionUtil.enableCollision(player);
    }
  }

  public boolean isCollisionDisabled(Player player) {
    return this.disabledCollision.contains(player);
  }
}
