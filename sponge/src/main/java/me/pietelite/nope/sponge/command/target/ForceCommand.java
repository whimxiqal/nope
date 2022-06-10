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

package me.pietelite.nope.sponge.command.target;

import java.util.function.Function;
import me.pietelite.nope.common.api.edit.TargetEditor;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.util.Formatter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

public class ForceCommand extends CommandNode {

  private final Function<CommandContext, TargetEditor> targetEditorFunction;

  public ForceCommand(CommandNode parent,
                      Function<CommandContext, TargetEditor> targetEditorFunction,
                      String targetable) {
    super(parent, null,
        "Ignore the effect of the \"unrestricted\" permission for " + targetable,
        "force");
    this.targetEditorFunction = targetEditorFunction;
  }

  @Override
  public CommandResult execute(CommandContext context) {
    TargetEditor editor = targetEditorFunction.apply(context);
    editor.bypassUnrestricted(!editor.bypassUnrestricted());
    context.cause().audience().sendMessage(Formatter.success(
        "Players with the ___ permission are now ___",
        Permissions.UNRESTRICTED.get(),
        editor.bypassUnrestricted() ? "affected" : "not affected"));
    return CommandResult.success();
  }

}
