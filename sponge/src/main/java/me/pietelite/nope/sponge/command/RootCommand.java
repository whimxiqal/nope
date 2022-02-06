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

package me.pietelite.nope.sponge.command;

import me.pietelite.nope.sponge.command.tree.EvaluateCommand;
import me.pietelite.nope.sponge.command.tree.HostCommand;
import me.pietelite.nope.sponge.command.tree.HostsCommand;
import me.pietelite.nope.sponge.command.tree.ReloadCommand;
import me.pietelite.nope.sponge.command.tree.SettingsCommand;
import me.pietelite.nope.sponge.command.tree.ToolCommand;
import me.pietelite.nope.sponge.util.Formatter;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

public class RootCommand extends CommandNode {

  public RootCommand() {
    super(null, null,
        "Root for all Nope commands", "nope");
    addChild(new EvaluateCommand(this));
    addChild(new HostCommand(this));
    addChild(new HostsCommand(this));
    addChild(new ReloadCommand(this));
    addChild(new SettingsCommand(this));
    addChild(new ToolCommand(this));
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    context.sendMessage(Identity.nil(), Formatter.WELCOME);
    return CommandResult.success();
  }
}
