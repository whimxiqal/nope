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
 *
 */

package me.pietelite.nope.sponge.command.tree;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.permission.Permission;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.FunctionlessCommandNode;
import me.pietelite.nope.sponge.command.parameters.Flags;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.util.Formatter;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class VerboseCommand extends FunctionlessCommandNode {

  public VerboseCommand(CommandNode parent) {
    super(parent, Permissions.DEBUG,
        "Send verbose messages about events throughout the server",
        "verbose");
    addChild(new VerboseOffCommand(this));
    addChild(new VerboseOnCommand(this));
  }

  static class VerboseOnCommand extends CommandNode {
    public VerboseOnCommand(CommandNode parent) {
      super(parent, Permissions.DEBUG,
          "Turn on verbose messaging",
          "on");
      addFlag(Flags.PLAYER);
      addFlag(Flags.SETTING_KEY);
      addFlag(Flags.TARGET);
    }

    @Override
    public CommandResult execute(CommandContext context) throws CommandException {
      if (!(context.cause().root() instanceof ServerPlayer)) {
        context.sendMessage(Identity.nil(), Formatter.error(
            "Support for non-player users is currently in development!"));
        return CommandResult.success();
      }
      ServerPlayer player = (ServerPlayer) context.cause().root();

      Collection<String> watchedPlayers = context.all(ParameterKeys.PLAYER)
          .stream()
          .map(ServerPlayer::name).collect(Collectors.toList());
      Collection<? extends SettingKey<?, ?, ?>> settingKeys = context.all(ParameterKeys.SETTING_KEY);
      Collection<? extends String> targets = context.all(ParameterKeys.VALUE);
      Nope.instance().debugManager().watch(player.uniqueId(),
          watchedPlayers,
          settingKeys,
          targets);

      Collection<Component> tokens = Stream.concat(watchedPlayers.stream(),
              Stream.concat(settingKeys.stream().map(SettingKey::id),
                  targets.stream()))
          .map(Formatter::accent)
          .collect(Collectors.toList());
      if (tokens.isEmpty()) {
        context.sendMessage(Identity.nil(), Formatter.success("You are now watching everything"));
      } else {
        context.sendMessage(Identity.nil(), Formatter.success("You are now watching: ")
            .append(Component.join(JoinConfiguration.separator(Formatter.dull(", ")), tokens)));
      }
      return CommandResult.success();
    }
  }

  static class VerboseOffCommand extends CommandNode {

    public VerboseOffCommand(CommandNode parent) {
      super(parent, null, "Turn off verbose messaging", "off");
    }

    @Override
    public CommandResult execute(CommandContext context) throws CommandException {
      if (!(context.cause().root() instanceof ServerPlayer)) {
        context.sendMessage(Identity.nil(), Formatter.error(
            "Support for non-player users is currently in development!"));
        return CommandResult.success();
      }
      UUID playerUuid = ((ServerPlayer) context.cause().root()).uniqueId();
      if (Nope.instance().debugManager().isWatching(playerUuid)) {
        Nope.instance().debugManager().stopWatching(playerUuid);
        context.sendMessage(Identity.nil(), Formatter.success("Stopped verbose messaging"));
      } else {
        context.sendMessage(Identity.nil(), Formatter.error("You do not have verbose messaging enabled"));
      }
      return CommandResult.success();
    }
  }
}
