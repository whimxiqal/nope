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

package me.pietelite.nope.sponge.command.tree.profile.blank.edit.setting;

import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.FunctionlessCommandNode;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.command.tree.profile.blank.edit.setting.target.ForceCommand;
import me.pietelite.nope.sponge.command.tree.profile.blank.edit.setting.target.PermissionCommand;
import me.pietelite.nope.sponge.command.tree.profile.blank.edit.setting.target.PlayerCommand;
import me.pietelite.nope.sponge.command.tree.profile.blank.edit.setting.target.SetCommand;

public class TargetCommand extends FunctionlessCommandNode {
  public TargetCommand(CommandNode parent) {
    super(parent,
        Permissions.EDIT,
        "Edit the target of a setting",
        "target");
    prefix(Parameters.SETTING_KEY);
    addChild(new ForceCommand(this));
    addChild(new PermissionCommand(this));
    addChild(new PlayerCommand(this));
    addChild(new SetCommand(this));
  }
}
