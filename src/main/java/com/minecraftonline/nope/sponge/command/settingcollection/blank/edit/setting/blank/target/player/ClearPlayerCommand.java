package com.minecraftonline.nope.sponge.command.settingcollection.blank.edit.setting.blank.target.player;

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

public class ClearPlayerCommand<T extends SettingCollection> extends CommandNode {

  private final Parameter.Key<T> settingCollectionParameterKey;

  public ClearPlayerCommand(CommandNode parent,
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
    SettingKey<?, ?> key = context.requireOne(ParameterKeys.SETTING_KEY);

    Optional<Target> target = collection.getTarget(key);
    if (target.isPresent()) {
      if (target.get().users().isEmpty()) {
        context.cause().audience().sendMessage(Formatter.warn(
            "There are no players on this target"
        ));
      } else {
        target.get().users().clear();
        context.cause().audience().sendMessage(Formatter.success(
            "All players were cleared from the target on key ___", key.id()
        ));
        if (target.get().isWhitelist()) {
          target.get().blacklist();
          context.cause().audience().sendMessage(Formatter.success(
              "The target type was set to ___", "blacklist"
          ));
        }
      }
    }
    return CommandResult.success();
  }
}
