package com.minecraftonline.nope.config.serializer;

import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.control.flags.FlagGameMode;
import com.minecraftonline.nope.control.flags.FlagUtil;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.util.TypeTokens;

public class FlagGameModeSerializer implements TypeSerializer<FlagGameMode> {
  @Nullable
  @Override
  public FlagGameMode deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
    GameMode gameMode = value.getValue(TypeTokens.GAME_MODE_TOKEN);
    if (gameMode == null) {
      throw new ObjectMappingException("Cannot deserialize a null GameMode");
    }
    FlagGameMode flagGameMode = new FlagGameMode(gameMode);
    FlagUtil.deserializeGroup(flagGameMode, value);
    return flagGameMode;
  }

  @Override
  public void serialize(@NonNull TypeToken<?> type, @Nullable FlagGameMode obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
    FlagUtil.serialize(obj, value);
  }
}
