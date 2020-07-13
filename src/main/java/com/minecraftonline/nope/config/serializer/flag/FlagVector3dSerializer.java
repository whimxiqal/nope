package com.minecraftonline.nope.config.serializer.flag;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.control.flags.FlagUtil;
import com.minecraftonline.nope.control.flags.FlagVector3d;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FlagVector3dSerializer implements TypeSerializer<FlagVector3d> {
  @Nullable
  @Override
  public FlagVector3d deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
    Vector3d vector3d = value.getValue(TypeToken.of(Vector3d.class));
    if (vector3d == null) {
      return null;
    }
    FlagVector3d flagVector3d = new FlagVector3d(vector3d);
    FlagUtil.deserializeGroup(flagVector3d, value);
    return flagVector3d;
  }

  @Override
  public void serialize(@NonNull TypeToken<?> type, @Nullable FlagVector3d obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
    FlagUtil.serialize(obj, value);
  }
}
