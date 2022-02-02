/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

package com.minecraftonline.nope.sponge.command.tree.template.blank.create;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.template.Template;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.util.Formatter;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

public class CreateCommand extends CommandNode {

  public CreateCommand(CommandNode parent) {
    super(parent,
        Permissions.EDIT,
        "Create a template of settings",
        "create", "c");
    prefix(Parameters.TEMPLATE);
    addParameter(Parameters.NAME);
    addParameter(Parameters.DESCRIPTION);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    String name = context.requireOne(ParameterKeys.NAME);
    String description = context.requireOne(ParameterKeys.DESCRIPTION);
    Template template = new Template(name, description);
    context.cause()
        .audience()
        .sendMessage(Formatter.success("Created template ",
            Component.text(template.name())));
    return CommandResult.success();
  }

}
