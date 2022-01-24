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

public class AddPermissionCommand<T extends SettingCollection> extends CommandNode {

  private final Parameter.Key<T> settingCollectionParameterKey;

  public AddPermissionCommand(CommandNode parent,
                              Parameter.Key<T> settingCollectionParameterKey,
                              String collectionName) {
    super(parent, Permissions.EDIT,
        "Set a permission on the target of a " + collectionName,
        "add", "set");
    this.settingCollectionParameterKey = settingCollectionParameterKey;
    addParameter(Parameters.PERMISSION);
    addParameter(Parameters.PERMISSION_VALUE);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    T collection = context.requireOne(settingCollectionParameterKey);
    SettingKey<?, ?, ?> key = context.requireOne(ParameterKeys.SETTING_KEY);
    String permission = context.requireOne(ParameterKeys.PERMISSION);
    boolean value = context.requireOne(ParameterKeys.PERMISSION_VALUE);

    Optional<Target> targetOptional = collection.getTarget(key);
    Target target;
    if (targetOptional.isPresent()) {
      target = targetOptional.get();
    } else {
      target = Target.all();
      collection.setTarget(key, target);
    }
    Boolean prev = target.permissions().put(permission, value);
    if (prev != null) {
      if (prev == value) {
        return CommandResult.error(Formatter.error(
            "Permission ___ already set to ___ on key ___",
            permission, value, key.id()
        ));
      }
      context.cause().audience().sendMessage(Formatter.success(
          "Removed permission ___ = ___ set on key ___",
          permission, prev, key.id()
      ));
    }
    context.cause().audience().sendMessage(Formatter.success(
        "Added permission ___ = ___ set on key ___",
        permission, value, key.id()
    ));
    collection.save();
    return CommandResult.success();
  }

}
