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
}
