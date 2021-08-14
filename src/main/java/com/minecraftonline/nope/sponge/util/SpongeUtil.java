package com.minecraftonline.nope.sponge.util;

import org.spongepowered.api.world.server.ServerWorld;

public final class SpongeUtil {

  public static String worldToId(ServerWorld world) {
    return world.key().formatted();
  }

  private SpongeUtil() {
  }
}
