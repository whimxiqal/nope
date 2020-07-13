package com.minecraftonline.nope.control.flags;

import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class FlagDouble extends Flag<Double> {
  public FlagDouble(Double value) {
    super(value, Double.class);
  }

  public FlagDouble(Double value, TargetGroup group) {
    super(value, Double.class, group);
  }
}
