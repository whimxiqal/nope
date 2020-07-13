package com.minecraftonline.nope.config.serializer.flag;

import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.control.flags.FlagDouble;
import com.minecraftonline.nope.control.flags.FlagUtil;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FlagDoubleSerializer implements TypeSerializer<FlagDouble> {
  @Nullable
  @Override
  public FlagDouble deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
    if (value.isVirtual()) {
      return null;
    }
    double d = value.getDouble();
    FlagDouble flagDouble = new FlagDouble(d);
    FlagUtil.deserializeGroup(flagDouble, value);
    return flagDouble;
  }

  @Override
  public void serialize(@NonNull TypeToken<?> type, @Nullable FlagDouble obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
    FlagUtil.serialize(obj, value);
  }
}
