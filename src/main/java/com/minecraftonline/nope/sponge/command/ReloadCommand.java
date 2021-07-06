package com.minecraftonline.nope.sponge.command;

import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.general.CommandNode;
import com.minecraftonline.nope.sponge.command.general.LambdaCommandNode;
import com.minecraftonline.nope.sponge.game.listener.DynamicSettingListeners;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.text.Text;

/**
 * The command to reload all config data from storage.
 */
public class ReloadCommand extends LambdaCommandNode {

  /**
   * Default constructor.
   *
   * @param parent parent node
   */
  public ReloadCommand(CommandNode parent) {
    super(parent, Permissions.COMMAND_RELOAD,
        Text.of("Reloads all config data from storage"),
        "reload", true);
    addAliases("load");

    setExecutor((src, args) -> {
      SpongeNope.getInstance().loadState();
      DynamicSettingListeners.register();
      src.sendMessage(Format.success("Successfully reloaded"));
      return CommandResult.success();
    });
  }
}
