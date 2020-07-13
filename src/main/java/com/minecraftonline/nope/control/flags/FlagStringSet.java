package com.minecraftonline.nope.control.flags;

import java.util.Set;

public class FlagStringSet extends Flag<Set<String>> {
  public FlagStringSet(Set<String> value) {
    super(value, (Class<Set<String>>) value.getClass()); // TODO: check this works correctly
  }

  public FlagStringSet(Set<String> value, TargetGroup group) {
    super(value, (Class<Set<String>>) value.getClass(), group);
  }
}
