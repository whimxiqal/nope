package com.minecraftonline.nope.common.struct;

import org.jetbrains.annotations.NotNull;

public class Slab extends Volume {

  private final int minY;
  private final int maxY;

  private final Cuboid circumscribed;
  private final Cuboid inscribed;

  public Slab(int minY,
              int maxY) {
    this.minY = minY;
    this.maxY = maxY;

    circumscribed = new Cuboid(
        Integer.MIN_VALUE,
        minY,
        Integer.MIN_VALUE,
        Integer.MAX_VALUE,
        maxY,
        Integer.MAX_VALUE);

    inscribed = circumscribed;
  }

  public int minY() {
    return this.minY;
  }

  public int maxY() {
    return this.maxY;
  }

  @Override
  public @NotNull Cuboid circumscribed() {
    return circumscribed;
  }

  @Override
  public @NotNull Cuboid inscribed() {
    return inscribed;
  }

  @Override
  public boolean contains(int x, int y, int z) {
    return y >= minY
        && y <= maxY;
  }

}
