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

package com.minecraftonline.nope.sponge.command.settingcollection.blank.edit.setting.blank.target.permission;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.Target;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.util.Formatter;
import java.util.Optional;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

public class RemovePermissionCommand<T extends SettingCollection> extends CommandNode {

  private final Parameter.Key<T> settingCollectionParameterKey;

  public RemovePermissionCommand(CommandNode parent,
                                 Parameter.Key<T> settingCollectionParameterKey,
                                 String collectionName) {
    super(parent, Permissions.EDIT,
        "Set a permission on the target of a " + collectionName,
        "remove", "unset");
    this.settingCollectionParameterKey = settingCollectionParameterKey;
    addParameter(Parameters.PERMISSION);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    T collection = context.requireOne(settingCollectionParameterKey);
    SettingKey<?, ?, ?> key = context.requireOne(ParameterKeys.SETTING_KEY);
    String permission = context.requireOne(ParameterKeys.PERMISSION);

    Optional<Target> targetOptional = collection.getTarget(key);
    Target target;
    if (targetOptional.isPresent()) {
      target = targetOptional.get();
    } else {
      return CommandResult.error(Formatter.error(
          "There is no target on key ___", key.id()
      ));
    }
    Boolean prev = target.permissions().remove(permission);
    if (prev != null) {
      context.cause().audience().sendMessage(Formatter.success(
          "Removed permission ___ = ___ set on key ___",
          permission, key.id()
      ));
    } else {
      return CommandResult.error(Formatter.success(
          "Permission ___ does not exist on key ___",
          permission, key.id()
      ));
    }
    collection.save();
    return CommandResult.success();
  }

}
