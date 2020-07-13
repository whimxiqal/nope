package com.minecraftonline.nope.control.flags;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import javax.annotation.Nullable;

public class FlagUtil {
  public static void serialize(@Nullable Flag<?> flag, ConfigurationNode value) throws ObjectMappingException {
    if (flag == null) {
      throw new ObjectMappingException("Cannot serialize a null flag");
    }
    value.setValue(flag.getValue());
    serializeGroup(flag, value);
  }

  public static void deserializeGroup(Flag<?> flag, ConfigurationNode value) {
    String key = (String) value.getKey();
    String group = value.getParent().getNode(key + "-group").getString();
    if (group != null) {
      try {
        flag.setGroup(Flag.TargetGroup.valueOf(group.toUpperCase()));
      } catch (IllegalArgumentException ignored) {}
    }
  }

  public static void serializeGroup(Flag<?> flag, ConfigurationNode value) {
    if (flag.getGroup() != Flag.TargetGroup.ALL) {
      String key = (String) value.getKey();
      value.getParent().getNode(key + "-group").setValue(flag.getGroup().toString());
    }
  }
}
