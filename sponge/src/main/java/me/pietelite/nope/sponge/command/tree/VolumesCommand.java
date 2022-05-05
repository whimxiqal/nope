package me.pietelite.nope.sponge.command.tree;

import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.FunctionlessCommandNode;
import me.pietelite.nope.sponge.command.tree.volumes.VolumeApplyCommand;
import me.pietelite.nope.sponge.command.tree.volumes.VolumeStopCommand;

public class VolumesCommand extends FunctionlessCommandNode {
  public VolumesCommand(CommandNode parent) {
    super(parent, Permissions.HOST_EDIT, "Use the interactive zone editor", "zones");
    addChild(new VolumeApplyCommand(this));
    addChild(new VolumeStopCommand(this));
  }
}
