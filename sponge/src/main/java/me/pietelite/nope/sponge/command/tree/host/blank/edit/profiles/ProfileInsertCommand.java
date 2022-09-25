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
import org.spongepowered.api.command.parameter.Parameter;

public class ProfileInsertCommand extends CommandNode {

  private static final Parameter.Key<Integer> indexParameter = Parameter.key("index", Integer.class);

  public ProfileInsertCommand(CommandNode parent) {
    super(parent, null, "Insert profile to a host's profile list", "insert");
    addParameter(Parameters.PROFILE);
    addParameter(Parameter.integerNumber().key(indexParameter).build());
  }

  @Override
  public CommandResult execute(CommandContext context) {
    Host host = context.requireOne(Parameters.HOST);
    Profile profile = context.requireOne(Parameters.PROFILE);
    int index = context.requireOne(indexParameter);
    HostEditor editor = ApiUtil.editHost(host.name());
    if (index <= 0 || index > editor.profiles().size() + 1) {
      return CommandResult.error(Formatter.error("That index is out of bounds"));
    }
    if (editor.profiles().get(Nope.NOPE_SCOPE).contains(profile.name())) {
      return CommandResult.error(Formatter.error("That profile already exists on this host"));
    }
    editor.addProfile(Nope.NOPE_SCOPE, profile.name(), index);
    context.sendMessage(Identity.nil(), Formatter.success("Added profile ___ at index ___ on host ___",
        profile.name(), index, host.name()));
    return CommandResult.success();
  }
}
