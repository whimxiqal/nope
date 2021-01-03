///*
// * MIT License
// *
// * Copyright (c) 2020 MinecraftOnline
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package com.minecraftonline.nope.listener.flag;
//
//import com.flowpowered.math.vector.Vector3d;
//import com.google.common.collect.Lists;
//import com.minecraftonline.nope.Nope;
//import com.minecraftonline.nope.control.flags.FlagState;
//import com.minecraftonline.nope.control.flags.FlagString;
//import com.minecraftonline.nope.control.flags.Membership;
//import org.apache.commons.lang3.tuple.Pair;
//import org.spongepowered.api.Sponge;
//import org.spongepowered.api.entity.living.player.Player;
//import org.spongepowered.api.event.CauseStackManager;
//import org.spongepowered.api.event.Listener;
//import org.spongepowered.api.event.cause.EventContextKeys;
//import org.spongepowered.api.event.entity.MoveEntityEvent;
//import org.spongepowered.api.text.Text;
//import org.spongepowered.api.text.serializer.TextSerializers;
//import org.spongepowered.api.util.AABB;
//import org.spongepowered.api.util.Direction;
//import org.spongepowered.api.world.Location;
//import org.spongepowered.api.world.World;
//
//import java.util.List;
//import java.util.Optional;
//
//public class PositionListener extends FlagListener {
//
//  @Listener
//  public void onMove(MoveEntityEvent e) {
//    if (e.getCause().first(Nope.class).isPresent()) {
//      return; // Caused by us.
//    }
//
//    Location<World> from = e.getFromTransform().getLocation();
//    Location<World> to = e.getToTransform().getLocation();
//    RegionSet regionSetFrom = Nope.getInstance().getGlobalHost().getRegions(from);
//    RegionSet regionSetTo = Nope.getInstance().getGlobalHost().getRegions(to);
//    Membership membership;
//
//    if (e.getTargetEntity() instanceof Player) {
//      membership = Membership.player((Player) e.getTargetEntity());
//    }
//    else {
//      membership = Membership.NONE;
//    }
//
//    // Collision
//
//    FlagState toCollision = regionSetTo.findFirstFlagSettingOrDefault(Settings.FLAG_PLAYER_COLLISION, membership);
//
//    boolean setCollisionValue = toCollision.getValue();
//
//    if (!(e.getTargetEntity() instanceof Player)) {
//      // If it is a player, wait until we know if the event is going to be cancelled first
//      /*if (e.getTargetEntity() instanceof LivingCollisionBridge) {
//        Nope.getInstance().getCollisionHandler().disableCollision(e.getTargetEntity());
//        ((LivingCollisionBridge)e.getTargetEntity()).setCanBePushed(setCollisionValue);
//      }*/
//      return;
//    }
//
//    // Movement
//
//    Player player = (Player) e.getTargetEntity();
//
//    boolean isTeleport = e instanceof MoveEntityEvent.Teleport;
//
//    boolean shouldCancel = false;
//
//    boolean canEnter = canChangeRegion(regionSetTo, regionSetFrom, membership, Settings.FLAG_ENTRY);
//    if (!canEnter) {
//      shouldCancel = true;
//    }
//
//    boolean canExit = canChangeRegion(regionSetFrom, regionSetTo, membership, isTeleport ? Settings.FLAG_EXIT_VIA_TELEPORT : Settings.FLAG_EXIT);
//    if (!canExit) {
//      shouldCancel = true;
//    }
//
//    //if (canEnter) {
//    //	// Stuck in the region... need to teleport them out.
//    //	teleportPlayerOutSafely(regionSetTo, membership, player); // TODO: check tp doesn't put them into the ground or similar.
//    //}
//
//    if (!canEnter && !canExit) {
//      shouldCancel = false;
//      Nope.getInstance().getLogger().warn("Player: " + player.getName() + " is stuck at: " + player.getLocation() + ". Who made a region that you can't be in or leave...");
//      Nope.getInstance().getLogger().warn("All region movement against this player will be disabled until they are unstuck.");
//    }
//
//    if (Nope.getInstance().canOverrideRegion(player)) {
//      shouldCancel = false; // Never cancel events against an overriding player.
//    }
//
//    e.setCancelled(shouldCancel);
//
//    // Messaging
//
//    Setting<FlagString> entryMessage = shouldCancel ? Settings.FLAG_ENTRY_DENY_MESSAGE : Settings.FLAG_GREETING;
//    Setting<FlagString> exitMessage = shouldCancel ? Settings.FLAG_EXIT_DENY_MESSAGE : Settings.FLAG_FAREWELL;
//
//    regionSetTo.findFirstFlagSettingWithRegion(entryMessage, membership)
//        .filter(pair -> !regionSetFrom.containsRegion(pair.getValue()))
//        .map(pair -> TextSerializers.FORMATTING_CODE.deserialize(pair.getKey().getValue()))
//        .ifPresent(player::sendMessage);
//
//    regionSetFrom.findFirstFlagSettingWithRegion(exitMessage, membership)
//        .filter(pair -> !regionSetTo.containsRegion(pair.getValue()))
//        .map(pair -> TextSerializers.FORMATTING_CODE.deserialize(pair.getKey().getValue()))
//        .ifPresent(player::sendMessage);
//
//    // Player Collision
//
//    if (!shouldCancel) {
//      // If not cancelling, set collision
//      //((LivingCollisionBridge)e.getTargetEntity()).setCanBePushed(setCollisionValue);
//      if (setCollisionValue) {
//        Nope.getInstance().getCollisionHandler().enableCollision(player);
//      }
//      else {
//        Nope.getInstance().getCollisionHandler().disableCollision(player);
//      }
//
//    }
//  }
//
//  public boolean canChangeRegion(RegionSet regionSet, RegionSet otherRegion, Membership membership, Setting<FlagState> allowed) {
//    Optional<Pair<FlagState, Region>> changeRegion = regionSet.findFirstFlagSettingWithRegion(allowed, membership);
//
//    boolean canChangeRegion = changeRegion.map(Pair::getKey).orElse(allowed.getDefaultValue()).getValue();
//
//    // We can change region,
//    // or we can't but we aren't changing regions
//    return canChangeRegion ||
//        !(changeRegion.isPresent() && otherRegion.containsRegion(changeRegion.get().getValue()));
//  }
//
//  public void teleportPlayerOutSafely(RegionSet regionSet, Membership membership, Player player) {
//    Pair<FlagState, Region> pair = regionSet.findFirstFlagSettingWithRegion(Settings.FLAG_ENTRY, membership).get();
//    if (pair.getValue() instanceof GlobalRegion) {
//      Nope.getInstance().getLogger().warn("What the hell are you doing, god no please no. Don't set GlobalRegion to be no entry, Nope nope nope");
//      Nope.getInstance().getLogger().warn("Removing that flag");
//      pair.getValue().unset(Settings.FLAG_ENTRY);
//      return;
//    }
//    RegularRegion region = (RegularRegion)pair.getValue();
//    Location<World> loc = player.getLocation();
//    Vector3d target = findShortestExit(loc.getPosition(), region.getAabb());
//    try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
//
//      frame.pushCause(Nope.getInstance());
//      frame.addContext(EventContextKeys.PLUGIN, Nope.getInstance().getPluginContainer());
//      player.setLocation(loc.setPosition(target));
//    }
//    player.sendMessage(Text.of("You are not allowed to be here!"));
//  }
//
//  private Vector3d findShortestExit(Vector3d loc, AABB aabb) {
//    Vector3d min = aabb.getMin();
//    Vector3d max = aabb.getMax();
//
//    List<Pair<Double, Vector3d>> list = Lists.newArrayList(
//        getDistance(loc, min, Direction.NORTH), // negative vector directions go to min
//        getDistance(loc, min, Direction.EAST),
//        getDistance(loc, min, Direction.DOWN),
//
//        getDistance(loc, max, Direction.SOUTH),
//        getDistance(loc, max, Direction.WEST),
//        getDistance(loc, max, Direction.UP) // positive vector directions go to max
//    );
//    // TODO: fix, doesn't seem to work.
//    double length = Double.MAX_VALUE;
//    Vector3d target = null;
//    for (Pair<Double, Vector3d> exitRoute : list) {
//      if (exitRoute.getKey() < length) {
//        target = exitRoute.getValue();
//      }
//    }
//    return target;
//  }
//
//  private Pair<Double, Vector3d> getDistance(Vector3d from, Vector3d to, Direction dir) {
//    Vector3d dirVector = dir.asOffset();
//    // By do this we take the dir out of from and the other two components
//    // of the 3d vector. This means we are finding the closest edge of the box,
//    // since we go straight to the edge in any particular Direction, dir.
//    Vector3d target = Vector3d.ONE.sub(dirVector).mul(to).add(dirVector.mul(from)); // Get other components of position
//    return Pair.of(from.distance(target), target.add(dirVector.mul(0.5))); // Add a little extra to make sure they escape
//  }
//}
