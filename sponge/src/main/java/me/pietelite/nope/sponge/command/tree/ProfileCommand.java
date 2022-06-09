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

package me.pietelite.nope.sponge.command.tree;

import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.FunctionlessCommandNode;
import me.pietelite.nope.sponge.command.tree.host.blank.HostDestroyCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.HostEditCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.HostInfoCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.HostShowCommand;
import me.pietelite.nope.sponge.command.tree.profile.blank.ProfileDestroyCommand;
import me.pietelite.nope.sponge.command.tree.profile.blank.ProfileEditCommand;
import me.pietelite.nope.sponge.command.tree.profile.blank.ProfileInfoCommand;

public class ProfileCommand extends FunctionlessCommandNode {
  public ProfileCommand(CommandNode parent) {
    super(parent,
        null,
        "Edit and delete profiles",
        "profile");
    addChild(new ProfileDestroyCommand(this));
    addChild(new ProfileEditCommand(this));
    addChild(new ProfileInfoCommand(this));
  }
}
