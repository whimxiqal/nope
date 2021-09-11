package com.minecraftonline.nope.sponge.command.tree.template.blank.edit.target;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.FunctionlessCommandNode;

public class PermissionCommand extends FunctionlessCommandNode {

  public PermissionCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Set a permission on the target of a template",
        "permission", "perm", "p");
  }

}
