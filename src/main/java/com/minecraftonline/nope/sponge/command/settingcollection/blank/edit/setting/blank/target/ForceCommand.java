package com.minecraftonline.nope.sponge.command.settingcollection.blank.edit.setting.blank.target;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.Target;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.util.Formatter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

public class ForceCommand<T extends SettingCollection> extends CommandNode {

  private final Parameter.Key<T> settingCollectionParameterKey;

  public ForceCommand(CommandNode parent,
                      Parameter.Key<T> settingCollectionParameterKey,
                      String collectionName) {
    super(parent, Permissions.EDIT,
        "Ignore the effect of the unrestricted permission for a given setting on " + collectionName,
        "force");
    this.settingCollectionParameterKey = settingCollectionParameterKey;
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    T collection = context.requireOne(settingCollectionParameterKey);
    SettingKey<?, ?, ?> key = context.requireOne(ParameterKeys.SETTING_KEY);
    if (!key.playerRestrictive()) {
      return CommandResult.error(Formatter.error(
          "You may not force a setting with key ___ because it is not player-restrictive in nature",
          key.id()
      ));
    }
    Target target = collection.computeTarget(key, Target::all);
    target.setIndiscriminate(!target.isIndiscriminate());
    context.cause().audience().sendMessage(Formatter.success(
        "The setting with key ___ now ___ players with the ___ permission",
        key.id(),
        target.isIndiscriminate() ? "affects" : "does not affect",
        Permissions.UNRESTRICTED.get()
    ));
    return CommandResult.success();
  }

}
