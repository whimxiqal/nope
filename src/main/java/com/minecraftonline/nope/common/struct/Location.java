package com.minecraftonline.nope.common.struct;

import java.util.UUID;
import lombok.Data;

/**
 * A generic Minecraft location.
 */
@Data
public class Location {
  private double posX;
  private double posY;
  private double posZ;
  private UUID worldUuid;

  public Location(int x, int y, int z, UUID worldUuid) {
    this.posX = x;
    this.posY = y;
    this.posZ = z;
    this.worldUuid = worldUuid;
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
}
