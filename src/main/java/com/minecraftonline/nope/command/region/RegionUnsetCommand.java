package com.minecraftonline.nope.command.region;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingValue;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class RegionUnsetCommand extends LambdaCommandNode {

  RegionUnsetCommand(CommandNode parent) {
    super(parent,
        Permissions.EDIT_REGION,
        Text.of("Unset settings on a region"),
        "unset");

    addCommandElements(
        GenericArguments.flags()
            .valueFlag(NopeArguments.host(Text.of("region")), "r", "-region")
            .buildWith(GenericArguments.none()),
        GenericArguments.onlyOne(NopeArguments.settingKey(Text.of("setting"))));

    setExecutor((src, args) -> {
      SettingKey<?> settingKey = args.requireOne(Text.of("setting"));

      Host host = args.<Host>getOne("region").orElse(RegionCommand.inferHost(src).orElse(null));
      if (host == null) {
        return CommandResult.empty();
      }

      SettingValue<?> settingValue = host.remove(settingKey);

      if (settingValue == null) {
        src.sendMessage(Format.error(Format.settingKey(settingKey, false),
            " is not assigned on this host!"));
        return CommandResult.empty();
      }
      Nope.getInstance().getHostTree().save();
      src.sendMessage(Format.success("Unset ",
          Format.settingKey(settingKey, false),
          " on region ",
          Format.host(host)));

      return CommandResult.empty();
    });
  }
}
