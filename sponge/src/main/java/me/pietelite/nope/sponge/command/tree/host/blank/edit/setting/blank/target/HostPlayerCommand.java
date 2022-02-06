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

package me.pietelite.nope.sponge.command.tree.host.blank.edit.setting.blank.target;

import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.FunctionlessCommandNode;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.setting.blank.target.permission.HostRemovePermissionCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.setting.blank.target.player.HostAddPlayerCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.setting.blank.target.player.HostClearPlayerCommand;

public class HostPlayerCommand extends FunctionlessCommandNode {

  public HostPlayerCommand(CommandNode parent) {
    super(parent,
        Permissions.EDIT,
        "Set players in the target of a setting",
        "player", "user");
    addChild(new HostAddPlayerCommand(this));
    addChild(new HostClearPlayerCommand(this));
    addChild(new HostRemovePermissionCommand(this));
  }

}
