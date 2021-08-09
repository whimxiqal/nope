package com.minecraftonline.nope.sponge.command.general;

import com.minecraftonline.nope.common.permission.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public abstract class PlayerOnlyCommandNode extends CommandNode {
  public PlayerOnlyCommandNode(CommandNode parent,
                               Permission permission,
                               @NotNull String description,
                               @NotNull String primaryAlias,
                               @NotNull String... otherAliases) {
    super(parent, permission, description, primaryAlias, otherAliases);
  }

  public PlayerOnlyCommandNode(@Nullable CommandNode parent,
                               @Nullable Permission permission,
                               @NotNull String description,
                               @NotNull String primaryAlias,
                               boolean addHelp) {
    super(parent, permission, description, primaryAlias, addHelp);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    if (context.cause().root() instanceof Player) {
      return execute(context, (Player) context.cause().root());
    } else {
      return CommandResult.error(formatter().error("Only players may execute this command."));
    }
  }

  abstract public CommandResult execute(CommandContext context, Player cause) throws CommandException;
}
