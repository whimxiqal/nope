package com.minecraftonline.nope.common.struct;

import org.jetbrains.annotations.NotNull;

public class Cylinder extends Volume {

  private final int posX;
  private final int minY;
  private final int maxY;
  private final int posZ;
  private final double radius;
  private final double radiusSquared;

  private final Cuboid circumscribed;
  private final Cuboid inscribed;

  public Cylinder(int posX,
                  int minY,
                  int maxY,
                  int posZ,
                  double radius) {
    this.posX = posX;
    this.minY = minY;
    this.maxY = maxY;
    this.posZ = posZ;
    this.radius = radius;
    this.radiusSquared = radius * radius;

    circumscribed = new Cuboid(
        (int) Math.floor(posX - radius),
        minY,
        (int) Math.floor(posZ - radius),
        (int) Math.ceil(posX + radius),
        maxY,
        (int) Math.ceil(posZ + radius));

    double radiusSqrt2Over2 = radius * Math.sqrt(2) / 2;
    inscribed = new Cuboid(
        (int) Math.ceil(posX - radiusSqrt2Over2),
        minY,
        (int) Math.ceil(posZ - radiusSqrt2Over2),
        (int) Math.floor(posX + radiusSqrt2Over2),
        maxY,
        (int) Math.floor(posZ + radiusSqrt2Over2)
    );
  }

  public int posX() {
    return this.posX;
  }

  public int minY() {
    return this.minY;
  }

  public int maxY() {
    return this.maxY;
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
    return y >= minY
        && y <= maxY
        && (posX - x) * (posX - x) + (posZ - z) * (posZ - z) <= radiusSquared;
  }

}
