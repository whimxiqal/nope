package com.minecraftonline.nope.config.serializer;

import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.control.flags.FlagString;
import com.minecraftonline.nope.control.flags.FlagUtil;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FlagStringSerializer implements TypeSerializer<FlagString> {

  @Nullable
  @Override
  public FlagString deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
    String string = value.getString();
    if (string == null) {
      throw new ObjectMappingException("Cannot deserialize a null string for a flag");
    }
    FlagString flagString = new FlagString(string);
    FlagUtil.deserializeGroup(flagString, value);
    return flagString;
  }

  @Override
  public void serialize(@NonNull TypeToken<?> type, @Nullable FlagString obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
    FlagUtil.serialize(obj, value);
  }
}
