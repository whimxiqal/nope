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

package me.pietelite.nope.sponge.command.tree.profile.blank;

import me.pietelite.nope.common.host.Profile;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.common.util.ApiUtil;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.common.message.Formatter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;

public class ProfileDestroyCommand extends CommandNode {
  public ProfileDestroyCommand(CommandNode parent) {
    super(parent,
        Permissions.PROFILE_DESTROY,
        "Destroy a profile",
        "destroy");
    prefix(Parameters.PROFILE);
  }

  @Override
  public CommandResult execute(CommandContext context) {
    Profile profile = context.requireOne(ParameterKeys.PROFILE);
    ApiUtil.editNopeScope().editProfile(profile.name()).destroy();
    context.cause().audience().sendMessage(Formatter.success("Profile ___ was destroyed", profile.name()));
    return CommandResult.success();
  }
}
