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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Cylinder;
import me.pietelite.nope.common.math.Geometry;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.tool.CylinderSelection;
import me.pietelite.nope.sponge.util.EffectsUtil;
import me.pietelite.nope.sponge.util.Formatter;
import me.pietelite.nope.sponge.util.SpongeUtil;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerWorld;

/**
 * A command to create a {@link Cylinder}.
 */
public class CylinderCommand extends CommandNode {

  /**
   * Generic constructor.
   *
   * @param parent the parent node
   */
  public CylinderCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Create a new cylinder",
        "cylinder");
    addParameter(Parameters.CYLINDER);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    Host host = context.requireOne(ParameterKeys.HOST);
    if (!(host instanceof Scene)) {
      return CommandResult.error(Formatter.error(
          "You may not create volumes for host ___", host.name()
      ));
    }
    Scene scene = (Scene) host;

    Cylinder cylinder;

    Optional<ServerWorld> world = context.one(ParameterKeys.WORLD);
    Optional<Integer> posX = context.one(ParameterKeys.POS_X);
    Optional<Integer> minY = context.one(ParameterKeys.POS_Y_1);
    Optional<Integer> maxY = context.one(ParameterKeys.POS_Y_2);
    Optional<Integer> posZ = context.one(ParameterKeys.POS_Z);
    Optional<Double> radius = context.one(ParameterKeys.RADIUS);
    if (world.isPresent()
        && posX.isPresent()
        && minY.isPresent()
        && maxY.isPresent()
        && posZ.isPresent()
        && radius.isPresent()) {
      cylinder = new Cylinder(Nope.instance().system().domain(SpongeUtil.worldToId(world.get())),
          posX.get(),
          minY.get(),
          maxY.get(),
          posZ.get(),
          radius.get());
      if (!cylinder.valid()) {
        return CommandResult.error(Formatter.error(
            "Your designated ___ is invalid", "cylinder"
        ));
      }
    } else {
      Object cause = context.cause().root();
      if (cause instanceof Player) {
        Player player = (Player) context.cause().root();
        CylinderSelection selection = SpongeNope.instance()
            .selectionHandler()
            .cylinderDraft(player.uniqueId());
        if (selection == null) {
          return CommandResult.error(Formatter.error(
              "You must either supply the volume specifications for your ___"
                  + " or use the ___ to make a selection",
              "cylinder", "cylinder tool"
          ));
        }
        List<String> errors = new LinkedList<>();
        if (!selection.validate(errors)) {
          errors.forEach(error -> player.sendMessage(Formatter.error(error)));
        }
        cylinder = selection.build();
        if (cylinder == null) {
          return CommandResult.error(Formatter.error(
              "Your ___ selection is invalid", "cylinder"
          ));
        }
      } else {
        return CommandResult.error(Formatter.error(
            "You must supply the volume specifications for your ___",
            "cylinder", "cylinder tool"
        ));
      }
    }

    for (int i = 0; i < scene.volumes().size(); i++) {
      if (Geometry.intersects(scene.volumes().get(i), cylinder)) {
        context.cause().audience().sendMessage(Formatter.warn(
            "Your new ___ intersects with scene ___'s volume number ___ ",
            "cylinder", scene.name(), i
        ));
      }
    }
    Nope.instance().system().addVolume(cylinder, scene);
    scene.ensurePriority();
    context.cause().audience().sendMessage(Formatter.success(
        "A ___ was created on scene ___", "cylinder", scene.name()
    ));
    if (context.cause().root() instanceof ServerPlayer) {
      EffectsUtil.show(cylinder, (ServerPlayer) context.cause().root());
    }
    return CommandResult.success();

  }

}
