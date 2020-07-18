package com.minecraftonline.nope.command;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.RegionWandHandler;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.control.RegularRegion;
import com.minecraftonline.nope.control.WorldHost;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Map;

public class RegionCreateCommand extends LambdaCommandNode {
  public RegionCreateCommand(CommandNode parent) {
    super(parent,
        Permissions.CREATE_REGION,
        Text.of("Create a region with current selection and given name"),
        "create",
        "new");
    setCommandElement(GenericArguments.onlyOne(GenericArguments.string(Text.of("name"))));
    setExecutor((src, args) -> {
      String name = (String) args.getOne(Text.of("name")).get();
      if (!(src instanceof Player)) {
        src.sendMessage(Format.error("You must execute this command as a player"));
        return CommandResult.empty();
      }
      Player player = (Player)src;
      RegionWandHandler.Selection selection = Nope.getInstance().getRegionWandHandler().getSelectionMap().get(player);
      if (selection == null || !selection.isComplete()) {
        player.sendMessage(Format.error("You must have a selection (use /nope region wand to get a wand)"));
        return CommandResult.empty();
      }
      WorldHost worldHost = Nope.getInstance().getGlobalHost().getWorld(selection.getWorld());
      worldHost.getRegions().entrySet().stream()
          .map(Map.Entry::getKey)
          .forEach(regionName -> Nope.getInstance().getLogger().info("currently there is region with name: " + regionName));
      if (worldHost.getRegions().get(name) != null) {
        player.sendMessage(Format.error("There is already a region with the name '" + name + "'"));
        return CommandResult.empty();
      }
      worldHost.addRegion(name, new RegularRegion(selection.getWorld(), selection.getPos1(), selection.getPos2()));
      player.sendMessage(Format.info("Region '" + name + "' successfully created"));
      return CommandResult.success();
    });
  }
}
