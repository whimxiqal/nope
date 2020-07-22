package com.minecraftonline.nope.command.region;

import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.FunctionlessCommandNode;
import com.minecraftonline.nope.permission.Permissions;
import org.spongepowered.api.text.Text;

public class RegionCommand extends FunctionlessCommandNode {
  public RegionCommand(CommandNode parent) {
    super(parent,
        Permissions.REGION,
        Text.of("Region sub command for all things regions"),
        "region",
        "rg");
    addChildren(new RegionWandCommand(this));
    addChildren(new RegionCreateCommand(this));
    addChildren(new ListRegionsCommand(this));
    addChildren(new DeleteRegionCommand(this));
    addChildren(new RegionInfoCommand(this));
  }
}
