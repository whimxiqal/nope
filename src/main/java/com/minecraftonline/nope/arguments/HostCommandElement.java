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
import com.minecraftonline.nope.control.GlobalHost;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.WorldHost;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.host.HostTree;
import com.minecraftonline.nope.host.VolumeHost;
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

class HostCommandElement extends CommandElement {

  protected HostCommandElement(@Nullable Text key) {
    super(key);
  }

  @Nullable
  @Override
  protected Host parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
    String regionStr = args.next();

    final HostTree hostTree = Nope.getInstance().getHostTree();

    VolumeHost volumeHost = hostTree.getRegion(regionStr);

    if (volumeHost != null) {
      return volumeHost;
    }

    if (regionStr.equals(Nope.GLOBAL_HOST_NAME)) {
      return hostTree.getGlobalHost();
    }

    for (World world : Sponge.getServer().getWorlds()) {
      Host worldHost = hostTree.getWorldHost(world.getUniqueId());
      if (worldHost == null) {
        throw new IllegalStateException("Missing world host for world " + world.getName());
      }
      if (regionStr.equals(worldHost.getName())) {
        return worldHost;
      }
    }

    throw new ArgumentParseException(Text.of("Region '" + regionStr + "' does not exist!"), regionStr, regionStr.length());
  }

  @Override
  public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
    String beginning = args.nextIfPresent().orElse("");

    List<String> completions = new ArrayList<>();

    HostTree hostTree = Nope.getInstance().getHostTree();

    final String globalHostName = hostTree.getGlobalHost().getName();
    if (globalHostName.startsWith(beginning)) {
      completions.add(globalHostName);
    }

    for (World world : Sponge.getServer().getWorlds()) {

      Host worldHost = hostTree.getWorldHost(world.getUniqueId());

      if (worldHost == null) throw new RuntimeException("No worldhost for world: '" + world.getName() + "'");

      final String worldHostName = worldHost.getName();
      if (worldHostName.startsWith(beginning)) {
        completions.add(worldHostName);
      }

      hostTree.getRegions(world.getUniqueId()).stream()
          .map(VolumeHost::getName)
          .filter(name -> name.startsWith(beginning))
          .forEach(completions::add);
    }

    return completions;
  }
}
