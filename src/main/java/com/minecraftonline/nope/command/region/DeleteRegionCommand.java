package com.minecraftonline.nope.command.region;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.control.GlobalRegion;
import com.minecraftonline.nope.control.WorldHost;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class DeleteRegionCommand extends LambdaCommandNode {
  public DeleteRegionCommand(CommandNode parent) {
    super(parent,
        Permissions.DELETE_REGIONS,
        Text.of("Delete a given region"),
        "delete",
        "remove");
    setCommandElement(GenericArguments.onlyOne(GenericArguments.string(Text.of("region"))));
    setExecutor((src, args) -> {
      if (!(src instanceof Player)) {
        src.sendMessage(Format.error("You must be a player to use this command"));
        return CommandResult.empty();
      }
      String region = args.<String>getOne(Text.of("region")).get();
      WorldHost worldHost = Nope.getInstance().getGlobalHost().getWorld(((Player)src).getWorld());
      if (!worldHost.getRegions().containsKey(region)) {
        src.sendMessage(Format.error("No region with the name: '" + region + "'"));
        return CommandResult.empty();
      }
      boolean isGlobal = region.equals("__global__");
      if (isGlobal) {
        worldHost.removeRegion(region);
        worldHost.addRegion(region, new GlobalRegion(worldHost.getWorldUuid()));
        src.sendMessage(Format.info("Global region successfully cleared - it cannot be deleted"));
      }
      else {
        worldHost.removeRegion(region);
        src.sendMessage(Format.info("Region: '" + region + "' has been deleted"));
      }
      return CommandResult.success();
    });
  }
}
