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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.function.Function;

public interface Membership extends Function<Region, Membership.Status> {
  public enum Status {
    OWNER,
    MEMBER,
    NONE
  }

  static Membership block(Vector3i loc) {
    return region -> region.isLocationInRegion(loc) ? Status.MEMBER : Status.NONE;
  }

  static Membership player(org.spongepowered.api.entity.living.player.Player player) {
    return region -> {
      TargetSet owners = region.getSettingValue(Settings.REGION_OWNERS).orElse(null);
      if (owners != null && owners.isPlayerTargeted(player)) {
        return Status.OWNER;
      }
      TargetSet members = region.getSettingValue(Settings.REGION_MEMBERS).orElse(null);
      if (members != null && members.isPlayerTargeted(player)) {
        return Status.MEMBER;
      }
      return Status.NONE;
    };
  }

  static Membership constant(Status status) {
    return region -> status;
  }

  /**
   * Multiple locations is useful when you don't know for certain where
   * something came from, but any of the locations would produce the given result,
   * in which case, as long as 1 of the locations is inside the region, member status
   * is assumed
   * @param locations List of Locations
   * @return Membership
   */
  static Membership multipleLocations(List<Location<World>> locations) {
    return region -> {
      for (Location<World> loc : locations) {
        if (region.isLocationInRegion(loc.getBlockPosition())) {
          return Status.MEMBER;
        }
      }
      return Status.NONE;
    };
  }
}
