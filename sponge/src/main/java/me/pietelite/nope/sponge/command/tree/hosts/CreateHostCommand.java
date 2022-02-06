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

package me.pietelite.nope.sponge.command.tree.hosts;

import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.host.Zone;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.Flags;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.util.Formatter;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

public class CreateHostCommand extends CommandNode {

  public CreateHostCommand(CommandNode parent) {
    super(parent,
        Permissions.CREATE,
        "Create a zone",
        "create");
    addParameter(Parameters.HOST_NAME);
    addFlag(Flags.PARENT);
    addFlag(Flags.PRIORITY);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    String name = context.requireOne(ParameterKeys.HOST_NAME);

    Optional<Host> existingHost = Nope.instance().hostSystem().host(name);
    if (existingHost.isPresent()) {
      return CommandResult.error(Formatter.error(
          "A host already exists with the name ___", existingHost.get().name()));
    }
    Zone parent = context.one(ParameterKeys.PARENT).orElse(null);
    int priority = context.one(ParameterKeys.PRIORITY).orElse(0);
    Zone zone = new Zone(name, parent, priority);
    Nope.instance().hostSystem().addZone(zone);
    zone.save();
    context.cause()
        .audience()
        .sendMessage(Formatter.success("Created zone ___",
            Component.text(zone.name())));
    return CommandResult.success();
  }

}
