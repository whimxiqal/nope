package com.minecraftonline.nope.command.region;

import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.permission.Permission;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

public class RegionSetPriorityCommand extends LambdaCommandNode {

  public RegionSetPriorityCommand(CommandNode parent) {
    super(parent, Permission.of("nope.region.edit.priority"), Text.of("Allows the user to set the priority of a region"), "setpriority");

    addCommandElements(GenericArguments.onlyOne(NopeArguments.host(Text.of("region"))), GenericArguments.integer(Text.of("priority")));
    setExecutor((src, args) -> {
      src.sendMessage(Format.error("Command not implemented yet!"));
      return CommandResult.empty();
      /*HostWrapper hostWrapper = args.<HostWrapper>getOne(Text.of("region")).get();
      Region region = hostWrapper.getRegion();
      int priority = args.<Integer>getOne(Text.of("priority")).get();

      region.set(Settings.REGION_PRIORITY, priority);
      src.sendMessage(Text.of(TextColors.GREEN, "Successfully set priority on region '" + hostWrapper.getRegionName()));
      return CommandResult.success();*/
    });
  }
}
