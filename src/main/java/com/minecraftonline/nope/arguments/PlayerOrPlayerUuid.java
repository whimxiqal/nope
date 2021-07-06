package com.minecraftonline.nope.arguments;

import com.minecraftonline.nope.Nope;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.api.util.annotation.NonnullByDefault;

/**
 * A {@link CommandElement} which finds a player or a player uuid.
 */
@NonnullByDefault
public class PlayerOrPlayerUuid extends CommandElement {
  protected PlayerOrPlayerUuid(@Nullable Text key) {
    super(key);
  }

  @Nullable
  @Override
  protected CompletableFuture<GameProfile> parseValue(CommandSource source, CommandArgs args)
      throws ArgumentParseException {
    String arg = args.next();
    CompletableFuture<GameProfile> future = new CompletableFuture<>();
    UUID uuid = null;
    try {
      uuid = UUID.fromString(arg);
    } catch (IllegalArgumentException ignored) {
      // Invalid uuid, try for player name
    }

    if (uuid == null) {
      uuid = Sponge.getServer().getPlayer(arg)
          .map(Identifiable::getUniqueId)
          .orElseThrow(() -> new ArgumentParseException(Text.of(
              "Player not online or uuid invalid!"), arg, 0));
    }


    Sponge.getServer().getGameProfileManager().get(uuid).whenComplete((profile, throwable) -> {
      if (throwable != null) {
        if (throwable instanceof ExecutionException) {
          future.completeExceptionally(throwable);
        } else {
          Nope.getInstance().getLogger().error("Error while waiting for game profile", throwable);
        }
        return;
      }
      future.complete(profile);
    });

    return future;
  }

  @Override
  public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
    String start = args.nextIfPresent().orElse("");

    return Sponge.getServer().getOnlinePlayers().stream()
        .map(Player::getName)
        .filter(s -> s.startsWith(start))
        .collect(Collectors.toList());
  }
}
