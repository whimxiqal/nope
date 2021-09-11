package com.minecraftonline.nope.sponge.command.tree.host.blank.edit.volumes.create;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.host.Zone;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.math.Cuboid;
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

public class CuboidCommand extends CommandNode {

  public CuboidCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Create a new box",
        "box");
    addParameter(Parameters.WORLD);
    addParameter(Parameters.POS_X_1);
    addParameter(Parameters.POS_Y_1);
    addParameter(Parameters.POS_Z_1);
    addParameter(Parameters.POS_X_2);
    addParameter(Parameters.POS_Y_2);
    addParameter(Parameters.POS_Z_2);
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

    Cuboid cuboid;

    Optional<ServerWorld> world = context.one(ParameterKeys.WORLD);
    Optional<Integer> posX1 = context.one(ParameterKeys.POS_X_1);
    Optional<Integer> posY1 = context.one(ParameterKeys.POS_Y_1);
    Optional<Integer> posZ1 = context.one(ParameterKeys.POS_Z_1);
    Optional<Integer> posX2 = context.one(ParameterKeys.POS_X_2);
    Optional<Integer> posY2 = context.one(ParameterKeys.POS_Y_2);
    Optional<Integer> posZ2 = context.one(ParameterKeys.POS_Z_2);
    if (world.isPresent()
        && posX1.isPresent()
        && posY1.isPresent()
        && posZ1.isPresent()
        && posX2.isPresent()
        && posY2.isPresent()
        && posZ2.isPresent()) {
      cuboid = new Cuboid(Nope.instance().hostSystem().domain(SpongeUtil.worldToId(world.get())),
          Math.min(posX1.get(), posX2.get()),
          Math.min(posY1.get(), posY2.get()),
          Math.min(posZ1.get(), posZ2.get()),
          Math.max(posX1.get(), posX2.get()),
          Math.max(posY1.get(), posY2.get()),
          Math.max(posZ1.get(), posZ2.get()));
      if (!cuboid.valid()) {
        return CommandResult.error(Formatter.error(
            "Your designated ___ is invalid", "box"
        ));
      }
    } else {
      Object cause = context.cause().root();
      Optional<Cuboid.Selection> selection = Optional.empty();
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
        cuboid = selection.get().solidify();
        if (!cuboid.valid()) {
          return CommandResult.error(Formatter.error(
              "Your ___ selection is invalid", "box"
          ));
        }
      } else {
        return CommandResult.error(Formatter.error(
            "You must either supply the volume specifications for your ___ or use the ___",
            "box", "box tool"
        ));
      }
    }

    for (int i = 0; i < zone.volumes().size(); i++) {
      if (Geometry.intersects(zone.volumes().get(i), cuboid)) {
        context.cause().audience().sendMessage(Formatter.warn(
            "Your new box intersects with zone ___'s volume number ___ ",
            zone.name(), i
        ));
      }
    }
    Nope.instance().hostSystem().addVolume(cuboid, zone);
    zone.ensurePriority();
    context.cause().audience().sendMessage(Formatter.success(
        "A box was created on zone ___", zone.name()
    ));
    return CommandResult.success();

  }
}
