package com.minecraftonline.nope.config.serializer;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Vector3dSerializer implements TypeSerializer<Vector3d> {
  @Nullable
  @Override
  public Vector3d deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
    if (value.isVirtual()) {
      return null;
    }
    return new Vector3d(
        value.getNode("x").getDouble(),
        value.getNode("y").getDouble(),
        value.getNode("z").getDouble()
    );
  }

  @Override
  public void serialize(@NonNull TypeToken<?> type, @Nullable Vector3d obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
    if (obj == null) {
      throw new ObjectMappingException("Cannot serialize a null Vector3d");
    }
    value.getNode("x").setValue(obj.getX());
    value.getNode("y").setValue(obj.getY());
    value.getNode("z").setValue(obj.getZ());
  }
}
