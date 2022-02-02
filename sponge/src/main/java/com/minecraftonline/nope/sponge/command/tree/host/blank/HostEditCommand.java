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

package com.minecraftonline.nope.sponge.command.tree.host.blank;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.FunctionlessCommandNode;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.HostSettingCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.NameCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.PriorityCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.VolumesCommand;

public class HostEditCommand extends FunctionlessCommandNode {
  public HostEditCommand(CommandNode parent) {
    super(parent,
        Permissions.EDIT,
        "Edit settings and properties of a host",
        "edit");
    prefix(Parameters.HOST);
    addChild(new HostSettingCommand(this));
    addChild(new NameCommand(this));
    addChild(new PriorityCommand(this));
    addChild(new VolumesCommand(this));
  }

}
