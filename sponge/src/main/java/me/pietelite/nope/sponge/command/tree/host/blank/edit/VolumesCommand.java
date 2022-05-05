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

package me.pietelite.nope.sponge.command.tree.host.blank.edit;

import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.FunctionlessCommandNode;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.VolumeCreateCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.VolumeDestroyCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.VolumeEditCommand;

public class VolumesCommand extends FunctionlessCommandNode {
  public VolumesCommand(CommandNode parent) {
    super(parent, null,
        "Alter the zones, which define the location of scenes",
        "zones");
    addChild(new VolumeCreateCommand(this));
    addChild(new VolumeDestroyCommand(this));
    addChild(new VolumeEditCommand(this));
  }

}
