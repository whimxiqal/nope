package me.pietelite.nope.sponge.command.tree.volumes;

import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.gui.volume.InteractiveVolume;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.util.EffectsUtil;
import me.pietelite.nope.sponge.util.Formatter;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class VolumeApplyCommand extends CommandNode {
  public VolumeApplyCommand(CommandNode parent) {
    super(parent, null, "Apply the zone you are currently editing to a scene", "apply");
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    if (!(context.cause().root() instanceof ServerPlayer)) {
      return CommandResult.error(Formatter.error("Only players may execute this command"));
    }
    ServerPlayer player = (ServerPlayer) context.cause().root();
    if (!Nope.instance().interactiveVolumeHandler().hasSession(player.uniqueId())) {
      context.sendMessage(Identity.nil(), Formatter.error("You do not have an active editing session"));
      return CommandResult.success();
    }
    InteractiveVolume<?> interactiveVolume = Nope.instance()
        .interactiveVolumeHandler()
        .finishSession(player.uniqueId());
    Scene scene = interactiveVolume.scene();
    if (scene.expired()) {
      context.sendMessage(Identity.nil(), Formatter.error("The scene you were editing has been removed"));
      return CommandResult.success();
    }
    Nope.instance().system().addVolume(interactiveVolume.volume(), scene);
    context.sendMessage(Identity.nil(), Formatter.success("You have added your zone to ___",
        scene.name()));
    SpongeNope.instance().particleEffectHandler().show(interactiveVolume.volume(), player);
    return CommandResult.success();
  }
}
