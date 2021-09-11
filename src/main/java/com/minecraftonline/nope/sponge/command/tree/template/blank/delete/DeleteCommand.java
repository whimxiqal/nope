package com.minecraftonline.nope.sponge.command.tree.template.blank.delete;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.template.Template;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

public class DeleteCommand extends CommandNode {
  public DeleteCommand(CommandNode parent) {
    super(parent,
        Permissions.EDIT,
        "Delete a template",
        "delete", "d");
    prefix(Parameters.TEMPLATE);
    addParameter(Parameters.TEMPLATE);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    Template template = context.requireOne(ParameterKeys.TEMPLATE);
    Nope.instance().templateSet().remove(template.name());
    return CommandResult.success();
  }
}
