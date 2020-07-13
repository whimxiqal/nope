package com.minecraftonline.nope.config.serializer.flag;

import com.minecraftonline.nope.control.flags.FlagState;
import com.minecraftonline.nope.control.flags.FlagUtil;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FlagStateSerializer implements TypeSerializer<FlagState> {
  @Nullable
  @Override
  public FlagState deserialize(com.google.common.reflect.@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
    String s = value.getString();
    if (s == null) {
      return null;
    }
    boolean bool;
    if (s.equals("allow")) {
      bool = true;
    }
    else if (s.equals("deny")) {
      bool = false;
    }
    else {
      return null;
    }
    FlagState flagState = new FlagState(bool);
    FlagUtil.deserializeGroup(flagState, value);
    return flagState;
  }

  @Override
  public void serialize(com.google.common.reflect.@NonNull TypeToken<?> type, @Nullable FlagState obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
    if (obj == null) {
      return;
    }
    value.setValue(obj.getValue() ? "allow" : "deny");
    FlagUtil.deserializeGroup(obj, value);
  }
}
