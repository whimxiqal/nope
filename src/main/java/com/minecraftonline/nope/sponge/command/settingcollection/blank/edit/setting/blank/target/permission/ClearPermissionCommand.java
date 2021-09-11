package com.minecraftonline.nope.sponge.command.settingcollection.blank.edit.setting.blank.target.permission;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.Target;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.util.Formatter;
import java.util.Optional;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

public class ClearPermissionCommand<T extends SettingCollection> extends CommandNode {

  private final Parameter.Key<T> settingCollectionParameterKey;

  public ClearPermissionCommand(CommandNode parent,
                                Parameter.Key<T> settingCollectionParameterKey,
                                String collectionName) {
    super(parent, Permissions.EDIT,
        "Set a permission on the target of a " + collectionName,
        "clear", "reset");
    this.settingCollectionParameterKey = settingCollectionParameterKey;
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    T collection = context.requireOne(settingCollectionParameterKey);
    SettingKey<?> key = context.requireOne(ParameterKeys.SETTING_KEY);

    Optional<Target> target = collection.getTarget(key);
    if (target.isPresent()) {
      target.get().permissions().clear();
      context.cause().audience().sendMessage(Formatter.success(
          "All permissions cleared on key ___", key.id()
      ));
      return CommandResult.success();
    } else {
      return CommandResult.error(Formatter.error(
          "There is no target on key ___", key.id()
      ));
    }
  }

}
