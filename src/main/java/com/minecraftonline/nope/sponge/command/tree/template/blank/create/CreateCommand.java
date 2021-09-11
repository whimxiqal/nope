package com.minecraftonline.nope.sponge.command.tree.template.blank.create;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.template.Template;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.util.Formatter;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

public class CreateCommand extends CommandNode {

  public CreateCommand(CommandNode parent) {
    super(parent,
        Permissions.EDIT,
        "Create a template of settings",
        "create", "c");
    prefix(Parameters.TEMPLATE);
    addParameter(Parameters.NAME);
    addParameter(Parameters.DESCRIPTION);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    String name = context.requireOne(ParameterKeys.NAME);
    String description = context.requireOne(ParameterKeys.DESCRIPTION);
    Template template = new Template(name, description);
    context.cause()
        .audience()
        .sendMessage(Formatter.success("Created template ",
            Component.text(template.name())));
    return CommandResult.success();
  }

}
