package com.minecraftonline.nope.sponge.util;

import com.minecraftonline.nope.common.struct.Location;
import org.spongepowered.api.world.server.ServerWorld;

public final class AdapterUtil {

  private AdapterUtil() {
  }

  public static Location adaptLocation(org.spongepowered.api.world.Location<ServerWorld, ?> location) {
    return new Location(location.blockX(), location.blockY(), location.blockZ(), location.world().key().formatted());
  }

}
