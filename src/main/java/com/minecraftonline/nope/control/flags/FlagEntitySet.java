package com.minecraftonline.nope.control.flags;

import org.spongepowered.api.entity.EntityType;

import java.util.Set;

public class FlagEntitySet extends Flag<Set<EntityType>> {
  public FlagEntitySet(Set<EntityType> value) {
    super(value, (Class<Set<EntityType>>) value.getClass());
  }

  public FlagEntitySet(Set<EntityType> value, TargetGroup group) {
    super(value, (Class<Set<EntityType>>) value.getClass(), group);
  }

  @Override
  public String serialize(Flag<Set<EntityType>> flag) {
    StringBuilder builder = new StringBuilder("{");
    flag.getValue().stream()
        .map(EntityType::getName)
        .forEach(name -> builder.append(" ").append(name).append(","));
    return builder.deleteCharAt(builder.length() - 1).append(" }").toString();
  }
}
