//package com.minecraftonline.nope.command.region;
//
//import com.minecraftonline.nope.setting.SettingLibrary;
//import com.minecraftonline.nope.arguments.NopeArguments;
//import com.minecraftonline.nope.command.common.CommandNode;
//import com.minecraftonline.nope.command.common.LambdaCommandNode;
//import com.minecraftonline.nope.host.Host;
//import com.minecraftonline.nope.permission.Permissions;
//import com.minecraftonline.nope.setting.SettingValue;
//import org.spongepowered.api.command.CommandResult;
//import org.spongepowered.api.text.Text;
//
//public class RegionTestCommand extends LambdaCommandNode {
//    public RegionTestCommand(CommandNode parent) {
//        super(parent, Permissions.EDIT_REGION, Text.of("Do a test operation"), "test");
//
//        addCommandElements(NopeArguments.host(Text.of("host")));
//        setExecutor((src, args) -> {
//            Host host = args.<Host>getOne("host").get();
//            host.put(SettingLibrary.FLAG_BUILD, SettingValue.of(false));
//            return CommandResult.success();
//        });
//    }
//}
