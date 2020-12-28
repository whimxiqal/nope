package com.minecraftonline.nope.command.region;

import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.flags.Flag;
import com.minecraftonline.nope.permission.Permission;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class RegionRemoveFlagCommand extends LambdaCommandNode {
  public RegionRemoveFlagCommand(CommandNode parent) {
    super(parent, Permission.of("nope.region.edit.flag"), Text.of("Allows the user to unset flags on a region"), "removeflag");

    addCommandElements(GenericArguments.onlyOne(NopeArguments.regionWrapper(Text.of("region"))), GenericArguments.onlyOne(GenericArguments.string(Text.of("flag"))));

    setExecutor((src, args) -> {
      src.sendMessage(Format.error("Command not implemented yet!"));
      return CommandResult.empty();
      /*HostWrapper wrapper = args.<HostWrapper>getOne("region").get();
      String flag = args.<String>getOne("flag").get();

      Optional<com.minecraftonline.nope.setting.Setting<?>> optFlag = Settings.REGISTRY_MODULE.getById(flag)
          .filter(setting -> setting.isApplicable(com.minecraftonline.nope.setting.Setting.Applicability.REGION))
          .filter(setting -> setting.getDefaultValue() instanceof Flag); // Only flags

      if (!optFlag.isPresent()) {
        src.sendMessage(Format.error("Unknown flag: '" + flag + '"'));
      }
      wrapper.getRegion().unset(optFlag.get());
      src.sendMessage(Format.info("Unset flag '" + flag + "' on region '" + wrapper.getRegionName() + "'"));
      return CommandResult.success();*/
    });
  }
}
