package com.minecraftonline.nope.sponge.command.tree.host.blank.edit.volumes;

import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.host.Zone;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.util.Formatter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

public class VolumeDestroyCommand extends CommandNode {
  public VolumeDestroyCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Destroy a volume by index",
        "destroy");
    addParameter(Parameters.INDEX);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    int index = context.one(ParameterKeys.INDEX).orElseThrow(() -> new CommandException(Formatter.error(
        "You must supply a volume index"
    )));
    Host host = context.requireOne(ParameterKeys.HOST);
    if (!(host instanceof Zone)) {
      return CommandResult.error(Formatter.error(
          "You may not destroy volumes in host ___", host.name()
      ));
    }
    Zone zone = (Zone) host;
    try {
      zone.remove(index);
    } catch (IndexOutOfBoundsException e) {
      return CommandResult.error(Formatter.error(
          "Your index ___ is out of bounds", index
      ));
    }
    context.cause().audience().sendMessage(Formatter.success(
        "You deleted the volume of ___ at index ___",
        zone.name(), index
    ));
    return CommandResult.success();
  }
}
