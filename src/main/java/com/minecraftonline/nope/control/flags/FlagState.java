package com.minecraftonline.nope.control.flags;

public class FlagState extends Flag<Boolean> {
  public FlagState(Boolean value) {
    super(value, Boolean.class);
  }

  public FlagState(Boolean value, TargetGroup group) {
    super(value, Boolean.class, group);
  }
}
