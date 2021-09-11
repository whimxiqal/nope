package com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank.target;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.FunctionlessCommandNode;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank.target.permission.HostRemovePermissionCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank.target.player.HostAddPlayerCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank.target.player.HostClearPlayerCommand;

public class HostPlayerCommand extends FunctionlessCommandNode {

  public HostPlayerCommand(CommandNode parent) {
    super(parent,
        Permissions.EDIT,
        "Set players in the target of a setting",
        "player", "user");
    addChild(new HostAddPlayerCommand(this));
    addChild(new HostClearPlayerCommand(this));
    addChild(new HostRemovePermissionCommand(this));
  }

}
