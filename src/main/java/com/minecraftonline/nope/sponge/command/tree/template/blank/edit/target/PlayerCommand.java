package com.minecraftonline.nope.sponge.command.tree.template.blank.edit.target;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.FunctionlessCommandNode;

public class PlayerCommand extends FunctionlessCommandNode {

  public PlayerCommand(CommandNode parent) {
    super(parent,
        Permissions.EDIT,
        "Set players in the target of a setting",
        "user", "u", "player");
  }

}
