package com.minecraftonline.nope.command.region;

import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.permission.Permission;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

public class RegionMoveCommand extends LambdaCommandNode {
  public RegionMoveCommand(CommandNode parent) {
    super(parent, Permission.of("nope.region.edit.move"), Text.of("Allows the user to resize the region"), "move");

    addCommandElements(GenericArguments.onlyOne(NopeArguments.host(Text.of("region"))));
    setExecutor((src, args) -> {
      src.sendMessage(Format.error("Command not implemented yet!"));
      return CommandResult.empty();
      /*if (!(src instanceof Player)) {
        src.sendMessage(Format.error("You must execute this command as a player"));
        return CommandResult.empty();
      }
      HostWrapper hostWrapper = args.<HostWrapper>getOne(Text.of("region")).get();
      Region region = hostWrapper.getRegion();
      Player player = (Player)src;

      if (hostWrapper.getRegion() instanceof GlobalRegion) {
        player.sendMessage(Format.error("You cannot move the global region!"));
        return CommandResult.success();
      }

      RegionWandHandler.Selection selection = Nope.getInstance().getRegionWandHandler().getSelectionMap().get(player);
      if (selection == null || !selection.isComplete()) {
        player.sendMessage(Format.error("You must have a selection (use /nope region wand to get a wand)"));
        return CommandResult.empty();
      }

      assert selection.getWorld() != null; // Not null selection is complete
      if (!hostWrapper.getWorldHost().getWorldUuid().equals(selection.getWorld().getUniqueId())) {
        player.sendMessage(Format.error("You cannot change the region's world"));
      }

      // We need to set it like this because otherwise we end up with min-maxing old values with new
      ((RegularRegion)region).moveTo(selection.getPos1(), selection.getPos2());

      return CommandResult.success();*/
    });
  }
}
