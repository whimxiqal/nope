/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) 2021 Pieter Svenson
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package com.minecraftonline.nope.sponge.command.tree.host.blank;

import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.host.Zone;
import com.minecraftonline.nope.common.math.Vector3d;
import com.minecraftonline.nope.common.math.Volume;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.util.EffectsUtil;
import com.minecraftonline.nope.sponge.util.Formatter;
import com.minecraftonline.nope.sponge.util.SpongeUtil;
import java.util.Optional;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerLocation;

public class HostShowCommand extends CommandNode {

  public HostShowCommand(CommandNode parent) {
    super(parent, Permissions.INFO,
        "Show the boundaries of volumes",
        "show");
    prefix(Parameters.HOST);
    addParameter(Parameters.INDEX_OPTIONAL);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    if (!(context.cause().root() instanceof ServerPlayer)) {
      return CommandResult.error(Formatter.error(
          "Only players may execute this command."
      ));
    }
    Player player = (Player) context.cause().root();

    Host host = context.requireOne(Parameters.HOST);
    if (!(host instanceof Zone)) {
      return CommandResult.error(Formatter.error(
          "You may not see the boundaries of this type of host"
      ));
    }
    Zone zone = (Zone) host;

    Optional<Integer> index = context.one(ParameterKeys.INDEX);
    if (index.isPresent()) {
      try {
        if (EffectsUtil.show(zone.volumes().get(index.get()), player)) {
          player.sendMessage(Formatter.success("Showing boundaries of zone ___, volume ___",
              zone.name(),
              index.get()));
        } else {
          player.sendMessage(Formatter.error("The boundaries of volume ___ of zone ___ is too far away",
              index.get(),
              zone.name()));
        }
      } catch (IndexOutOfBoundsException e) {
        return CommandResult.error(Formatter.error(
            "Index ___ is out of bounds", index.get()
        ));
      }
    } else {
      if (EffectsUtil.show(zone, player)) {
        player.sendMessage(Formatter.success("Showing boundaries of zone ___", zone.name()));
      } else {
        player.sendMessage(Formatter.success("The boundaries of zone ___ are too far away", zone.name()));
      }
    }
    return CommandResult.success();
  }
}
