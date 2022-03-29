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

package me.pietelite.nope.sponge.command.tree.host.blank.edit.setting.blank.value;

import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.Flags;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.util.Formatter;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

public class ValueUnsetCommand extends CommandNode {

  public ValueUnsetCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Unset the value of a setting on a host",
        "unset");
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    SettingKey<?, ?, ?> settingKey = context.requireOne(ParameterKeys.SETTING_KEY);
    Host host = context.requireOne(Parameters.HOST);
    if (host.removeValue(settingKey) == null) {
      context.sendMessage(Identity.nil(),
          Formatter.error("Setting ___ is not set on host ___",
              settingKey.id(),
              host.name()));
    } else {
      context.sendMessage(Identity.nil(),
          Formatter.success("Value of setting ___ was removed on host ___",
              settingKey.id(),
              host.name()));
      host.save();
    }

    if (context.hasFlag(Flags.OPEN_EDITOR)) {
      Formatter.sendSettingEditor(context.cause().audience(), host, 1);
    }
    return CommandResult.success();
  }
}