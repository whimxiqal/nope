package com.minecraftonline.nope.command.region;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.RegionWandHandler;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.host.VolumeHost;
import com.minecraftonline.nope.permission.Permission;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

public class RegionMoveCommand extends LambdaCommandNode {
  public RegionMoveCommand(CommandNode parent) {
    super(parent, Permission.of("nope.region.edit.move"), Text.of("Allows the user to resize the region"), "move");

    addCommandElements(
            NopeArguments.host(Text.of("host")),
            NopeArguments.regionLocation(Text.of("selection"))
    );
    setExecutor((src, args) -> {
      Host host = args.requireOne("host");
      RegionWandHandler.Selection selection = args.requireOne("selection");

      if (!(host instanceof VolumeHost)) {
        src.sendMessage(Format.error("You can only move volume regions!"));
        return CommandResult.empty();
      }

      Vector3i min = selection.getMin();
      Vector3i max = selection.getMax();

      World world = selection.getWorld();

      if (world == null) {
        throw new IllegalStateException("World was null where it never should be!");
      }

      try {
        VolumeHost removed = Nope.getInstance().getHostTree().removeRegion(host.getName());
        VolumeHost created = Nope.getInstance().getHostTree().addRegion(
                host.getName(),
                world.getUniqueId(),
                min,
                max,
                host.getPriority()
        );
        created.putAll(removed.getAll());

      } catch (IllegalArgumentException e) {
        src.sendMessage(Format.error("Could not move region: " + e.getMessage()));
        return CommandResult.empty();
      }

      Nope.getInstance().getHostTree().save();
      src.sendMessage(Format.success(String.format(
              "Moved region %s to (%d, %d, %d) <-> (%d, %d, %d) in world %s",
              host.getName(),
              min.getX(), min.getY(), min.getZ(),
              max.getX(), max.getY(), max.getZ(),
              world.getName()
      )));

      return CommandResult.success();
    });
  }
}
