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

package com.minecraftonline.nope.control.flags;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.target.TargetSet;
import org.spongepowered.api.entity.living.player.Player;

import java.util.function.Function;

public abstract class Membership implements Function<Region, Membership.Status> {
  public enum Status {
    OWNER,
    MEMBER,
    NONE
  }

  public static Membership block(Vector3i loc) {
    return new Block(loc);
  }

  public static Membership player(org.spongepowered.api.entity.living.player.Player player) {
    return new Player(player);
  }

  private static class Block extends Membership {
    private Vector3i loc;

    public Block(Vector3i loc) {
      this.loc = loc;
    }

    @Override
    public Status apply(Region region) {
      return region.isLocationInRegion(loc) ? Status.MEMBER : Status.NONE;
    }
  }

  private static class Player extends Membership {
    private org.spongepowered.api.entity.living.player.Player player;

    public Player(org.spongepowered.api.entity.living.player.Player player) {
      this.player = player;
    }

    @Override
    public Status apply(Region region) {
      TargetSet owners = region.getSettingValue(Settings.REGION_OWNERS).orElse(null);
      if (owners != null && owners.isPlayerTargeted(player)) {
        return Status.OWNER;
      }
      TargetSet members = region.getSettingValue(Settings.REGION_MEMBERS).orElse(null);
      if (members != null && members.isPlayerTargeted(player)) {
        return Status.MEMBER;
      }
      return Status.NONE;
    }
  }
}
