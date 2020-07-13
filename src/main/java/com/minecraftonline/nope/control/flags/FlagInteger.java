package com.minecraftonline.nope.control.flags;

public class FlagInteger extends Flag<Integer> {
  public FlagInteger(Integer value) {
    super(value, Integer.class);
  }

  public FlagInteger(Integer value, TargetGroup group) {
    super(value, Integer.class, group);
  }
}
