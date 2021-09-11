package com.minecraftonline.nope.sponge.command.tree.host.blank.edit.volumes.create;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.host.Zone;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.math.Cylinder;
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

public class CylinderCommand extends CommandNode {

  public CylinderCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Create a new cylinder",
        "cylinder");
    addParameter(Parameters.WORLD);
    addParameter(Parameters.POS_X);
    addParameter(Parameters.POS_Y_1);
    addParameter(Parameters.POS_Y_2);
    addParameter(Parameters.POS_Z);
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
      cylinder = new Cylinder(Nope.instance().hostSystem().domain(SpongeUtil.worldToId(world.get())),
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
      Optional<Cylinder.Selection> selection = Optional.empty();
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
        cylinder = selection.get().solidify();
        if (!cylinder.valid()) {
          return CommandResult.error(Formatter.error(
              "Your ___ selection is invalid", "cylinder"
          ));
        }
      } else {
        return CommandResult.error(Formatter.error(
            "You must either supply the volume specifications for your ___ or use the ___",
            "cylinder", "cylinder tool"
        ));
      }
    }

    for (int i = 0; i < zone.volumes().size(); i++) {
      if (Geometry.intersects(zone.volumes().get(i), cylinder)) {
        context.cause().audience().sendMessage(Formatter.warn(
            "Your new ___ intersects with zone ___'s volume number ___ ",
            "cylinder", zone.name(), i
        ));
      }
    }
    Nope.instance().hostSystem().addVolume(cylinder, zone);
    zone.ensurePriority();
    context.cause().audience().sendMessage(Formatter.success(
        "A ___ was created on zone ___", "cylinder", zone.name()
    ));
    return CommandResult.success();

  }

}
