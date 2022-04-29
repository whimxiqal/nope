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

package me.pietelite.nope.sponge.command.tree.profile.blank.edit.setting.target.permission;

import java.util.Optional;
import me.pietelite.nope.common.host.Profile;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.Target;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.util.Formatter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

public class ClearPermissionCommand extends CommandNode {

  public ClearPermissionCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Clear all permissions from the target of a host",
        "clear", "reset");
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    Profile profile = context.requireOne(ParameterKeys.PROFILE);
    SettingKey<?, ?, ?> key = context.requireOne(ParameterKeys.SETTING_KEY);

    Optional<Target> target = profile.getTarget(key);
    if (target.isPresent()) {
      target.get().permissions().clear();
      context.cause().audience().sendMessage(Formatter.success(
          "All permissions cleared on key ___", key.id()
      ));
      return CommandResult.success();
    } else {
      return CommandResult.error(Formatter.error(
          "There is no target on key ___", key.id()
      ));
    }
  }

}
