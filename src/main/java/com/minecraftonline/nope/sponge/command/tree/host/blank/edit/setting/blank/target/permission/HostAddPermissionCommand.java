package com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank.target.permission;

import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.settingcollection.blank.edit.setting.blank.target.permission.AddPermissionCommand;

public class HostAddPermissionCommand extends AddPermissionCommand<Host> {

  public HostAddPermissionCommand(CommandNode parent) {
    super(parent, ParameterKeys.HOST, "host");
  }

}
