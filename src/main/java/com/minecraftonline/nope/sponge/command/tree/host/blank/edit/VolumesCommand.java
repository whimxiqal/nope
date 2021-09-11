package com.minecraftonline.nope.sponge.command.tree.host.blank.edit;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.FunctionlessCommandNode;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.volumes.VolumeCreateCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.edit.volumes.VolumeDestroyCommand;

public class VolumesCommand extends FunctionlessCommandNode {
  public VolumesCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Alter the volumes, which give dimensions to zones",
        "volumes");
    addChild(new VolumeCreateCommand(this));
    addChild(new VolumeDestroyCommand(this));
  }

}
