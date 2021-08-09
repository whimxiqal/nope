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

package com.minecraftonline.nope.sponge.command.general;

import com.minecraftonline.nope.common.permission.Permission;
import javax.annotation.Nonnull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

/**
 * A type of command node to have no function when used.
 * Instead, if no subcommand or subcommand given, it
 * just says that there were insufficient arguments.
 */
public abstract class FunctionlessCommandNode extends CommandNode {

  public FunctionlessCommandNode(CommandNode parent,
                                 Permission permission,
                                 @Nonnull String description,
                                 @Nonnull String primaryAlias,
                                 @Nonnull String... otherAliases) {
    super(parent, permission, description, primaryAlias, otherAliases);
  }

  @Nonnull
  @Override
  public final CommandResult execute(CommandContext context) throws CommandException {
    if (this.getHelpCommand() == null) {
      return CommandResult.error(formatter().error("Too few arguments"));
    } else {
      return this.getHelpCommand().execute(context);
    }
  }
}
