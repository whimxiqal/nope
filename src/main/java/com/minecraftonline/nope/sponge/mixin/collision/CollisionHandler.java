/*
 * MIT License
 *
 * Copyright (c) 2021 MinecraftOnline
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
 *
 */

package com.minecraftonline.nope.sponge.mixin.collision;

import com.minecraftonline.nope.sponge.util.CollisionUtil;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.spongepowered.api.entity.living.player.Player;

/**
 * A handler to disable and re-enable collision for players.
 */
public class CollisionHandler {
  private final Set<UUID> disabledCollision = new HashSet<>();

  /**
   * Disable collision for an in-game player.
   *
   * @param player the player
   */
  public void disableCollision(Player player) {
    if (disabledCollision.add(player.getUniqueId())) {
      CollisionUtil.disableCollision(player);
    }
  }

  /**
   * Enable collision for a player.
   *
   * @param player the player
   */
  public void enableCollision(Player player) {
    if (disabledCollision.remove(player.getUniqueId())) {
      CollisionUtil.enableCollision(player);
    }
  }

  public void logOut(Player player) {
    disabledCollision.remove(player.getUniqueId());
  }

  public boolean isCollisionDisabled(Player player) {
    return this.disabledCollision.contains(player.getUniqueId());
  }
}
