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

package me.pietelite.nope.sponge.command.tree.host.blank;

import java.util.Optional;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.util.EffectsUtil;
import me.pietelite.nope.sponge.util.Formatter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class HostShowCommand extends CommandNode {

  public HostShowCommand(CommandNode parent) {
    super(parent, Permissions.INFO,
        "Show the boundaries of volumes",
        "show");
    prefix(Parameters.HOST);
    addParameter(Parameters.VOLUME_INDEX_OPTIONAL);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    if (!(context.cause().root() instanceof ServerPlayer)) {
      return CommandResult.error(Formatter.error(
          "Only players may execute this command."
      ));
    }
    ServerPlayer player = (ServerPlayer) context.cause().root();

    Host host = context.requireOne(Parameters.HOST);
    if (!(host instanceof Scene)) {
      return CommandResult.error(Formatter.error(
          "You may not see the boundaries of this type of host"
      ));
    }
    Scene scene = (Scene) host;

    Optional<Integer> index = context.one(ParameterKeys.VOLUME_INDEX);
    if (index.isPresent()) {
      try {
        if (EffectsUtil.show(scene.volumes().get(index.get()), player)) {
          player.sendMessage(Formatter.success("Showing boundaries of scene ___, volume ___",
              scene.name(),
              index.get()));
        } else {
          player.sendMessage(Formatter.error("The boundaries of volume ___ of scene ___ is too far away",
              index.get(),
              scene.name()));
        }
      } catch (IndexOutOfBoundsException e) {
        return CommandResult.error(Formatter.error(
            "Index ___ is out of bounds", index.get()
        ));
      }
    } else {
      if (EffectsUtil.show(scene, player)) {
        player.sendMessage(Formatter.success("Showing boundaries of scene ___", scene.name()));
      } else {
        player.sendMessage(Formatter.success("The boundaries of scene ___ are too far away", scene.name()));
      }
    }
    return CommandResult.success();
  }
}
