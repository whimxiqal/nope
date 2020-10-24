package com.minecraftonline.nope.command.setting;

import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.FunctionlessCommandNode;
import com.minecraftonline.nope.permission.Permission;
import org.spongepowered.api.text.Text;

public class SettingCommand extends FunctionlessCommandNode {
  public SettingCommand(CommandNode parent) {
    super(parent, Permission.of("nope.setting"), Text.of("Allows the user to change nope settings"), "setting");

    addChildren(new ListSettingsCommand(this));
  }
}
