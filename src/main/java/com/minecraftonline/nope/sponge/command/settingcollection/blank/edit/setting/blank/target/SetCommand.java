package com.minecraftonline.nope.sponge.command.settingcollection.blank.edit.setting.blank.target;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.Target;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.command.parameters.TargetOption;
import com.minecraftonline.nope.sponge.util.Formatter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

public class SetCommand<T extends SettingCollection> extends CommandNode {

  private final Parameter.Key<T> settingCollectionParameterKey;

  public SetCommand(CommandNode parent,
                    Parameter.Key<T> settingCollectionParameterKey,
                    String collectionName) {
    super(parent, Permissions.EDIT,
        "Set a permission on the target of a " + collectionName,
        "set");
    this.settingCollectionParameterKey = settingCollectionParameterKey;
    addParameter(Parameters.TARGET_OPTION);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    T collection = context.requireOne(settingCollectionParameterKey);
    SettingKey<?, ?, ?> key = context.requireOne(ParameterKeys.SETTING_KEY);
    TargetOption option = context.requireOne(ParameterKeys.TARGET_OPTION);

    switch (option) {
      case ALL:
        collection.setTarget(key, Target.all());
        context.cause().audience().sendMessage(Formatter.success(
            "Setting ___ now targets all users", key.id()
        ));
        break;
      case NONE:
        collection.setTarget(key, Target.none());
        context.cause().audience().sendMessage(Formatter.success(
            "Setting ___ now targets no one", key.id()
        ));
        break;
      case EMPTY:
      default:
        collection.removeTarget(key);
        context.cause().audience().sendMessage(Formatter.success(
            "Target removed from setting ___", key.id()
        ));
    }
    return CommandResult.success();
  }

}
