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

package com.minecraftonline.nope.command;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.host.VolumeHost;
import com.minecraftonline.nope.key.zonewand.ZoneWandHandler;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.sponge.SpongeWorld;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class SelectCommand extends LambdaCommandNode {

  SelectCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_CREATE,
        Text.of("Create a zone with current selection and given name"),
        "select");
    addCommandElements(
        GenericArguments.flags()
            .valueFlag(NopeArguments.host(Text.of("zone")), "z", "-zone")
            .flag("w")
            .buildWith(GenericArguments.none()));
    setExecutor((src, args) -> {
      if (!(src instanceof Player)) {
        return CommandResult.empty();
      }
      Player player = (Player) src;

      Host host = args.<Host>getOne("zone").orElse(NopeCommandRoot.inferHost(src).orElse(null));
      if (host == null) {
        return CommandResult.empty();
      }
      if (!(host instanceof VolumeHost) || host.getWorldUuid() == null) {
        player.sendMessage(Format.error("The host " + host.getName() + " has no viable selection"));
        return CommandResult.empty();
      }
      VolumeHost volumeHost = (VolumeHost) host;

      if (args.hasFlag("w")) {
        Optional<PluginContainer> pluginContainer = Sponge.getPluginManager().getPlugin("worldedit");
        if (!pluginContainer.isPresent()) {
          player.sendMessage(Format.error("WorldEdit is not loaded."));
          return CommandResult.empty();
        }
        SpongeWorld spongeWorld = SpongeWorldEdit.inst().getWorld(player.getLocation().getExtent());
          SpongeWorldEdit.inst()
              .getSession(player)
              .setRegionSelector(spongeWorld, new CuboidRegionSelector(spongeWorld,
                  new Vector(volumeHost.getMinX(), volumeHost.getMinY(), volumeHost.getMinZ()),
                  new Vector(volumeHost.getMaxX(), volumeHost.getMaxY(), volumeHost.getMaxZ())));
        player.sendMessage(Format.success("Your WorldEdit selection was set to the corners of zone ",
            TextColors.GRAY, volumeHost.getName()));
        return CommandResult.success();
      } else {
        Nope.getInstance().getZoneWandHandler().getSelectionMap().put(player.getUniqueId(),
            new ZoneWandHandler.Selection(Sponge.getServer()
                .getWorld(host.getWorldUuid())
                .orElseThrow(() -> new RuntimeException("Could not find world")),
                new Vector3i(volumeHost.getMinX(), volumeHost.getMinY(), volumeHost.getMinZ()),
                new Vector3i(volumeHost.getMaxX(), volumeHost.getMaxY(), volumeHost.getMaxZ())));
        player.sendMessage(Format.success("Your Nope selection was set to the corners of zone ",
            TextColors.GRAY, volumeHost.getName()));
        return CommandResult.success();
      }
    });
  }

}
