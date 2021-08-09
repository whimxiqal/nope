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
import com.minecraftonline.nope.common.struct.Vector3i;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.general.CommandNode;
import com.minecraftonline.nope.sponge.command.general.arguments.NopeParameterKeys;
import com.minecraftonline.nope.sponge.command.general.arguments.NopeParameters;
import com.minecraftonline.nope.sponge.wand.Selection;
import java.util.Objects;
import java.util.function.Supplier;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.world.server.ServerWorld;

/**
 * A command to move the boundaries of a zone.
 */
public class MoveCommand extends CommandNode {

  MoveCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_EDIT,
        "Redefine the boundaries of a zone",
        "move");
    addParameter(NopeParameters.HOST);
    addParameter(NopeParameters.SELECTION);
  }


  private CommandResult reAddHost(VolumeHost host) {
    VolumeHost reAdded;
    Supplier<CommandResult> fail = () -> {
      SpongeNope.instance().logger().error(String.format("A request to move the host %s was sent. "
              + "The move failed and the original host could not be recovered.",
          host.getName()));
      return CommandResult.error(formatter().error("Severe: The host in transit could not be recovered"));
    };
    try {
      reAdded = SpongeNope.instance().getHostTreeAdapter().addZone(
          host.getName(),
          Objects.requireNonNull(host.getWorldKey()).formatted(),
          new Vector3i(host.getMinX(), host.getMinY(), host.getMinZ()),
          new Vector3i(host.getMaxX(), host.getMaxY(), host.getMaxZ()),
          host.getPriority());
      if (reAdded == null) {
        return fail.get();
      }
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return fail.get();
    }
    return CommandResult.error(formatter().error("Could not move zone"));
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    Host host = context.requireOne(NopeParameterKeys.HOST);
    Selection selection = context.requireOne(NopeParameterKeys.SELECTION);

    if (!(host instanceof VolumeHost)) {
      return CommandResult.error(formatter().error("You can only move volumetric zones!"));
    }

    Vector3i min = selection.minPosition();
    Vector3i max = selection.maxPosition();
    ServerWorld world = selection.world();

    // Remove the host that's moving
    VolumeHost removed;
    try {
      removed = SpongeNope.instance().getHostTreeAdapter().removeZone(host.getName());
    } catch (IllegalArgumentException e) {
      return CommandResult.error(formatter().error("Could not move zone: " + e.getMessage()));
    }

    // Add the new one
    VolumeHost created;
    try {
      created = SpongeNope.instance().getHostTreeAdapter().addZone(
          host.getName(),
          world.world().key().formatted(),
          min,
          max,
          host.getPriority()
      );
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return reAddHost(removed);
    }

    if (created == null) {
      return reAddHost(removed);
    }

    created.putAll(removed.getAll());

    SpongeNope.instance().saveState();
    context.cause()
        .audience()
        .sendMessage(formatter().success("Moved zone ___ to (___, ___, ___) <-> (___, ___, ___) in world ___",
            host.getName(),
            min.getX(), min.getY(), min.getZ(),
            max.getX(), max.getY(), max.getZ(),
            world.properties().displayName()
        ));

    return CommandResult.success();
  }
}
