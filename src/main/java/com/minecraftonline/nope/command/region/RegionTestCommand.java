package com.minecraftonline.nope.command.region;

import com.minecraftonline.nope.SettingLibrary;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.control.flags.FlagState;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.permission.Permissions;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.text.Text;

public class RegionTestCommand extends LambdaCommandNode {
    public RegionTestCommand(CommandNode parent) {
        super(parent, Permissions.EDIT_REGION, Text.of("Do a test operation"), "test");

        addCommandElements(NopeArguments.regionWrapper(Text.of("host")));
        setExecutor((src, args) -> {
            Host host = args.<Host>getOne("host").get();
            host.put(SettingLibrary.FLAG_BUILD, new FlagState(false));
            return CommandResult.success();
        });
    }
}
