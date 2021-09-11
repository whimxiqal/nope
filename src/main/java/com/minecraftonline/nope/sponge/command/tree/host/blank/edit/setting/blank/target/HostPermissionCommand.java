package com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank.target;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.FunctionlessCommandNode;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank.target.permission.HostAddPermissionCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank.target.permission.HostClearPermissionCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank.target.permission.HostRemovePermissionCommand;

public class HostPermissionCommand extends FunctionlessCommandNode {

  public HostPermissionCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Set a permission on the target of a host",
        "permission", "perm");
    addChild(new HostAddPermissionCommand(this));
    addChild(new HostClearPermissionCommand(this));
    addChild(new HostRemovePermissionCommand(this));
  }

}
