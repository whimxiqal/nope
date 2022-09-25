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

package me.pietelite.nope.sponge.command.tree.host.blank.edit.profiles;

import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.HostEditor;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.host.Profile;
import me.pietelite.nope.common.util.ApiUtil;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.common.message.Formatter;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;

public class ProfileRemoveCommand extends CommandNode {
  public ProfileRemoveCommand(CommandNode parent) {
    super(parent, null, "Remove a profile from a host's profile list", "remove");
    addParameter(Parameters.PROFILE);
  }

  @Override
  public CommandResult execute(CommandContext context) {
    Host host = context.requireOne(Parameters.HOST);
    Profile profile = context.requireOne(Parameters.PROFILE);
    HostEditor editor = ApiUtil.editHost(host.name());
    if (editor.profiles().get(Nope.NOPE_SCOPE).contains(profile.name())) {
      return CommandResult.error(Formatter.error("That profile isn't in this host's profile list"));
    }
    if (host.name().equals(Nope.GLOBAL_ID) && profile.name().equals(Nope.GLOBAL_ID)) {
      return CommandResult.error(Formatter.error("You may not remove the global "
          + "profile from the global host"));
    }
    ApiUtil.editHost(host.name()).removeProfile(Nope.NOPE_SCOPE, profile.name());
    context.sendMessage(Identity.nil(), Formatter.success("Removed the profile ___ from host ___",
        profile.name(), host.name()));
    return CommandResult.success();
  }
}
