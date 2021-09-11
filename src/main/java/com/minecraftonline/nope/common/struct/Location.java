package com.minecraftonline.nope.common.struct;

import com.minecraftonline.nope.common.host.Domain;
import com.minecraftonline.nope.common.math.Vector3d;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * A generic Minecraft location.
 */
public class Location {

  @Getter
  @Accessors(fluent = true)
  private final double posX;
  @Getter
  @Accessors(fluent = true)
  private final double posY;
  @Getter
  @Accessors(fluent = true)
  private final double posZ;
  @Getter
  @Accessors(fluent = true)
  private final Domain domain;

  public Location(int x, int y, int z, Domain domain) {
    this.posX = x;
    this.posY = y;
    this.posZ = z;
    this.domain = domain;
  }

  public int getBlockX() {
    return (int) Math.floor(posX);
  }

  public int getBlockY() {
    return (int) Math.floor(posY);
  }

  public int getBlockZ() {
    return (int) Math.floor(posZ);
  }

  public Vector3d vector3d() {
    return Vector3d.of(posX, posY, posZ);
  }
}
