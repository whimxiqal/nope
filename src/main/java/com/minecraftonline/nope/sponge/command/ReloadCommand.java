package com.minecraftonline.nope.sponge.command;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.general.CommandNode;
import com.minecraftonline.nope.sponge.listener.DynamicSettingListeners;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

/**
 * The command to reload all config data from storage.
 */
public class ReloadCommand extends CommandNode {

  /**
   * Default constructor.
   *
   * @param parent parent node
   */
  public ReloadCommand(CommandNode parent) {
    super(parent, Permissions.COMMAND_RELOAD,
        "Reloads all config data from storage",
        "reload", true);
    addAliases("load");

  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    SpongeNope.instance().loadState();
    DynamicSettingListeners.register();
    context.cause().audience().sendMessage(formatter().success("Successfully reloaded"));
    return CommandResult.success();
  }
}
