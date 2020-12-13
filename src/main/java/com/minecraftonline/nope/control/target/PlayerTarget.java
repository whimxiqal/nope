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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.UUID;

public class PlayerTarget implements Target {
  private UUID uuid;

  public PlayerTarget(String name) throws PlayerNotFoundException {
    this.uuid = Sponge.getServer().getPlayer(name).map(Player::getUniqueId)
        .orElseThrow(PlayerTarget.PlayerNotFoundException::new);
  }

  public PlayerTarget(UUID uuid) {
    this.uuid = uuid;
  }

  @Override
  public boolean isTargeted(Player player) {
    return player.getUniqueId().equals(this.uuid);
  }

  @Override
  public String serialize() {
    return uuid.toString();
  }

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
}
