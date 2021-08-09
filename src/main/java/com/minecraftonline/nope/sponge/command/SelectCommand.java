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

package com.minecraftonline.nope.sponge.command;

import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.host.VolumeHost;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.general.CommandNode;
import com.minecraftonline.nope.sponge.command.general.PlayerOnlyCommandNode;
import com.minecraftonline.nope.sponge.command.general.arguments.NopeFlags;
import com.minecraftonline.nope.sponge.command.general.arguments.NopeParameterKeys;
import com.minecraftonline.nope.sponge.wand.Selection;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.sponge.SpongeWorld;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.plugin.PluginContainer;

/**
 * A command to allows the player to put their Nope selection
 * around the given zone.
 */
public class SelectCommand extends PlayerOnlyCommandNode {

  SelectCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_CREATE,
        "Create a zone with current selection and given name",
        "select");
    addFlag(NopeFlags.HOST_INFER_FLAG);
    addFlag(Flag.of("w"));
  }

  @Override
  public CommandResult execute(CommandContext context, Player player) throws CommandException {
    Host host = context.requireOne(NopeParameterKeys.HOST);

    if (!(host instanceof VolumeHost) || host.getWorldKey() == null) {
      return CommandResult.error(formatter().error("The host ___ has no viable selection", host.getName()));
    }
    VolumeHost volumeHost = (VolumeHost) host;

    if (context.hasFlag("w")) {
      Optional<PluginContainer> pluginContainer = Sponge.pluginManager().plugin("worldedit");
      if (!pluginContainer.isPresent()) {
        return CommandResult.error(formatter().error("WorldEdit is not loaded"));
      }
      SpongeWorld spongeWorld = SpongeWorldEdit.inst().getWorld(player.location().world());
      SpongeWorldEdit.inst()
          .getSession(player)
          .setRegionSelector(spongeWorld, new CuboidRegionSelector(spongeWorld,
              new Vector(volumeHost.getMinX(), volumeHost.getMinY(), volumeHost.getMinZ()),
              new Vector(volumeHost.getMaxX(), volumeHost.getMaxY(), volumeHost.getMaxZ())));
      player.sendMessage(formatter()
          .success("Your WorldEdit selection was set to the corners of zone ___",
              volumeHost.getName()));
    } else {
      Selection.Draft draft = SpongeNope.instance().getSelectionHandler().draft(player.uniqueId());
      draft.setPosition1(new Selection.Position(player.serverLocation().worldKey(),
          volumeHost.getMinX(),
          volumeHost.getMinY(),
          volumeHost.getMinZ()));
      draft.setPosition2(new Selection.Position(player.serverLocation().worldKey(),
          volumeHost.getMaxX(),
          volumeHost.getMaxY(),
          volumeHost.getMaxZ()));
      player.sendMessage(formatter().success(
          "Your Nope selection was set to the corners of zone ___", volumeHost.getName()));
    }
    return CommandResult.success();
  }
}
