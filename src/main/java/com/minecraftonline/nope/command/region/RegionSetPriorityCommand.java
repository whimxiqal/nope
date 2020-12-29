package com.minecraftonline.nope.command.region;

import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.permission.Permission;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

public class RegionSetPriorityCommand extends LambdaCommandNode {

  public RegionSetPriorityCommand(CommandNode parent) {
    super(parent, Permission.of("nope.region.edit.priority"), Text.of("Allows the user to set the priority of a region"), "setpriority");

    addCommandElements(
        GenericArguments.onlyOne(NopeArguments.host(Text.of("host"))),
        GenericArguments.integer(Text.of("priority"))
    );
    setExecutor((src, args) -> {
      Host host = args.requireOne("host");
      int priority = args.requireOne("priority");

      try {
        host.setPriority(priority);
      } catch (UnsupportedOperationException e) {
        src.sendMessage(Format.error("This host does not support having its priority set!"));
        return CommandResult.empty();
      } catch (IllegalArgumentException e) {
        src.sendMessage(Format.error(e.getMessage()));
        return CommandResult.empty();
      }

      src.sendMessage(Format.success("Set priority of host " + host.getName() + ", to " + priority));

      return CommandResult.success();
    });
  }
}
