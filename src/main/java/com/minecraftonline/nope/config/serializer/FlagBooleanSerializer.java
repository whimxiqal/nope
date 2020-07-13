package com.minecraftonline.nope.config.serializer;

import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.control.flags.FlagBoolean;
import com.minecraftonline.nope.control.flags.FlagUtil;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FlagBooleanSerializer implements TypeSerializer<FlagBoolean> {
  @Nullable
  @Override
  public FlagBoolean deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
    if (value.isVirtual()) {
      return null;
    }
    FlagBoolean flagBoolean = new FlagBoolean(value.getBoolean());
    FlagUtil.deserializeGroup(flagBoolean, value);
    return flagBoolean;
  }

  @Override
  public void serialize(@NonNull TypeToken<?> type, @Nullable FlagBoolean obj, @NonNull ConfigurationNode value) throws ObjectMappingException {

  }
}
