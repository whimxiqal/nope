package me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create;

import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.FunctionlessCommandNode;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create.selection.SelectionCuboidCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create.selection.SelectionCylinderCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create.selection.SelectionSlabCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create.selection.SelectionSphereCommand;

public class VolumeSelectionCommand extends FunctionlessCommandNode {
  public VolumeSelectionCommand(CommandNode parent) {
    super(parent, null, "Explicitly define a new volume", "selection");
    addChild(new SelectionCuboidCommand(this));
    addChild(new SelectionCylinderCommand(this));
    addChild(new SelectionSlabCommand(this));
    addChild(new SelectionSphereCommand(this));
  }
}
