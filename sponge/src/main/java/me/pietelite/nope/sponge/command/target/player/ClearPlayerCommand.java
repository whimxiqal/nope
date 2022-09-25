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

package me.pietelite.nope.sponge.command.target.player;

import java.util.function.Function;
import me.pietelite.nope.common.api.edit.TargetEditor;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.common.message.Formatter;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;

public class ClearPlayerCommand extends CommandNode {

  private final Function<CommandContext, TargetEditor> targetEditorFunction;

  public ClearPlayerCommand(CommandNode parent,
                            Function<CommandContext, TargetEditor> targetEditorFunction,
                            String targetable) {
    super(parent, null,
        "Clear all players from the target of " + targetable,
        "clear", "reset");
    this.targetEditorFunction = targetEditorFunction;
  }

  @Override
  public CommandResult execute(CommandContext context) {
    TargetEditor editor = targetEditorFunction.apply(context);
    if (editor.clearPlayers()) {
      context.sendMessage(Identity.nil(), Formatter.success("Cleared all players from target"));
    } else {
      context.sendMessage(Identity.nil(), Formatter.error("There are no players on this target"));
    }
    return CommandResult.success();
  }
}