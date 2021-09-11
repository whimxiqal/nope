package com.minecraftonline.nope.sponge.command.tree;

import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.FunctionlessCommandNode;
import com.minecraftonline.nope.sponge.command.tree.host.blank.HostDestroyCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.HostEditCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.HostInfoCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.HostShowCommand;

public class HostCommand extends FunctionlessCommandNode {
  public HostCommand(CommandNode parent) {
    super(parent,
        null,
        "Create, delete, and edit hosts",
        "host");
    addChild(new HostDestroyCommand(this));
    addChild(new HostEditCommand(this));
    addChild(new HostInfoCommand(this));
    addChild(new HostShowCommand(this));
  }
}
