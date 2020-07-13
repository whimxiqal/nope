package com.minecraftonline.nope.control.flags;

public class FlagString extends Flag<String> {
  public FlagString(String value) {
    super(value, String.class);
  }

  public FlagString(String value, TargetGroup group) {
    super(value, String.class, group);
  }
}
