/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) 2021 Pieter Svenson
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.FunctionlessCommandNode;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank.target.HostForceCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank.target.HostPermissionCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank.target.HostPlayerCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank.target.HostSetCommand;

public class HostTargetCommand extends FunctionlessCommandNode {
  public HostTargetCommand(CommandNode parent) {
    super(parent,
        Permissions.EDIT,
        "Edit the target of a setting",
        "target");
    prefix(Parameters.SETTING_KEY);
    addChild(new HostForceCommand(this));
    addChild(new HostPermissionCommand(this));
    addChild(new HostPlayerCommand(this));
    addChild(new HostSetCommand(this));
  }
}