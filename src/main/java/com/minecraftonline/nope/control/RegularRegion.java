package com.minecraftonline.nope.control;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class RegularRegion extends Region {
  private AABB aabb;
  private UUID worldUUID;

  public RegularRegion(Location<World> corner1, Location<World> corner2) {
    if (!corner1.getExtent().equals(corner2.getExtent())) {
      throw new IllegalStateException("Cannot have a region with corners in different worlds");
    }
    this.worldUUID = corner1.getExtent().getUniqueId();
    this.aabb = new AABB(corner1.getPosition(), corner2.getPosition());
  }

  public RegularRegion(World world, Vector3d corner1, Vector3d corner2) {
    this.worldUUID = world.getUniqueId();
    this.aabb = new AABB(corner1, corner2);
  }

  @Override
  public boolean isLocationInRegion(Location<World> location) {
    return location.getExtent().getUniqueId().equals(worldUUID)
        && aabb.contains(location.getPosition());
  }
}
