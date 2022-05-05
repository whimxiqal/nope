package me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create;

import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.FunctionlessCommandNode;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create.builder.BuilderCuboidCommand;

public class VolumeBuilderCommand extends FunctionlessCommandNode {
  public VolumeBuilderCommand(CommandNode parent) {
    super(parent, null, "Build a new zone with the interactive builder tool", "builder");
    addChild(new BuilderCuboidCommand(this));
  }
}
