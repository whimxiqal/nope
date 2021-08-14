package com.minecraftonline.nope.common.struct;

import org.jetbrains.annotations.NotNull;

public class Sphere extends Volume {

  private final int posX;
  private final int posY;
  private final int posZ;
  private final double radius;
  private final double radiusSquared;

  private final Cuboid circumscribed;
  private final Cuboid inscribed;

  public Sphere(int posX,
                int posY,
                int posZ,
                double radius) {
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
    this.radius = radius;
    this.radiusSquared = radius * radius;

    circumscribed = new Cuboid(
        (int) Math.floor(posX - radius),
        (int) Math.floor(posY - radius),
        (int) Math.floor(posZ - radius),
        (int) Math.ceil(posX + radius),
        (int) Math.ceil(posY + radius),
        (int) Math.ceil(posZ + radius));

    double radiusSqrt3Over3 = radius * Math.sqrt(3) / 3;
    inscribed = new Cuboid(
        (int) Math.ceil(posX - radiusSqrt3Over3),
        (int) Math.ceil(posY - radiusSqrt3Over3),
        (int) Math.ceil(posZ - radiusSqrt3Over3),
        (int) Math.floor(posX + radiusSqrt3Over3),
        (int) Math.floor(posY + radiusSqrt3Over3),
        (int) Math.floor(posZ + radiusSqrt3Over3)
    );
  }

  public int posX() {
    return this.posX;
  }

  public int posY() {
    return this.posY;
  }

  public int posZ() {
    return this.posZ;
  }

  public double radius() {
    return this.radius;
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
    return (posX - x) * (posX - x) + (posY - y) * (posY - y) + (posZ - z) * (posZ - z) <= radiusSquared;
  }

}
