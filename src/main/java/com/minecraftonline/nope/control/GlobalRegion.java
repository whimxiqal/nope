package com.minecraftonline.nope.control;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

/**
 * The global region that spans an entire world,
 * whos name is always __global__
 */
public class GlobalRegion extends Region {
  private UUID worldUUID;

  public GlobalRegion(UUID worldUUID) {
    this.worldUUID = worldUUID;
  }

  @Override
  public boolean isLocationInRegion(Location<World> location) {
    return location.getExtent().getUniqueId().equals(worldUUID);
  }
}
