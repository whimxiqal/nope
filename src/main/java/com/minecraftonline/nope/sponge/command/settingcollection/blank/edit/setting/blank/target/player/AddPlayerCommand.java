package com.minecraftonline.nope.sponge.command.settingcollection.blank.edit.setting.blank.target.player;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.Target;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.util.Formatter;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;

public class AddPlayerCommand<T extends SettingCollection> extends CommandNode {

  private final Parameter.Key<T> settingCollectionParameterKey;

  public AddPlayerCommand(CommandNode parent,
                          Parameter.Key<T> settingCollectionParameterKey,
                          String collectionName) {
    super(parent, Permissions.EDIT,
        "Add a player on the target of a " + collectionName,
        "add");
    this.settingCollectionParameterKey = settingCollectionParameterKey;
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    T collection = context.requireOne(settingCollectionParameterKey);
    SettingKey<?, ?, ?> key = context.requireOne(ParameterKeys.SETTING_KEY);
    Collection<CompletableFuture<GameProfile>> players = context.requireOne(ParameterKeys.PLAYER_LIST);

    Target target = collection.computeTarget(key, Target::none);
    String listType = target.isWhitelist() ? "whitelist" : "blacklist";
    Sponge.asyncScheduler().submit(Task.builder().execute(() -> {
      GameProfile profile;
      for (CompletableFuture<GameProfile> player : players) {
        try {
          profile = player.get();
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
          continue;
        }
        if (target.users().add(profile.uuid())) {
          context.cause().audience().sendMessage(Formatter.success(
              "Added user ___ to the ___ on ___ ",
              profile.name().orElse(profile.uuid().toString()), listType, key.id()
          ));
        } else {
          context.cause().audience().sendMessage(Formatter.warn(
              "Could not add user ___ to the ___ on ___ ",
              profile.name().orElse(profile.uuid().toString()), listType, key.id()
          ));
        }
      }
    })
        .plugin(SpongeNope.instance().pluginContainer())
        .build());
    return CommandResult.success();
  }
}