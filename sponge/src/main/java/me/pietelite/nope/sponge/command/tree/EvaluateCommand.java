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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import me.pietelite.nope.common.host.Evaluation;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.util.Formatter;
import me.pietelite.nope.sponge.util.SpongeUtil;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
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
    return executeHelper(context, key);
  }

  private <X> CommandResult executeHelper(CommandContext context, SettingKey<X, ?, ?> key) {
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
    Evaluation<X> evaluation = SpongeNope.instance().system().lookup(key,
        player.uniqueId(),
        SpongeUtil.reduceLocation(player.serverLocation()));

    List<Component> contents = new LinkedList<>();
    contents.add(Formatter.accent("(default)")
        .append(Formatter.dull(" "))
        .append(Component.text(key.manager().printData(key.defaultData())).color(Formatter.WHITE)));
    evaluation.stream()
        .map(stage -> Formatter.host(stage.profile())
            .append(Formatter.dull(" -> "))
            .append(Component.text(key.manager().printData(stage.value()))))
        .forEach(contents::add);

    Formatter.paginator(Component.text("Evaluation of ")
            .color(Formatter.GOLD)
            .append(Formatter.accent(key.id()))
            .append(Component.text(" of ").color(Formatter.GOLD))
            .append(Formatter.accent(player.name())))
        .header(Formatter.accent("at ___, ___, ___ in ___",
            player.serverLocation().blockX(),
            player.serverLocation().blockY(),
            player.serverLocation().blockZ(),
            player.serverLocation().world().key().formatted()))
        .contents(contents)
        .sendTo(player);

    return CommandResult.success();
  }
}
