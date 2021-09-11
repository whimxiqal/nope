package com.minecraftonline.nope.sponge.command.tree.host.blank.edit.setting.blank.target.player;

import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.settingcollection.blank.edit.setting.blank.target.player.AddPlayerCommand;

public class HostAddPlayerCommand extends AddPlayerCommand<Host> {
  public HostAddPlayerCommand(CommandNode parent) {
    super(parent, ParameterKeys.HOST, "host");
  }
}
