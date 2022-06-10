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
import me.pietelite.nope.sponge.command.FunctionlessCommandNode;
import me.pietelite.nope.sponge.command.InlineCommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.command.target.TargetCommand;
import me.pietelite.nope.sponge.command.tree.profile.blank.edit.SettingCommand;
import me.pietelite.nope.sponge.command.tree.profile.blank.edit.SettingsCommand;
import me.pietelite.nope.sponge.util.Formatter;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.CommandResult;

public class ProfileEditCommand extends FunctionlessCommandNode {
  public ProfileEditCommand(CommandNode parent) {
    super(parent, Permissions.PROFILE_EDIT,
        "Edit settings and properties of a profile",
        "edit");
    prefix(Parameters.PROFILE);

    CommandNode clearCommand = new InlineCommandNode(this, null,
        "Clear all settings on this host",
        "clear", context -> {
      Profile profile = context.requireOne(Parameters.PROFILE);
      profile.clear();
      profile.save();
      context.sendMessage(Identity.nil(), Formatter.success("Cleared ___", profile.name()));
      return CommandResult.success();
    });
    addChild(clearCommand);

    CommandNode nameCommand = new InlineCommandNode(this, null,
        "Edit the name of a host",
        "name", context -> {
      Profile profile = context.requireOne(ParameterKeys.PROFILE);
      String newName = context.requireOne(ParameterKeys.ID);
      if (ApiUtil.editNopeScope().editProfile(profile.name())
          .name(newName)) {
        context.sendMessage(Identity.nil(), Formatter.success("Name of profile ___ changed to ___",
            profile.name(),
            newName));
      } else {
        context.sendMessage(Identity.nil(), Formatter.error("The profile is already called ___"));
      }
      return CommandResult.success();
    });
    nameCommand.addParameter(Parameters.ID);
    addChild(nameCommand);

    addChild(new TargetCommand(this, context -> {
      Profile profile = context.requireOne(Parameters.PROFILE);
      return ApiUtil.editNopeScope().editProfile(profile.name())
          .editTarget();
    }, null, "all settings on a profile"));
    addChild(new SettingCommand(this));
    addChild(new SettingsCommand(this));
  }

}
