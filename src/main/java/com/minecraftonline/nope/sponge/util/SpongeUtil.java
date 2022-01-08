package com.minecraftonline.nope.sponge.util;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.math.Vector3d;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.struct.Location;
import com.minecraftonline.nope.sponge.SpongeNope;
import java.util.UUID;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;

public final class SpongeUtil {

  public static String worldToId(ServerWorld world) {
    return world.key().formatted();
  }

  public static Location reduceLocation(ServerLocation location) {
    return new Location(location.blockX(),
        location.blockY(),
        location.blockZ(),
        Nope.instance().hostSystem().domain(worldToId(location.world())));
  }

  public static UUID reducePlayer(Player player) {
    return player.uniqueId();
  }

  public static UUID reduceEntity(Entity entity) {
    if (entity instanceof Player) {
      return reducePlayer((Player) entity);
    } else {
      return null;
    }
  }

  public static org.spongepowered.math.vector.Vector3d raiseVector(Vector3d vector) {
    return new org.spongepowered.math.vector.Vector3d(vector.x(), vector.y(), vector.z());
  }

  public static <T> T valueFor(SettingKey<T> key, Entity entity, ServerLocation location) {
    return SpongeNope.instance().hostSystem().lookup(key, reduceEntity(entity), reduceLocation(location));
  }

  public static <T> T valueFor(SettingKey<T> key, ServerLocation location) {
    return SpongeNope.instance().hostSystem().lookupAnonymous(key, reduceLocation(location));
  }

  private SpongeUtil() {
  }
}
