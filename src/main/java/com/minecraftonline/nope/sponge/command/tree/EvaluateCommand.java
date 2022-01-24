/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Pieter Svenson
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

package com.minecraftonline.nope.sponge.command.tree;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.permission.Permission;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.util.Formatter;
import com.minecraftonline.nope.sponge.util.SpongeUtil;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class EvaluateCommand extends CommandNode {

  public EvaluateCommand(CommandNode parent) {
    super(parent, Permissions.INFO,
        "Evaluate the value for some setting",
        "evaluate");
    addParameter(Parameters.SETTING_KEY);
    addParameter(Parameters.PLAYER_OPTIONAL);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    SettingKey<?, ?, ?> key = context.requireOne(ParameterKeys.SETTING_KEY);
    Optional<ServerPlayer> playerOptional = context.one(ParameterKeys.PLAYER_OPTIONAL);
    ServerPlayer player;
    if (playerOptional.isPresent()) {
      player = playerOptional.get();
    } else {
      if (context.cause().root() instanceof ServerPlayer) {
        player = (ServerPlayer) context.cause().root();
      } else {
        return CommandResult.error(Formatter.error("You must specify a player"));
      }
    }

    player.sendMessage(Formatter.success("Value for setting ___ at ___, ___, ___ in ___ is ___",
        key.id(),
        player.serverLocation().blockX(),
        player.serverLocation().blockY(),
        player.serverLocation().blockZ(),
        player.serverLocation().world().key().formatted(),
        SpongeUtil.valueFor(key, player)));
    return CommandResult.success();
  }
}
