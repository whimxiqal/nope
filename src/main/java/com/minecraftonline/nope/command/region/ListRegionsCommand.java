package com.minecraftonline.nope.command.region;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.WorldHost;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Map;

public class ListRegionsCommand extends LambdaCommandNode {
  public ListRegionsCommand(CommandNode parent) {
    super(parent,
        Permissions.LIST_REGIONS,
        Text.of("List the regions in the current world"),
        "list",
        "l");
    setExecutor((src, args) -> {
      if (!(src instanceof Player)) {
        src.sendMessage(Format.error("You must be a player to use this command!"));
        return CommandResult.empty();
      }
      WorldHost host = Nope.getInstance().getGlobalHost().getWorld(((Player)src).getWorld());
      src.sendMessage(Format.info("------ Regions ------"));
      for (Map.Entry<String, Region> entry : host.getRegions().entrySet()) {
        src.sendMessage(Format.info(entry.getKey()));
      }
      return CommandResult.success();
    });
  }
}
