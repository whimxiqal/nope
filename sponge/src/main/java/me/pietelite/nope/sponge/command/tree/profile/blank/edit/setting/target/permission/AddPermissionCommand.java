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
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.util.Formatter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

/**
 * Command to add a permission target to a host setting.
 */
public class AddPermissionCommand extends CommandNode {

  /**
   * Generic constructor.
   *
   * @param parent parent command
   */
  public AddPermissionCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Set a permission on the target of a host",
        "add", "set");
    addParameter(Parameters.PERMISSION);
    addParameter(Parameters.PERMISSION_VALUE);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    Profile profile = context.requireOne(ParameterKeys.PROFILE);
    SettingKey<?, ?, ?> key = context.requireOne(ParameterKeys.SETTING_KEY);
    String permission = context.requireOne(ParameterKeys.PERMISSION);
    boolean value = context.requireOne(ParameterKeys.PERMISSION_VALUE);

    Optional<Target> targetOptional = profile.getTarget(key);
    Target target;
    if (targetOptional.isPresent()) {
      target = targetOptional.get();
    } else {
      target = Target.all();
      profile.setTarget(key, target);
    }
    Boolean prev = target.permissions().put(permission, value);
    if (prev != null) {
      if (prev == value) {
        return CommandResult.error(Formatter.error(
            "Permission ___ already set to ___ on key ___",
            permission, value, key.id()
        ));
      }
      context.cause().audience().sendMessage(Formatter.success(
          "Removed permission ___ = ___ set on key ___",
          permission, prev, key.id()
      ));
    }
    context.cause().audience().sendMessage(Formatter.success(
        "Added permission ___ = ___ set on key ___",
        permission, value, key.id()
    ));
    profile.save();
    return CommandResult.success();
  }

}
