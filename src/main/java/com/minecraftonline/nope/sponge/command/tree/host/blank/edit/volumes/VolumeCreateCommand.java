package com.minecraftonline.nope.sponge.command.tree.host.blank.edit.volumes;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.FunctionlessCommandNode;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.volumes.create.CuboidCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.volumes.create.CylinderCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.volumes.create.SlabCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.volumes.create.SphereCommand;

public class VolumeCreateCommand extends FunctionlessCommandNode {
  public VolumeCreateCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Create a new volume on this host",
        "create");
    addChild(new CuboidCommand(this));
    addChild(new CylinderCommand(this));
    addChild(new SlabCommand(this));
    addChild(new SphereCommand(this));
  }

}
