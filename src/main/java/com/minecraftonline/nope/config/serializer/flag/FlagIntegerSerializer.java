package com.minecraftonline.nope.config.serializer.flag;

import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.control.flags.FlagInteger;
import com.minecraftonline.nope.control.flags.FlagUtil;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FlagIntegerSerializer implements TypeSerializer<FlagInteger> {
  @Nullable
  @Override
  public FlagInteger deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
    if (value.isVirtual()) {
      return null;
    }
    int i = value.getInt();
    FlagInteger flagInteger = new FlagInteger(i);
    FlagUtil.deserializeGroup(flagInteger, value);
    return flagInteger;
  }

  @Override
  public void serialize(@NonNull TypeToken<?> type, @Nullable FlagInteger obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
    FlagUtil.serialize(obj, value);
  }
}
