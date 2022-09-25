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

package me.pietelite.nope.sponge.command.target.permission;

import java.util.function.Function;
import me.pietelite.nope.common.api.edit.TargetEditor;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.common.message.Formatter;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;

/**
 * Command to add a permission target to a host setting.
 */
public class AddPermissionCommand extends CommandNode {

  private final Function<CommandContext, TargetEditor> targetEditorFunction;

  public AddPermissionCommand(CommandNode parent,
                              Function<CommandContext, TargetEditor> targetEditorFunction,
                              String targetable) {
    super(parent, null,
        "Set a permission on the target of " + targetable, "add");
    this.targetEditorFunction = targetEditorFunction;
    addParameter(Parameters.PERMISSION);
    addParameter(Parameters.PERMISSION_VALUE);
  }

  @Override
  public CommandResult execute(CommandContext context) {
    TargetEditor editor = targetEditorFunction.apply(context);
    String permission = context.requireOne(ParameterKeys.PERMISSION);
    boolean value = context.requireOne(ParameterKeys.PERMISSION_VALUE);
    if (editor.addPermission(permission, value)) {
      context.sendMessage(Identity.nil(), Formatter.success("Added ___ on target",
          permission + " = " + value));
    } else {
      context.sendMessage(Identity.nil(), Formatter.error("This permission was already set here"));
    }
    return CommandResult.success();
  }

}
