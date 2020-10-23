package com.minecraftonline.nope.command.region.targetset;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.target.PlayerTarget;
import com.minecraftonline.nope.control.target.TargetSet;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TargetSetRemovePlayerCommand extends AbstractTargetSetSubCommand {
  public TargetSetRemovePlayerCommand(CommandNode parent, String targetSetName, Setting<TargetSet> setting) {
    super(parent, "removeplayer", targetSetName, "remove player from ", setting);

    addCommandElements(GenericArguments.onlyOne(NopeArguments.playerOrPlayerUUID(Text.of("player"))));
  }

  @Override
  protected CompletableFuture<TargetSet> makeChanges(CommandSource source, CommandContext args, TargetSet targetSet) {
    CompletableFuture<TargetSet> result = new CompletableFuture<>();
    CompletableFuture<GameProfile> future = args.<CompletableFuture<GameProfile>>getOne(Text.of("player")).get();

    future.whenComplete((profile, throwable) -> {
      if (throwable instanceof ExecutionException) {
        // No profile
        source.sendMessage(Text.of(TextColors.RED, "Account for uuid does not exist!"));
        result.complete(targetSet);
        return;
      }

      if (!profile.getName().isPresent()) {
        source.sendMessage(Text.of(TextColors.RED, "Username for uuid: '" + profile.getUniqueId() + "' did not exist! Is it a real account?"));
        source.sendMessage(Text.of(TextColors.RED, "Making no changes."));
        result.complete(targetSet);
        return;
      }

      boolean removed = targetSet.remove(new PlayerTarget(profile.getUniqueId()));
      result.complete(targetSet);

      Text text = removed ? Text.of(TextColors.GREEN, "Successfully removed player: '" + profile.getName().get() + "'")
          : Text.of(TextColors.RED, "No player named '" + profile.getName().get() + "' to remove");

      source.sendMessage(text);
    });

    return result;
  }
}
