package me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create;

import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.FunctionlessCommandNode;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create.builder.BuilderCuboidCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create.explicit.ExplicitCuboidCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create.explicit.ExplicitCylinderCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create.explicit.ExplicitSlabCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create.explicit.ExplicitSphereCommand;

public class VolumeExplicitCommand extends FunctionlessCommandNode {
  public VolumeExplicitCommand(CommandNode parent) {
    super(parent, null, "Explicitly define a new volume", "explicit");
    addChild(new ExplicitCuboidCommand(this));
    addChild(new ExplicitCylinderCommand(this));
    addChild(new ExplicitSlabCommand(this));
    addChild(new ExplicitSphereCommand(this));
  }
}
