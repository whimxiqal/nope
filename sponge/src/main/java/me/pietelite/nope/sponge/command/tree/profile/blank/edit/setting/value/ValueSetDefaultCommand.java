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

package me.pietelite.nope.sponge.command.tree.profile.blank.edit.setting.value;

import me.pietelite.nope.common.host.Profile;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.Flags;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.common.message.Formatter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;

public class ValueSetDefaultCommand extends CommandNode {
  public ValueSetDefaultCommand(CommandNode parent) {
    super(parent, null,
        "Set the universal default value of a setting on a host",
        "setdefault");
  }

  @Override
  public CommandResult execute(CommandContext context) {
    SettingKey<?, ?, ?> settingKey = context.requireOne(ParameterKeys.SETTING_KEY);
    Profile profile = context.requireOne(Parameters.PROFILE);
    profile.setDefaultValue(settingKey);

    if (context.hasFlag(Flags.OPEN_EDITOR)) {
      Formatter.sendSettingEditor(context.cause().audience(), profile, 1);
    }
    return CommandResult.success();
  }
}
