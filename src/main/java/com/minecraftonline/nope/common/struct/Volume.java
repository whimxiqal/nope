package com.minecraftonline.nope.common.struct;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Volume {

  @Getter
  @Accessors(fluent = true)
  @Nullable
  private String name;

  public Volume() {
    this(null);
  }

  public Volume(@Nullable String name) {
    this.name = name;
  }

  public void name(String name) {
    this.name = name;
    save();
  }

  @NotNull
  public abstract Cuboid circumscribed();

  @NotNull
  public abstract Cuboid inscribed();

  public abstract boolean contains(int x, int y, int z);

  abstract void save();

}
