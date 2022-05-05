package me.pietelite.nope.sponge.command.tree.volumes;

import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.util.Formatter;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class VolumeStopCommand extends CommandNode {
  public VolumeStopCommand(CommandNode parent) {
    super(parent, null, "Stop a current session editing a zone", "stop");
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    if (!(context.cause().root() instanceof ServerPlayer)) {
      return CommandResult.error(Formatter.error("Only players may execute this command"));
    }
    ServerPlayer player = (ServerPlayer) context.cause().root();
    if (!Nope.instance().interactiveVolumeHandler().hasSession(player.uniqueId())) {
      context.sendMessage(Identity.nil(), Formatter.error("You do not have an active zone editor session"));
      return CommandResult.success();
    }
    Nope.instance().interactiveVolumeHandler().finishSession(player.uniqueId());
    context.sendMessage(Identity.nil(), Formatter.success("Stopped your zone editor session"));
    return CommandResult.success();
  }
}
