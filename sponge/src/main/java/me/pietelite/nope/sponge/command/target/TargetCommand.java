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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import me.pietelite.nope.common.api.edit.TargetEditor;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.FunctionlessCommandNode;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

public class TargetCommand extends FunctionlessCommandNode {
  public TargetCommand(CommandNode parent,
                       Function<CommandContext, TargetEditor> targetEditorFunction,
                       @Nullable Parameter.Value<?> next,
                       String targetable) {
    super(parent, null,
        "Edit the target of " + targetable,
        "target");
    List<CommandNode> children = new LinkedList<>();
    children.add(new ForceCommand(this, targetEditorFunction, targetable));
    children.add(new PermissionCommand(this, targetEditorFunction, targetable));
    children.add(new PlayerCommand(this, targetEditorFunction, targetable));
    children.add(new SetCommand(this, targetEditorFunction, targetable));
    if (next != null) {
      children.forEach(child -> child.prefix(next));
    }
    children.forEach(this::addChild);
  }
}
