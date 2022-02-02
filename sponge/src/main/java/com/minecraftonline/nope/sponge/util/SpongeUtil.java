/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

package com.minecraftonline.nope.sponge.util;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.host.Domain;
import com.minecraftonline.nope.common.math.Vector3d;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.sets.ExplosiveSet;
import com.minecraftonline.nope.common.setting.sets.MovementSet;
import com.minecraftonline.nope.common.struct.Location;
import com.minecraftonline.nope.sponge.SpongeNope;
import java.util.UUID;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.explosive.EndCrystal;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.entity.explosive.fused.PrimedTNT;
import org.spongepowered.api.entity.living.monster.Creeper;
import org.spongepowered.api.entity.living.monster.boss.Wither;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.explosive.FireworkRocket;
import org.spongepowered.api.entity.projectile.explosive.WitherSkull;
import org.spongepowered.api.entity.projectile.explosive.fireball.ExplosiveFireball;
import org.spongepowered.api.entity.vehicle.minecart.TNTMinecart;
import org.spongepowered.api.event.cause.entity.MovementType;
import org.spongepowered.api.event.cause.entity.MovementTypes;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;

public final class SpongeUtil {

  private SpongeUtil() {
  }

  public static String worldToId(ServerWorld world) {
    return "_" + world.key().namespace() + "_" + world.key().value();
  }

  public static Location reduceLocation(ServerLocation location) {
    return new Location(location.blockX(),
        location.blockY(),
        location.blockZ(),
        Nope.instance().hostSystem().domain(worldToId(location.world())));
  }

  public static UUID reduceCause(Object cause) {
    if (cause instanceof Player) {
      return reducePlayer((Player) cause);
    } else {
      return null;
    }
  }

  public static UUID reducePlayer(Player player) {
    return player.uniqueId();
  }

  public static UUID reduceEntity(Entity entity) {
    return reduceCause(entity);
  }

  public static Domain reduceWorld(ServerWorld world) {
    return SpongeNope.instance().hostSystem().domain(worldToId(world));
  }

  public static org.spongepowered.math.vector.Vector3d raiseVector(Vector3d vector) {
    return new org.spongepowered.math.vector.Vector3d(vector.x(), vector.y(), vector.z());
  }

  public static <T> T valueFor(SettingKey<T, ?, ?> key, Entity entity) {
    return SpongeNope.instance().hostSystem().lookup(key, reduceEntity(entity), reduceLocation(entity.serverLocation()));
  }

  public static <T> T valueFor(SettingKey<T, ?, ?> key, Object cause, ServerLocation location) {
    return SpongeNope.instance().hostSystem().lookup(key, reduceCause(cause), reduceLocation(location));
  }

  public static <T> T valueFor(SettingKey<T, ?, ?> key, Object cause, Location location) {
    return SpongeNope.instance().hostSystem().lookup(key, reduceCause(cause), location);
  }

  public static <T> T valueFor(SettingKey<T, ?, ?> key, ServerLocation location) {
    return SpongeNope.instance().hostSystem().lookupAnonymous(key, reduceLocation(location));
  }

  public static MovementSet.Movement reduceMovementType(MovementType movementType) {
    if (movementType.equals(MovementTypes.CHORUS_FRUIT.get())) {
      return MovementSet.Movement.CHORUSFRUIT;
    } else if (movementType.equals(MovementTypes.COMMAND.get())) {
      return MovementSet.Movement.COMMAND;
    } else if (movementType.equals(MovementTypes.END_GATEWAY.get())) {
      return MovementSet.Movement.ENDGATEWAY;
    } else if (movementType.equals(MovementTypes.ENDER_PEARL.get())) {
      return MovementSet.Movement.ENDERPEARL;
    } else if (movementType.equals(MovementTypes.ENTITY_TELEPORT.get())) {
      return MovementSet.Movement.ENTITYTELEPORT;
    } else if (movementType.equals(MovementTypes.NATURAL.get())) {
      return MovementSet.Movement.NATURAL;
    } else if (movementType.equals(MovementTypes.PLUGIN.get())) {
      return MovementSet.Movement.PLUGIN;
    } else if (movementType.equals(MovementTypes.PORTAL.get())) {
      return MovementSet.Movement.PORTAL;
    } else {
      throw new IllegalArgumentException("Unknown movement type: " + movementType);
    }
  }

  public static ExplosiveSet.Explosive reduceExplosive(Explosive explosive) {
    if (explosive instanceof Creeper) {
      return ExplosiveSet.Explosive.CREEPER;
    } else if (explosive instanceof EndCrystal) {
      return ExplosiveSet.Explosive.ENDCRYSTAL;
    } else if (explosive instanceof ExplosiveFireball) {
      return ExplosiveSet.Explosive.EXPLOSIVEFIREBALL;
    } else if (explosive instanceof FireworkRocket) {
      return ExplosiveSet.Explosive.FIREWORK;
    } else if (explosive instanceof PrimedTNT) {
      return ExplosiveSet.Explosive.PRIMEDTNT;
    } else if (explosive instanceof TNTMinecart) {
      return ExplosiveSet.Explosive.TNTMINECART;
    } else if (explosive instanceof Wither) {
      return ExplosiveSet.Explosive.WITHER;
    } else if (explosive instanceof WitherSkull) {
      return ExplosiveSet.Explosive.WITHERSKULL;
    } else {
      throw new IllegalArgumentException("Unknown explosive type: " + explosive);
    }
  }
}
