package com.minecraftonline.nope.control.flags;

public class FlagBoolean extends Flag<Boolean> {
  public FlagBoolean(Boolean value) {
    super(value, Boolean.class);
  }

  public FlagBoolean(Boolean value, TargetGroup group) {
    super(value, Boolean.class, group);
  }
}
