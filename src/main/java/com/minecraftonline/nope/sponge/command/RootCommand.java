package com.minecraftonline.nope.sponge.command;

import com.minecraftonline.nope.sponge.command.tree.EvaluateCommand;
import com.minecraftonline.nope.sponge.command.tree.HostCommand;
import com.minecraftonline.nope.sponge.command.tree.HostsCommand;
import com.minecraftonline.nope.sponge.command.tree.ReloadCommand;
import com.minecraftonline.nope.sponge.command.tree.SettingsCommand;
import com.minecraftonline.nope.sponge.command.tree.TemplateCommand;
import com.minecraftonline.nope.sponge.command.tree.ToolCommand;
import com.minecraftonline.nope.sponge.util.Formatter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

public class RootCommand extends CommandNode {

  public RootCommand() {
    super(null, null,
        "Root for all Nope commands", "nope");
    addChild(new EvaluateCommand(this));
    addChild(new HostCommand(this));
    addChild(new HostsCommand(this));
    addChild(new ReloadCommand(this));
    addChild(new SettingsCommand(this));
    addChild(new TemplateCommand(this));
    addChild(new ToolCommand(this));
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    context.cause().audience().sendMessage(Formatter.success(
        "Nope command!"
    ));
    return CommandResult.success();
  }
}
