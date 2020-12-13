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

package com.minecraftonline.nope.arguments;

import com.google.common.collect.Lists;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.WorldHost;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class RegionCommandElement extends CommandElement {

  protected RegionCommandElement(@Nullable Text key) {
    super(key);
  }

  @Nullable
  @Override
  protected RegionWrapper parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
    String one = args.next();
    World world = null;
    if (source instanceof Player) {
      world = ((Player)source).getWorld();
    }
    String strRegion;
    if (one.equals("-w")) {
      String strWorld = args.next();
      world = Sponge.getServer().getWorld(strWorld)
          .orElseThrow(() -> new ArgumentParseException(Text.of("Invalid world name"), strWorld, 3 + strWorld.length()));
      strRegion = args.next();
    }
    else {
      strRegion = one;
    }
    if (world == null) {
      throw new ArgumentParseException(Text.of("Could not infer world, specify it with -w worldname"), strRegion, strRegion.length());
    }
    WorldHost worldHost = Nope.getInstance().getGlobalHost().getWorld(world);
    Region region = worldHost.getRegions().get(strRegion);
    if (region == null) {
      throw new ArgumentParseException(Text.of("Region '" + strRegion + "' in world '" + world.getName() + "' does not exist!"), strRegion, strRegion.length());
    }
    return new RegionWrapper(worldHost, region, strRegion);
  }

  @Override
  public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
    // TODO: fix for multiple arguments.
    //Nope.getInstance().getLogger().info("here1");
    // To get a region, we need a world and and a region string.
    World world = null;
    // Can we infer world from the player?
    if (src instanceof Player) {
      world = ((Player)src).getWorld();
    };
    //Nope.getInstance().getLogger().info("here2");
    if (!args.hasNext()) {
      if (world != null) {
        // we know the world, and we have no hints, return all regions for the players world
        return new ArrayList<>(Nope.getInstance().getGlobalHost().getWorld(world).getRegions().keySet());
      }
      // We don't know the world, the recommendation is to use the -w argument followed by a world
      return Sponge.getServer().getWorlds().stream()
          .map(World::getName)
          .map(name -> "-w " + name)
          .collect(Collectors.toList());
    }
    //Nope.getInstance().getLogger().info("here3");
    try {
      String regionArg = args.next();
      if (regionArg.equals("-w")) {
        //Nope.getInstance().getLogger().info("here4");
        // Its a world flag
        List<String> worlds = Sponge.getServer().getWorlds().stream()
            .map(World::getName)
            .collect(Collectors.toList());
        //Nope.getInstance().getLogger().info("here5");
        if (!args.hasNext()) {
          //Nope.getInstance().getLogger().info("returning");
          return Lists.newArrayList();
        }
        //Nope.getInstance().getLogger().info("passed check");
        String nextArg = args.next();
        //Nope.getInstance().getLogger().info("nextArg is '" + nextArg + "'");
        if (nextArg.isEmpty()) {
          //Nope.getInstance().getLogger().info("returning list of worlds " + worlds);
          return Lists.newArrayList("-waaaaaa", "-w b");
        }
        //Nope.getInstance().getLogger().info("checking for world..");
        world = Sponge.getServer().getWorld(nextArg).orElse(null);
        if (world == null) {
          //Nope.getInstance().getLogger().info("returning filtered possibilities");
          return ArgsUtil.filterPossibilities(nextArg, worlds);
        }
      }
      //Nope.getInstance().getLogger().info("past -w flag things");
      if (world != null) {
        // Parse regions
        List<String> completions = ArgsUtil.filterPossibilities(regionArg, Nope.getInstance().getGlobalHost().getWorld(world).getRegions().keySet());
        //Nope.getInstance().getLogger().info("complettions length: " + completions);
        return completions == null ? Lists.newArrayList() : completions;
      }
      return Lists.newArrayList();

    } catch (ArgumentParseException e) {
      Nope.getInstance().getLogger().error("Error while autocompleting Region argument", e);
      return Lists.newArrayList();
    }
  }
}
