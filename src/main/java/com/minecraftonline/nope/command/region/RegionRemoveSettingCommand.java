package com.minecraftonline.nope.command.region;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.permission.Permission;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingValue;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

public class RegionRemoveSettingCommand extends LambdaCommandNode {
  public RegionRemoveSettingCommand(CommandNode parent) {
    super(parent, Permission.of("nope.region.edit.flag"), Text.of("Allows the user to unset flags on a region"), "removeflag");

    addCommandElements(
        GenericArguments.onlyOne(NopeArguments.host(Text.of("region"))),
        GenericArguments.onlyOne(NopeArguments.settingKey(Text.of("setting")))
    );

    setExecutor((src, args) -> {
      Host host = args.requireOne(Text.of("region"));
      SettingKey<?> settingKey = args.requireOne(Text.of("setting"));

      SettingValue<?> settingValue = host.remove(settingKey);

      if (settingValue == null) {
        src.sendMessage(Format.error(settingKey.getId() + " is not assigned on this host!"));
        return CommandResult.empty();
      }
      Nope.getInstance().getHostTree().save();
      src.sendMessage(Format.success("Unset " + settingKey.getId() + " on this host"));

      return CommandResult.empty();
    });
  }
}
