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

package com.minecraftonline.nope.sponge.command.tree.host.blank.edit.volumes.create;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.host.Zone;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.math.Slab;
import com.minecraftonline.nope.common.math.Geometry;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.util.Formatter;
import com.minecraftonline.nope.sponge.util.SpongeUtil;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.server.ServerWorld;

public class SlabCommand extends CommandNode {

  public SlabCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Create a new slab",
        "slab");
    addParameter(Parameters.WORLD);
    addParameter(Parameters.POS_Y_1);
    addParameter(Parameters.POS_Y_2);
    addParameter(Parameters.RADIUS);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    Host host = context.requireOne(ParameterKeys.HOST);
    if (!(host instanceof Zone)) {
      return CommandResult.error(Formatter.error(
          "You may not create volumes for host ___", host.name()
      ));
    }
    Zone zone = (Zone) host;

    Slab slab;

    Optional<ServerWorld> world = context.one(ParameterKeys.WORLD);
    Optional<Integer> posY1 = context.one(ParameterKeys.POS_Y_1);
    Optional<Integer> posY2 = context.one(ParameterKeys.POS_Y_2);
    if (world.isPresent()
        && posY1.isPresent()
        && posY2.isPresent()) {
      slab = new Slab(Nope.instance().hostSystem().domain(SpongeUtil.worldToId(world.get())),
          Math.min(posY1.get(), posY2.get()),
          Math.max(posY2.get(), posY2.get()));
      if (!slab.valid()) {
        return CommandResult.error(Formatter.error(
            "Your designated ___ is invalid", "slab"
        ));
      }
    } else {
      Object cause = context.cause().root();
      Optional<Slab.Selection> selection = Optional.empty();
      if (cause instanceof Player) {
        Player player = (Player) context.cause().root();
        List<String> errors = new LinkedList<>();
        // TODO get selection
//        selection = SpongeNope.instance()
//            .selectionHandler()
//            .draft(player.uniqueId())
//            .build(errors);
      }
      if (selection.isPresent()) {
        slab = selection.get().solidify();
        if (!slab.valid()) {
          return CommandResult.error(Formatter.error(
              "Your ___ selection is invalid", "slab"
          ));
        }
      } else {
        return CommandResult.error(Formatter.error(
            "You must either supply the volume specifications for your ___ or use the ___",
            "slab", "slab tool"
        ));
      }
    }

    for (int i = 0; i < zone.volumes().size(); i++) {
      if (Geometry.intersects(zone.volumes().get(i), slab)) {
        context.cause().audience().sendMessage(Formatter.warn(
            "Your new ___ intersects with zone ___'s volume number ___ ",
            "slab", zone.name(), i
        ));
      }
    }
    Nope.instance().hostSystem().addVolume(slab, zone);
    zone.ensurePriority();
    context.cause().audience().sendMessage(Formatter.success(
        "A ___ was created on zone ___",
        "slab", zone.name()
    ));
    return CommandResult.success();

  }

}
