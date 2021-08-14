package com.minecraftonline.nope.common.struct;

import com.minecraftonline.nope.common.host.Domain;
import lombok.Data;

/**
 * A generic Minecraft location.
 */
@Data
public class Location {
  private double posX;
  private double posY;
  private double posZ;
  private Domain domain;

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
}
