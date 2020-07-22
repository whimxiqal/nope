package com.minecraftonline.nope.control.flags;

public class FlagState extends Flag<Boolean> {
  public FlagState(Boolean value) {
    super(value, Boolean.class);
  }

  public FlagState(Boolean value, TargetGroup group) {
    super(value, Boolean.class, group);
  }

  @Override
  public boolean shouldUseSerializeForConfigurate() {
    return true;
  }

  @Override
  public String serialize(Flag<Boolean> flag) {
    if (flag.getValue() == null) {
      throw new IllegalStateException("Flag<Boolean> with a null boolean!");
    }
    return flag.getValue() ? "allow" : "deny";
  }

  @Override
  public Boolean deserialize(String s) {
    if (s.equals("allow")) {
      return true;
    }
    if (s.equals("deny")) {
      return false;
    }
    throw new IllegalStateException("Tried to deserialize invalid Flag string!");
  }
}
