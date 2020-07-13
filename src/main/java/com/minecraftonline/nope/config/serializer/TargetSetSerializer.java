package com.minecraftonline.nope.config.serializer;

import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.control.target.Target;
import com.minecraftonline.nope.control.target.TargetSet;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.util.TypeTokens;

public class TargetSetSerializer implements TypeSerializer<TargetSet> {
  @Nullable
  @Override
  public TargetSet deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
    TargetSet targetSet = new TargetSet();
    for (Target.TargetType targetType : Target.TargetType.values()) {
      ConfigurationNode node = value.getNode(targetType.getKey());
      node.getList(TypeTokens.STRING_TOKEN).stream()
          .map(targetType::deserialize)
          .forEach(targetSet::add);
    }
    return targetSet;
  }

  @Override
  public void serialize(@NonNull TypeToken<?> type, @Nullable TargetSet obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
    if (obj == null) {
      throw new ObjectMappingException("Cannot serialize a null TargetSet");
    }
    for (Target.TargetType targetType : obj.getTargets().keySet()) {
      value.getNode(targetType.getKey()).setValue(obj.getTargets().get(targetType));
    }
  }
}
