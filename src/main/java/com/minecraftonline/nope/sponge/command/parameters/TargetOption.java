package com.minecraftonline.nope.sponge.command.parameters;

public enum TargetOption {
  ALL("Targets all users"),
  NONE("Targets no users"),
  EMPTY("Removes target");

  public final String description;

  TargetOption(String description) {
    this.description = description;
  }
}
