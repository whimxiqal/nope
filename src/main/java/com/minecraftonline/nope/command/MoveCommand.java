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
import com.minecraftonline.nope.key.regionwand.RegionWandHandler;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.host.VolumeHost;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

public class MoveCommand extends LambdaCommandNode {

  MoveCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_REGION_EDIT,
        Text.of("Redefine the boundaries of a region"),
        "move");

    addCommandElements(
        NopeArguments.host(Text.of("host")),
        NopeArguments.regionLocation(Text.of("selection"))
    );
    setExecutor((src, args) -> {
      Host host = args.requireOne("host");
      RegionWandHandler.Selection selection = args.requireOne("selection");

      if (!(host instanceof VolumeHost)) {
        src.sendMessage(Format.error("You can only move volumetric regions!"));
        return CommandResult.empty();
      }

      Vector3i min = selection.getMin();
      Vector3i max = selection.getMax();

      World world = selection.getWorld();

      if (world == null) {
        throw new IllegalStateException("World was null where it never should be!");
      }

      try {
        VolumeHost removed = Nope.getInstance().getHostTree().removeRegion(host.getName());
        VolumeHost created = Nope.getInstance().getHostTree().addRegion(
            host.getName(),
            world.getUniqueId(),
            min,
            max,
            host.getPriority()
        );
        created.putAll(removed.getAll());

      } catch (IllegalArgumentException e) {
        src.sendMessage(Format.error("Could not move region: " + e.getMessage()));
        return CommandResult.empty();
      }

      Nope.getInstance().saveState();
      src.sendMessage(Format.success(String.format(
          "Moved region %s to (%d, %d, %d) <-> (%d, %d, %d) in world %s",
          host.getName(),
          min.getX(), min.getY(), min.getZ(),
          max.getX(), max.getY(), max.getZ(),
          world.getName()
      )));

      return CommandResult.success();
    });
  }
}
