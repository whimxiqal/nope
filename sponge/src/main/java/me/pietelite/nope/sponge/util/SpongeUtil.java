/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.pietelite.nope.sponge.util;

import java.util.UUID;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.setting.data.Movement;
import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.math.Vector3d;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.struct.Location;
import me.pietelite.nope.sponge.SpongeNope;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.explosive.EndCrystal;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.entity.explosive.fused.PrimedTNT;
import org.spongepowered.api.entity.living.monster.Creeper;
import org.spongepowered.api.entity.living.monster.boss.Wither;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.entity.projectile.explosive.FireworkRocket;
import org.spongepowered.api.entity.projectile.explosive.WitherSkull;
import org.spongepowered.api.entity.projectile.explosive.fireball.ExplosiveFireball;
import org.spongepowered.api.entity.vehicle.minecart.TNTMinecart;
import org.spongepowered.api.event.cause.entity.MovementType;
import org.spongepowered.api.event.cause.entity.MovementTypes;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;

/**
 * A utility class to manage miscellaneous Sponge-related tasks.
 */
public final class SpongeUtil {

  private SpongeUtil() {
  }

  /**
   * Get the Nope id of a world.
   *
   * @param world the world
   * @return the id
   */
  public static String worldToId(ServerWorld world) {
    return "_" + world.key().namespace() + "_" + world.key().value();
  }

  /**
   * "Reduce" a Sponge type {@link ServerLocation} to a local type {@link Location}.
   *
   * @param location the sponge type location
   * @return the local type location
   */
  public static Location reduceLocation(ServerLocation location) {
    return new Location(location.blockX(),
        location.blockY(),
        location.blockZ(),
        Nope.instance().hostSystem().domain(worldToId(location.world())));
  }

  /**
   * "Reduce" a Sponge type cause to a unique identifier, or null if it can't be.
   *
   * @param cause the cause
   * @return the local type location
   */
  public static UUID reduceCause(Object cause) {
    if (cause instanceof ServerPlayer) {
      return reducePlayer((ServerPlayer) cause);
    } else {
      return null;
    }
  }

  /**
   * "Reduce" a Sponge type {@link ServerPlayer} to its unique identifier.
   *
   * @param player the player
   * @return the local type location
   */
  public static UUID reducePlayer(ServerPlayer player) {
    return player.uniqueId();
  }

  /**
   * "Reduce" a Sponge type {@link Entity} to its unique identifier, only if it's a player.
   *
   * @param entity the entity
   * @return the local type location
   */
  public static UUID reduceEntity(Entity entity) {
    return reduceCause(entity);
  }

  /**
   * "Reduce" a Sponge type {@link ServerWorld} to a local {@link Domain}.
   *
   * @param world the world
   * @return the domain
   */
  public static Domain reduceWorld(ServerWorld world) {
    return SpongeNope.instance().hostSystem().domain(worldToId(world));
  }

  public static org.spongepowered.math.vector.Vector3d raiseVector(Vector3d vector) {
    return new org.spongepowered.math.vector.Vector3d(vector.x(), vector.y(), vector.z());
  }

  /**
   * See {@link #valueFor(SettingKey, Object, Location)}.
   *
   * @param key    a key
   * @param entity an entity, which really is only used if it is a player
   * @param <T>    the data type
   * @return the evaluated data
   */
  public static <T> T valueFor(SettingKey<T, ?, ?> key, Entity entity) {
    return SpongeNope.instance().hostSystem().lookup(key,
        reduceEntity(entity),
        reduceLocation(entity.serverLocation())).result();
  }

  /**
   * An alias for {@link me.pietelite.nope.common.host.HostSystem#lookup(SettingKey, UUID, Location)}.
   *
   * @param key      a key
   * @param cause    the (root) cause of the event causing this lookup
   * @param location the location at which to evaluate
   * @param <T>      the data type
   * @return the evaluated data
   */
  public static <T> T valueFor(SettingKey<T, ?, ?> key, Object cause, ServerLocation location) {
    return SpongeNope.instance().hostSystem().lookup(key,
        reduceCause(cause),
        reduceLocation(location)).result();
  }

  /**
   * See {@link #valueFor(SettingKey, Object, ServerLocation)}, but with a local type {@link Location}.
   *
   * @param key      the key
   * @param cause    the cause
   * @param location the location
   * @param <T>      the data type
   * @return the evaluated data
   */
  public static <T> T valueFor(SettingKey<T, ?, ?> key, Object cause, Location location) {
    return SpongeNope.instance().hostSystem().lookup(key, reduceCause(cause), location).result();
  }

  public static <T> T valueFor(SettingKey<T, ?, ?> key, ServerLocation location) {
    return SpongeNope.instance().hostSystem().lookupAnonymous(key, reduceLocation(location)).result();
  }

  /**
   * "Reduce" a Sponge {@link MovementType} type to a local {@link Movement} type.
   *
   * @param movementType the Sponge movement type
   * @return the local type
   */
  public static Movement reduceMovementType(MovementType movementType) {
    if (movementType.equals(MovementTypes.CHORUS_FRUIT.get())) {
      return Movement.CHORUSFRUIT;
    } else if (movementType.equals(MovementTypes.COMMAND.get())) {
      return Movement.COMMAND;
    } else if (movementType.equals(MovementTypes.END_GATEWAY.get())) {
      return Movement.ENDGATEWAY;
    } else if (movementType.equals(MovementTypes.ENDER_PEARL.get())) {
      return Movement.ENDERPEARL;
    } else if (movementType.equals(MovementTypes.ENTITY_TELEPORT.get())) {
      return Movement.ENTITYTELEPORT;
    } else if (movementType.equals(MovementTypes.NATURAL.get())) {
      return Movement.NATURAL;
    } else if (movementType.equals(MovementTypes.PLUGIN.get())) {
      return Movement.PLUGIN;
    } else if (movementType.equals(MovementTypes.PORTAL.get())) {
      return Movement.PORTAL;
    } else {
      throw new IllegalArgumentException("Unknown movement type: " + movementType);
    }
  }

  /**
   * "Reduce" a Sponge {@link Explosive} type to a local
   * {@link me.pietelite.nope.common.api.setting.data.Explosive} type.
   *
   * @param explosive the Sponge explosive
   * @return the local type
   */
  public static me.pietelite.nope.common.api.setting.data.Explosive reduceExplosive(Explosive explosive) {
    if (explosive instanceof Creeper) {
      return me.pietelite.nope.common.api.setting.data.Explosive.CREEPER;
    } else if (explosive instanceof EndCrystal) {
      return me.pietelite.nope.common.api.setting.data.Explosive.ENDCRYSTAL;
    } else if (explosive instanceof ExplosiveFireball) {
      return me.pietelite.nope.common.api.setting.data.Explosive.EXPLOSIVEFIREBALL;
    } else if (explosive instanceof FireworkRocket) {
      return me.pietelite.nope.common.api.setting.data.Explosive.FIREWORK;
    } else if (explosive instanceof PrimedTNT) {
      return me.pietelite.nope.common.api.setting.data.Explosive.PRIMEDTNT;
    } else if (explosive instanceof TNTMinecart) {
      return me.pietelite.nope.common.api.setting.data.Explosive.TNTMINECART;
    } else if (explosive instanceof Wither) {
      return me.pietelite.nope.common.api.setting.data.Explosive.WITHER;
    } else if (explosive instanceof WitherSkull) {
      return me.pietelite.nope.common.api.setting.data.Explosive.WITHERSKULL;
    } else {
      throw new IllegalArgumentException("Unknown explosive type: " + explosive);
    }
  }
}
