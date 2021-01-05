package com.minecraftonline.nope.command.setting;

import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.FunctionlessCommandNode;
import com.minecraftonline.nope.permission.Permission;
import com.minecraftonline.nope.permission.Permissions;
import org.spongepowered.api.text.Text;

public class SettingCommand extends FunctionlessCommandNode {

  /**
   * Default constructor.
   *
   * @param parent the parent node
   */
  public SettingCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_ROOT,
        Text.of("Configure Nope settings"),
        "setting");
    addChildren(new ListSettingsCommand(this));
  }

}
