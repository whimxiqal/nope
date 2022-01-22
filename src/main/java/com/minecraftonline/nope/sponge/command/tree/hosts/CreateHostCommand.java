/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) 2021 Pieter Svenson
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package com.minecraftonline.nope.sponge.command.tree.hosts;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.host.Zone;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.Flags;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.util.Formatter;
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
    addParameter(Parameters.NAME);
    addFlag(Flags.PARENT);
    addFlag(Flags.PRIORITY);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    String name = context.requireOne(ParameterKeys.NAME);
    if (Nope.instance().hostSystem().hasName(name)) {
      return CommandResult.error(Formatter.error(
          "A host already exists with the name ___", name
      ));
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
