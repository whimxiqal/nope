package com.minecraftonline.nope.config.serializer;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Vector3iSerializer implements TypeSerializer<Vector3i> {
  @Nullable
  @Override
  public Vector3i deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
    if (value.isVirtual()) {
      return null;
    }
    return new Vector3i(
        value.getNode("x").getInt(),
        value.getNode("y").getInt(),
        value.getNode("z").getInt()
    );
  }

  @Override
  public void serialize(@NonNull TypeToken<?> type, @Nullable Vector3i obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
    if (obj == null) {
      return;
    }
    value.getNode("x").setValue(obj.getX());
    value.getNode("y").setValue(obj.getY());
    value.getNode("z").setValue(obj.getZ());
  }
}
