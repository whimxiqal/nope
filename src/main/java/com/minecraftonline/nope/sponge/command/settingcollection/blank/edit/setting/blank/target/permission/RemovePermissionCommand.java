package com.minecraftonline.nope.sponge.command.settingcollection.blank.edit.setting.blank.target.permission;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.Target;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.util.Formatter;
import java.util.Optional;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

public class RemovePermissionCommand<T extends SettingCollection> extends CommandNode {

  private final Parameter.Key<T> settingCollectionParameterKey;

  public RemovePermissionCommand(CommandNode parent,
                                 Parameter.Key<T> settingCollectionParameterKey,
                                 String collectionName) {
    super(parent, Permissions.EDIT,
        "Set a permission on the target of a " + collectionName,
        "remove", "unset");
    this.settingCollectionParameterKey = settingCollectionParameterKey;
    addParameter(Parameters.PERMISSION);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    T collection = context.requireOne(settingCollectionParameterKey);
    SettingKey<?, ?> key = context.requireOne(ParameterKeys.SETTING_KEY);
    String permission = context.requireOne(ParameterKeys.PERMISSION);

    Optional<Target> targetOptional = collection.getTarget(key);
    Target target;
    if (targetOptional.isPresent()) {
      target = targetOptional.get();
    } else {
      return CommandResult.error(Formatter.error(
          "There is no target on key ___", key.id()
      ));
    }
    Boolean prev = target.permissions().remove(permission);
    if (prev != null) {
      context.cause().audience().sendMessage(Formatter.success(
          "Removed permission ___ = ___ set on key ___",
          permission, key.id()
      ));
    } else {
      return CommandResult.error(Formatter.success(
          "Permission ___ does not exist on key ___",
          permission, key.id()
      ));
    }
    collection.save();
    return CommandResult.success();
  }

}
