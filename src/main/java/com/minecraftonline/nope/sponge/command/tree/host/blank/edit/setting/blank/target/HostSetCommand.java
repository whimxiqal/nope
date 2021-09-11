package com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank.target;

import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.settingcollection.blank.edit.setting.blank.target.SetCommand;

public class HostSetCommand extends SetCommand<Host> {

  public HostSetCommand(CommandNode parent) {
    super(parent, ParameterKeys.HOST, "host");
  }

}
