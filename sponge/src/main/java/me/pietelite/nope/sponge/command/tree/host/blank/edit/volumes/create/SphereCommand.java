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

package me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create;

import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.host.Zone;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.common.math.Sphere;
import me.pietelite.nope.common.math.Geometry;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.tool.SphereSelection;
import me.pietelite.nope.sponge.util.EffectsUtil;
import me.pietelite.nope.sponge.util.Formatter;
import me.pietelite.nope.sponge.util.SpongeUtil;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.server.ServerWorld;

public class SphereCommand extends CommandNode{

  public SphereCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Create a new sphere",
        "sphere");
    addParameter(Parameters.SPHERE);
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

    Sphere sphere;

    Optional<ServerWorld> world = context.one(ParameterKeys.WORLD);
    Optional<Integer> posX = context.one(ParameterKeys.POS_X);
    Optional<Integer> posY = context.one(ParameterKeys.POS_Y);
    Optional<Integer> posZ = context.one(ParameterKeys.POS_Z);
    Optional<Double> radius = context.one(ParameterKeys.RADIUS);
    if (world.isPresent()
        && posX.isPresent()
        && posY.isPresent()
        && posZ.isPresent()
        && radius.isPresent()) {
      sphere = new Sphere(Nope.instance().hostSystem().domain(SpongeUtil.worldToId(world.get())),
          posX.get(),
          posY.get(),
          posZ.get(),
          radius.get());
      if (!sphere.valid()) {
        return CommandResult.error(Formatter.error(
            "Your designated ___ is invalid", "sphere"
        ));
      }
    } else {
      Object cause = context.cause().root();
      if (cause instanceof Player) {
        Player player = (Player) context.cause().root();
        SphereSelection selection = SpongeNope.instance()
            .selectionHandler()
            .sphereDraft(player.uniqueId());
        if (selection == null) {
          return CommandResult.error(Formatter.error(
              "You must either supply the volume specifications for your ___"
                  + " or use the ___ to make a selection",
              "sphere", "sphere tool"
          ));
        }
        List<String> errors = new LinkedList<>();
        if (!selection.validate(errors)) {
          errors.forEach(error -> player.sendMessage(Formatter.error(error)));
        }
        sphere = selection.build();
        if (sphere == null) {
          return CommandResult.error(Formatter.error(
              "Your ___ selection is invalid", "sphere"
          ));
        }
      } else {
        return CommandResult.error(Formatter.error(
            "You must supply the volume specifications for your ___",
            "sphere", "sphere tool"
        ));
      }
    }

    for (int i = 0; i < zone.volumes().size(); i++) {
      if (Geometry.intersects(zone.volumes().get(i), sphere)) {
        context.cause().audience().sendMessage(Formatter.warn(
            "Your new ___ intersects with zone ___'s volume number ___ ",
            "sphere", zone.name(), i
        ));
      }
    }
    Nope.instance().hostSystem().addVolume(sphere, zone);
    zone.ensurePriority();
    context.cause().audience().sendMessage(Formatter.success(
        "A ___ was created on zone ___",
        "sphere", zone.name()
    ));
    if (context.cause().root() instanceof Player) {
      EffectsUtil.show(sphere, (Player) context.cause().root());
    }
    return CommandResult.success();

  }
}
