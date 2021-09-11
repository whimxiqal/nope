package com.minecraftonline.nope.sponge.command.tree.template.blank.edit;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.FunctionlessCommandNode;
import com.minecraftonline.nope.sponge.command.tree.template.blank.edit.target.PermissionCommand;
import com.minecraftonline.nope.sponge.command.tree.template.blank.edit.target.PlayerCommand;

public class TargetCommand extends FunctionlessCommandNode {
  public TargetCommand(CommandNode parent) {
    super(parent,
        Permissions.EDIT,
        "Edit the target of a setting",
        "target", "t");
    addChild(new PermissionCommand(this));
    addChild(new PlayerCommand(this));
  }
}
