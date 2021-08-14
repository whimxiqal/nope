package com.minecraftonline.nope.common.struct;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Vector3i {
  private int x;
  private int y;
  private int z;

  public double distanceSquared() {
    return (double) x * (double) x
        + (double) y * (double) y
        + (double) z * (double) z;
  }

}
