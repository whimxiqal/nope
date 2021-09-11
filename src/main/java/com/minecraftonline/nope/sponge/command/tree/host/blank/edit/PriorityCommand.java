package com.minecraftonline.nope.sponge.command.tree.host.blank.edit;

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

public class PriorityCommand extends CommandNode {
  public PriorityCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Edit the priority of a zone",
        "priority");
    addParameter(Parameters.PRIORITY);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    Host host = context.requireOne(ParameterKeys.HOST);
    int priority = context.requireOne(ParameterKeys.PRIORITY);

    if (!(host instanceof Zone)) {
      throw new CommandException(Formatter.error(
          "You may not change the priority on host ___", host.name()
      ));
    }

    ((Zone) host).setPriority(priority);
    return CommandResult.success();
  }
}
