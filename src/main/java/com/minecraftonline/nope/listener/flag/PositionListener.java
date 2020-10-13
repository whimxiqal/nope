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

package com.minecraftonline.nope.listener.flag;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.control.GlobalRegion;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.RegionSet;
import com.minecraftonline.nope.control.RegularRegion;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.flags.FlagBoolean;
import com.minecraftonline.nope.control.flags.FlagState;
import com.minecraftonline.nope.control.flags.FlagString;
import com.minecraftonline.nope.control.flags.Membership;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PositionListener extends FlagListener {

  @Listener
  public void onMove(MoveEntityEvent e) {
    if (e.getCause().first(Nope.class).isPresent()) {
      return; // Caused by us.
    }

    if (!(e.getTargetEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) e.getTargetEntity();
    if (Nope.getInstance().canOverrideRegion(player)) {
      return; // Force allow
    }
    Location<World> from = e.getFromTransform().getLocation();
    Location<World> to = e.getToTransform().getLocation();
    RegionSet regionSetFrom = Nope.getInstance().getGlobalHost().getRegions(from);
    RegionSet regionSetTo = Nope.getInstance().getGlobalHost().getRegions(to);

    Membership membership = Membership.player(player);
    boolean isTeleport = e instanceof MoveEntityEvent.Teleport;

    Tristate canEnter = canChangeRegion(regionSetTo, regionSetFrom, player, membership,
        Settings.FLAG_ENTRY, Settings.FLAG_ENTRY_DENY_MESSAGE, null, isTeleport, null);
    if (canEnter == Tristate.FALSE) {
      e.setCancelled(true);
    }

    Tristate canExit = canChangeRegion(regionSetFrom, regionSetTo, player, membership,
        Settings.FLAG_EXIT, Settings.FLAG_EXIT_DENY_MESSAGE, Settings.FLAG_EXIT_VIA_TELEPORT, isTeleport, Settings.FLAG_EXIT_OVERRIDE);
    if (canExit == Tristate.FALSE) {
      e.setCancelled(true);
    }

    if (canEnter == Tristate.UNDEFINED) {
      // Stuck in the region... need to teleport them out.
      teleportPlayerOutSafely(regionSetTo, membership, player); // TODO: check tp doesn't put them into the ground or similar.
    }

    if (canEnter == Tristate.FALSE
    && canExit == Tristate.FALSE) {
      e.setCancelled(false);
      Nope.getInstance().getLogger().warn("Player: " + player.getName() + " is stuck at: " + player.getLocation() + ". Who made a region that you can't be in or leave...");
      Nope.getInstance().getLogger().warn("All region movement against this player will be disabled until they are unstuck.");
    }
  }

  public Tristate canChangeRegion(RegionSet regionSet, RegionSet otherRegion, Player player, Membership membership, Setting<FlagState> allowed, Setting<FlagString> message, @Nullable Setting<FlagState> teleportOverride, boolean isTeleport, @Nullable Setting<FlagBoolean> override) {
    boolean canChangeRegion = regionSet.findFirstFlagSettingOrDefault(allowed, membership).getValue();
    if (!canChangeRegion) {
      if (!otherRegion.findFirstFlagSettingOrDefault(allowed, membership).getValue()) {
        return Tristate.UNDEFINED;
      }
      if ((teleportOverride != null && isTeleport && regionSet.findFirstFlagSettingOrDefault(teleportOverride, membership).getValue())
        || (override != null && regionSet.findFirstFlagSettingOrDefault(override, membership).getValue())) {
        return Tristate.TRUE; // Overrides.
      }
      // If we're not allowed to change region, and can't override that.
      Text text = TextSerializers.FORMATTING_CODE.deserialize(regionSet.findFirstFlagSettingOrDefault(message, membership).getValue());
      if (!text.isEmpty()) {
        player.sendMessage(text);
      }
      return Tristate.FALSE;
    }
    return Tristate.TRUE;
  }

  public void teleportPlayerOutSafely(RegionSet regionSet, Membership membership, Player player) {
    Pair<FlagState, Region> pair = regionSet.findFirstFlagSettingWithRegion(Settings.FLAG_ENTRY, membership).get();
    if (pair.getValue() instanceof GlobalRegion) {
      Nope.getInstance().getLogger().warn("What the hell are you doing, god no please no. Don't set GlobalRegion to be no entry, Nope nope nope");
      Nope.getInstance().getLogger().warn("Removing that flag");
      pair.getValue().unset(Settings.FLAG_ENTRY);
      return;
    }
    RegularRegion region = (RegularRegion)pair.getValue();
    Location<World> loc = player.getLocation();
    Vector3d target = findShortestExit(loc.getPosition(), region.getAabb());
    try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {

      frame.pushCause(Nope.getInstance());
      frame.addContext(EventContextKeys.PLUGIN, Nope.getInstance().getPluginContainer());
      player.setLocation(loc.setPosition(target));
    }
    player.sendMessage(Text.of("You are not allowed to be here!"));
  }

  private Vector3d findShortestExit(Vector3d loc, AABB aabb) {
    Vector3d min = aabb.getMin();
    Vector3d max = aabb.getMax();

    List<Pair<Double, Vector3d>> list = Lists.newArrayList(
        getDistance(loc, min, Direction.NORTH), // negative vector directions go to min
        getDistance(loc, min, Direction.EAST),
        getDistance(loc, min, Direction.DOWN),

        getDistance(loc, max, Direction.SOUTH),
        getDistance(loc, max, Direction.WEST),
        getDistance(loc, max, Direction.UP) // positive vector directions go to max
    );
    // TODO: fix, doesn't seem to work.
    double length = Double.MAX_VALUE;
    Vector3d target = null;
    for (Pair<Double, Vector3d> exitRoute : list) {
      if (exitRoute.getKey() < length) {
        target = exitRoute.getValue();
      }
    }
    return target;
  }

  private Pair<Double, Vector3d> getDistance(Vector3d from, Vector3d to, Direction dir) {
    Vector3d dirVector = dir.asOffset();
    // By do this we take the dir out of from and the other two components
    // of the 3d vector. This means we are finding the closest edge of the box,
    // since we go straight to the edge in any particular Direction, dir.
    Vector3d target = Vector3d.ONE.sub(dirVector).mul(to).add(dirVector.mul(from)); // Get other components of position
    return Pair.of(from.distance(target), target.add(dirVector.mul(0.5))); // Add a little extra to make sure they escape
  }
}
