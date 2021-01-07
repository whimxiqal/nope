package com.minecraftonline.nope.command.region;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class RegionSetPriorityCommand extends LambdaCommandNode {

  RegionSetPriorityCommand(CommandNode parent) {
    super(parent,
        Permissions.EDIT_REGION,
        Text.of("Allows the user to set the priority of a region"),
        "setpriority");

    addCommandElements(
        GenericArguments.flags()
            .valueFlag(NopeArguments.host(Text.of("region")), "r", "-region")
            .buildWith(GenericArguments.none()),
        GenericArguments.integer(Text.of("priority")));
    setExecutor((src, args) -> {
      int priority = args.requireOne("priority");

      Optional<Host> hostOptional = args.getOne("region");
      Host host;
      if (!hostOptional.isPresent()) {
        if (!(src instanceof Player)) {
          src.sendMessage(Format.error("Can't infer region! "
                  + "Please specify the target region."));
          return CommandResult.empty();
        }
        Player player = (Player) src;
        List<Host> containing = Nope.getInstance()
                .getHostTree()
                .getContainingHosts(player.getLocation());
        if (containing.isEmpty()) {
          src.sendMessage(Format.error("Can't infer region! "
                  + "Please specify the target region."));
          return CommandResult.empty();
        }
        host = containing.stream().max(Comparator.comparing(Host::getPriority)).get();
      } else {
        host = hostOptional.get();
      }

      try {
        host.setPriority(priority);
      } catch (UnsupportedOperationException e) {
        src.sendMessage(Format.error("This host does not support having its priority set!"));
        return CommandResult.empty();
      } catch (IllegalArgumentException e) {
        src.sendMessage(Format.error(e.getMessage()));
        return CommandResult.empty();
      }

      Nope.getInstance().getHostTree().save();
      src.sendMessage(Format.success("Set priority of host ",
          Format.host(host),
          " to ",
          Format.note(priority)));

      return CommandResult.success();
    });
  }
}
