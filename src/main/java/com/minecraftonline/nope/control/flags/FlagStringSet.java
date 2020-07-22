package com.minecraftonline.nope.control.flags;

import java.util.Set;

public class FlagStringSet extends Flag<Set<String>> {
  public FlagStringSet(Set<String> value) {
    super(value, (Class<Set<String>>) value.getClass()); // TODO: check this works correctly
  }

  public FlagStringSet(Set<String> value, TargetGroup group) {
    super(value, (Class<Set<String>>) value.getClass(), group);
  }

  @Override
  public String serialize(Flag<Set<String>> flag) {
    StringBuilder builder = new StringBuilder("{");
    flag.getValue().forEach(name -> builder.append(" ").append(name).append(","));
    return builder.deleteCharAt(builder.length() - 1).append(" }").toString();
  }
}
