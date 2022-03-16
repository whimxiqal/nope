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

package me.pietelite.nope.sponge.command.tree.host.blank.edit.setting.blank.target.player;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.Target;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.util.Formatter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;

public class HostRemovePlayerCommand extends CommandNode {

  public HostRemovePlayerCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Remove a player from the target of a host",
        "remove");
    addParameter(Parameters.PLAYER_LIST);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    Host host = context.requireOne(Parameters.HOST);
    SettingKey<?, ?, ?> key = context.requireOne(ParameterKeys.SETTING_KEY);
    Collection<CompletableFuture<GameProfile>> players = context.requireOne(ParameterKeys.PLAYER_LIST);

    Target target = host.computeTarget(key, Target::none);
    String listType = target.isWhitelist() ? "whitelist" : "blacklist";
    Sponge.asyncScheduler()
        .submit(Task.builder().execute(() -> {
              GameProfile profile;
              for (CompletableFuture<GameProfile> player : players) {
                try {
                  profile = player.get();
                } catch (InterruptedException | ExecutionException e) {
                  e.printStackTrace();
                  continue;
                }
                if (target.users().remove(profile.uuid())) {
                  context.cause().audience().sendMessage(Formatter.success(
                      "Removed user ___ to the ___ on ___ ",
                      profile.name().orElse(profile.uuid().toString()), listType, key.id()
                  ));
                } else {
                  context.cause().audience().sendMessage(Formatter.warn(
                      "Could not remove user ___ to the ___ on ___ ",
                      profile.name().orElse(profile.uuid().toString()), listType, key.id()
                  ));
                }
              }
            }).plugin(SpongeNope.instance().pluginContainer())
            .build());
    return CommandResult.success();
  }
}
