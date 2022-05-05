package me.pietelite.nope.sponge.command.tree.host.blank.edit;

import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.permission.Permission;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.util.Formatter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

public abstract class EditSceneCommandNode extends CommandNode {
  public EditSceneCommandNode(CommandNode parent, Permission permission,
                              @NotNull String description, @NotNull String primaryAlias,
                              @NotNull String... otherAliases) {
    super(parent, permission, description, primaryAlias, otherAliases);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    Host host = context.requireOne(ParameterKeys.HOST);
    if (!(host instanceof Scene)) {
      return CommandResult.error(Formatter.error(
          "You may not create zones for host ___", host.name()
      ));
    }
    return execute(context, (Scene) host);
  }

  public abstract CommandResult execute(CommandContext context, Scene scene) throws CommandException;
}
