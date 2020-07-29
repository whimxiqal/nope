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

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.RegionSet;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.flags.Flag;
import com.minecraftonline.nope.control.flags.FlagState;
import com.minecraftonline.nope.control.flags.FlagUtil;
import com.minecraftonline.nope.control.flags.Membership;
import com.minecraftonline.nope.control.target.TargetSet;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Piston;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BlockListener extends FlagListener {
  @Exclude({ChangeBlockEvent.Post.class})
  @Listener
  public void onBlockChange(ChangeBlockEvent e) {
    if (e.getContext().get(EventContextKeys.PLUGIN).isPresent()) {
      return; // Caused by a plugin, don't block.
    }
    Object root = e.getCause().root();
    //if (cancelledSources.contains(root)) {
    //	return; // stop infinite loop
    //}
    Set<Region> checkedRegions = new HashSet<>();

    for (Transaction<BlockSnapshot> transaction : e.getTransactions()) {
      // Setup information so we can make an properly informed decision when deciding whether
      // to cancel an action.
      Location<World> loc = transaction.getOriginal().getLocation().get();
      RegionSet regionSet = Nope.getInstance().getGlobalHost().getRegions(loc);
      Region region = regionSet.getHighestPriorityRegion().orElse(null);
      if (region == null) {
        Nope.getInstance().getLogger().info("region was null");
        continue; // Wouldn't really ever expect this, should always be a __global__ region.
      }
      if (checkedRegions.contains(region)) {
        Nope.getInstance().getLogger().info("skipping, already checked");
        continue;
      }
      checkedRegions.add(region);
      Membership membership;
      if (root instanceof Player) {
        membership = Membership.player((Player)root);
      }
      else if (root instanceof Piston) {
        /*Piston piston = ((Piston)root);
        membership = Membership.block(piston.getLocation().getBlockPosition());
        e.setCancelled(true);*/ // Crashes server, sponge bug.
        return;
      }
      else {
        return;
      }
      Setting<FlagState> specialSetting;
      // Setting to check for this particular break, i.e if its a place, the place setting,
      // if its the break, break setting, or null if no specialization
      if (e instanceof ChangeBlockEvent.Modify) {
        Nope.getInstance().getLogger().info("modify event");
        specialSetting = Settings.FLAG_INTERACT;
      }
      else if (e instanceof ChangeBlockEvent.Break) {
        Nope.getInstance().getLogger().info("break event");
        specialSetting = Settings.FLAG_BLOCK_BREAK;
      }
      else if (e instanceof ChangeBlockEvent.Place) {
        Nope.getInstance().getLogger().info("place event");
        specialSetting = Settings.FLAG_BLOCK_PLACE;
      }
      else {
        specialSetting = null;
      }
      // Check actual settings
      boolean shouldCancel = checkBuildFlags(regionSet, membership, specialSetting);
      if (shouldCancel) {
        e.setCancelled(true);
        //cancelledSources.add(root);
        Nope.getInstance().getLogger().info(e.getContext().toString());
        if (root instanceof CommandSource) {
          Text msg = Text.of("You can't do this here!");//TextSerializers.FORMATTING_CODE.deserialize(region.getSettingValueOrDefault(Settings.FLAG_DENY_MESSAGE).getValue());
          if (!msg.isEmpty()) {
            ((CommandSource)root).sendMessage(msg);
          }
        }
        return;
      }
    }
  }

  // Is this throwing errors about immutability, then maybe sponge updated so that
  // their code matches their documentation. If you're lucky, .filterDirections() will
  // now exist.
  //@Listener(order = Order.EARLY) // Stop redstone propagation
  public void onNotifyNeighborBlockEvent(NotifyNeighborBlockEvent e) {
    Player player = e.getCause().first(Player.class).orElse(null);
    //if (player != null && cancelledSources.contains(player)) {
    //	e.setCancelled(true);
    //	return;
    //}
    // Disallow notifications across region borders unless they are allowed.
    LocatableBlock locatableBlock = (LocatableBlock) e.getSource();
    Location<World> source = locatableBlock.getLocation();
    Iterator<Direction> iter = e.getNeighbors().keySet().iterator();
    while (iter.hasNext()) {
      Location<World> target = source.add(iter.next().asBlockOffset());
      RegionSet regionSet = Nope.getInstance().getGlobalHost().getRegions(target);
      Region region = regionSet.getHighestPriorityRegion().orElse(null);
      if (region == null) {
        continue;
      }

      if (checkBuildFlags(regionSet, Membership.block(source.getBlockPosition()), Settings.FLAG_INTERACT)) { // Interaction is best fit i think.
        iter.remove();
      }
    }
  }

  //@Listener // Piston handling.
  public void onChangeBlockEventPre(ChangeBlockEvent.Pre e) {
    Nope.getInstance().getLogger().warn("in changeblockeventpre");
    if (!(e.getSource() instanceof LocatableBlock)) {
      Nope.getInstance().getLogger().info("unexpected type in changeblockevent.pre: " + e.getSource());
      return;
    }
    LocatableBlock locatableBlock = (LocatableBlock) e.getSource();
    if (locatableBlock.getBlockState().getType() != BlockTypes.STICKY_PISTON
    && e.getContext().get(EventContextKeys.PISTON_RETRACT).isPresent()) {
      // We're retracting and not sticky, no problems
      return;
    }
    Membership membership = Membership.block(locatableBlock.getPosition());

    for (Location<World> loc : e.getLocations()) {
      RegionSet regionSet = Nope.getInstance().getGlobalHost().getRegions(loc);
      if (checkBuildFlags(regionSet, membership, Settings.FLAG_BLOCK_BREAK)
          || checkBuildFlags(regionSet, membership, Settings.FLAG_BLOCK_PLACE)) {
        // Could be optimised a fair amount, i.e caching regions and checking if already passed.
        e.setCancelled(true);
      }
    }
  }

  /**
   * Finds whether you should cancel an event
   * @param regionSet
   * @param membership Membership of source to check
   * @param specializedSetting
   * @return
   */
  private static boolean checkBuildFlags(RegionSet regionSet, Membership membership, @Nullable Setting<FlagState> specializedSetting) {
    boolean passthrough = regionSet.findFirstFlagSettingOrDefault(Settings.FLAG_PASSTHROUGH, membership).getValue();
    Optional<FlagState> buildFlag = getFlagNoDefault(Settings.FLAG_BUILD, regionSet, passthrough, membership);
    Optional<FlagState> specialFlag = specializedSetting == null ? Optional.empty() : getFlagNoDefault(specializedSetting, regionSet, passthrough, membership);

    boolean shouldAllow;
    if (specializedSetting != null) {
      // If theres a specialized setting, use their value, buildflag value or its default
      if (specialFlag.isPresent()) {
        shouldAllow = specialFlag.get().getValue();
      }
      else if (buildFlag.isPresent()) {
        shouldAllow = buildFlag.get().getValue();
      }
      else {
        shouldAllow = specializedSetting.getDefaultValue().getValue();
      }
      //Nope.getInstance().getLogger().info("here1");
    }
    else if (buildFlag.isPresent()) {
      shouldAllow = buildFlag.get().getValue();
      //Nope.getInstance().getLogger().info("here2, buildflag.getValue(): " + buildFlag.get().getValue());
    }
    else {
      shouldAllow = Settings.FLAG_BUILD.getDefaultValue().getValue();
    }
    //Nope.getInstance().getLogger().info("shouldAllow: " + shouldAllow);
    return !shouldAllow;
  }

  /**
   * Gets a flag from a region, or from lower priority regions if passthrough is set,
   * or its default if there is still no flag.
   * @param setting Setting to check
   * @param regionSet RegionSet to check if passthrough is true and nothing is found in region
   * @param passthrough whether to check regionSet for flags
   * @param membership the membership supplier
   * @param <T> Type of setting
   * @return Value or default value of setting
   */
  private static <T extends Flag<?>> T getFlag(Setting<T> setting, RegionSet regionSet, boolean passthrough, Membership membership) {
    return getFlagNoDefault(setting, regionSet, passthrough, membership).orElse(setting.getDefaultValue());
  }

  /**
   * Gets a flag from a region, or from lower priority regions if passthrough is set
   * @param setting Setting to check
   * @param regionSet RegionSet to check if passthrough is true and nothing is found in region
   * @param passthrough whether to check regionSet for flags
   * @param membership the membership supplier
   * @param <T> Type of setting
   * @return Value from region or regionset (if passthrough is set)
   */
  private static <T extends Flag<?>> Optional<T> getFlagNoDefault(Setting<T> setting, RegionSet regionSet, boolean passthrough, Membership membership) {
    if (passthrough) {
      return regionSet.findFirstFlagSetting(setting, membership);
    }
    else {
      return regionSet.getHighestPrioritySettingValue(setting)
          .filter(entry -> FlagUtil.appliesTo(entry.getKey(), entry.getValue(), membership))
          .map(Map.Entry::getKey);
    }
  }

  /**
   * Gets any flag from a region or from regionSet if none in region
   * and passthrough is true
   * @param setting Setting to get flag from
   * @param region Main region
   * @param regionSet other overlapping regions
   * @param passthrough whether to grab flags from lower priority regions if not present
   * @param <T> Type of Flag
   * @return Optional of the Flag if it is set.
   */
  private static <T extends Flag<?>> Optional<T> getAnyFlagNoDefault(Setting<T> setting, Region region, RegionSet regionSet, boolean passthrough) {
    Optional<T> optFlag = region.getSettingValue(setting);
    if (!optFlag.isPresent()) {
      // passthrough means, whether if this region doesn't have the build flag set,
      // look up further in the hierachy for one that does, or get the default.
      if (passthrough) {
        return regionSet.getHighestPrioritySettingValue(setting).map(Map.Entry::getKey);
      }
      return optFlag;
    }
    return optFlag;
  }
}
