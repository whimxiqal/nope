package com.minecraftonline.nope.command.region;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.RegionWandHandler;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.host.HostTree;
import com.minecraftonline.nope.host.VolumeHost;
import com.minecraftonline.nope.permission.Permission;
import com.minecraftonline.nope.util.Format;
import com.minecraftonline.nope.util.VolumeHostUtil;
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
        Host newHost = VolumeHostUtil.remakeRegion(src,
            (VolumeHost) host,
            host.getName(),
            world,
            min,
            max
        );
        if (newHost == null) {
          // Something bad happened. Already logged.
          return CommandResult.empty();
        }
      } catch (IllegalArgumentException e) {
        src.sendMessage(Format.error("Could not move region: " + e.getMessage()));
        return CommandResult.empty();
      }

      src.sendMessage(Format.success("Moved region " + host.getName() + ". New corners : " + min + " " + max + ". In world " + world.getName()));
      return CommandResult.success();
    });
  }
}
