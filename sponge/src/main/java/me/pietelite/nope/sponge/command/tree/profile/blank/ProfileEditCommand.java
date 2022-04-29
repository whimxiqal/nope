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

import me.pietelite.nope.common.api.NopeServiceProvider;
import me.pietelite.nope.common.host.Profile;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.FunctionlessCommandNode;
import me.pietelite.nope.sponge.command.InlineCommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.NameCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.PriorityCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.VolumesCommand;
import me.pietelite.nope.sponge.command.tree.profile.blank.edit.SettingCommand;
import me.pietelite.nope.sponge.command.tree.profile.blank.edit.SettingsCommand;
import me.pietelite.nope.sponge.util.Formatter;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.CommandResult;

public class ProfileEditCommand extends FunctionlessCommandNode {
  public ProfileEditCommand(CommandNode parent) {
    super(parent,
        Permissions.EDIT,
        "Edit settings and properties of a host",
        "edit");
    prefix(Parameters.HOST);

    CommandNode clearCommand = new InlineCommandNode(this, Permissions.EDIT,
        "Clear all settings on this host",
        "clear", context -> {
      Profile profile = context.requireOne(Parameters.PROFILE);
      profile.clear();
      profile.save();
      context.sendMessage(Identity.nil(), Formatter.success("Cleared ___", profile.name()));
      return CommandResult.success();
    });
    addChild(clearCommand);

    CommandNode nameCommand = new InlineCommandNode(this, Permissions.EDIT,
        "Edit the name of a host",
        "name", context -> {
      Profile profile = context.requireOne(ParameterKeys.PROFILE);
      String newName = context.requireOne(ParameterKeys.ID);
      if (NopeServiceProvider.service().editSystem()
          .editProfile(profile.name())
          .name(newName)
          .result().succeed()) {
        context.sendMessage(Identity.nil(), Formatter.success("Scene name ___ changed to ___",
            profile.name(),
            newName));
      } else {
        context.sendMessage(Identity.nil(), Formatter.error("Could not change scene name from ___ to ___",
            profile.name(),
            newName));
      }
      return CommandResult.success();
    });
    nameCommand.addParameter(Parameters.ID);
    addChild(nameCommand);

    addChild(new SettingCommand(this));
    addChild(new SettingsCommand(this));
  }

}
