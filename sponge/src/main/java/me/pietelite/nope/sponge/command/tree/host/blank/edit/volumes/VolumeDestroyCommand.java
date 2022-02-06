/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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

package me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes;

import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.host.Zone;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.util.Formatter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

public class VolumeDestroyCommand extends CommandNode {
  public VolumeDestroyCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Destroy a volume by index",
        "destroy");
    addParameter(Parameters.VOLUME_INDEX);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    int index = context.one(ParameterKeys.VOLUME_INDEX).orElseThrow(() -> new CommandException(Formatter.error(
        "You must supply a volume index"
    )));
    Host host = context.requireOne(ParameterKeys.HOST);
    if (!(host instanceof Zone)) {
      return CommandResult.error(Formatter.error(
          "You may not destroy volumes in host ___", host.name()
      ));
    }
    Zone zone = (Zone) host;
    try {
      zone.remove(index);
    } catch (IndexOutOfBoundsException e) {
      return CommandResult.error(Formatter.error(
          "Your index ___ is out of bounds", index
      ));
    }
    context.cause().audience().sendMessage(Formatter.success(
        "You deleted the volume of ___ at index ___",
        zone.name(), index
    ));
    return CommandResult.success();
  }
}
