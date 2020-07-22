package com.minecraftonline.nope.command.region;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.arguments.RegionWrapper;
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
    setCommandElement(GenericArguments.onlyOne(NopeArguments.regionCommandElement(Text.of("region"))));
    setExecutor((src, args) -> {
      RegionWrapper region = args.<RegionWrapper>getOne(Text.of("region")).get();
      WorldHost worldHost = region.getWorldHost();
      boolean isGlobal = region.getRegion() instanceof GlobalRegion;
      if (isGlobal) {
        worldHost.removeRegion(region.getRegionName());
        worldHost.addRegion(region.getRegionName(), new GlobalRegion(worldHost.getWorldUuid()));
        src.sendMessage(Format.info("Global region successfully cleared - it cannot be deleted"));
      }
      else {
        worldHost.removeRegion(region.getRegionName());
        src.sendMessage(Format.info("Region: '" + region + "' has been deleted"));
      }
      return CommandResult.success();
    });
  }
}
