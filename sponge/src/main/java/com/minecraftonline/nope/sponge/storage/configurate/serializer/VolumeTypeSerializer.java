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

package com.minecraftonline.nope.sponge.storage.configurate.serializer;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.math.Cuboid;
import com.minecraftonline.nope.common.math.Cylinder;
import com.minecraftonline.nope.common.math.Slab;
import com.minecraftonline.nope.common.math.Sphere;
import com.minecraftonline.nope.common.math.Volume;
import java.lang.reflect.Type;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public class VolumeTypeSerializer implements TypeSerializer<Volume> {
  @Override
  public Volume deserialize(Type type, ConfigurationNode node) throws SerializationException {
    String volumeType = node.node("type").getString().toLowerCase();
    switch (volumeType) {
      case "box":
        return deserializeCuboid(node);
      case "cylinder":
        return deserializeCylinder(node);
      case "sphere":
        return deserializeSphere(node);
      case "slab":
        return deserializeSlab(node);
      default:
        throw new SerializationException("Volume type " + volumeType + " was not recognized. "
            + "Options are box, cylinder, sphere, or slab");
    }
  }

  @Override
  public void serialize(Type type, @Nullable Volume obj, ConfigurationNode node) throws SerializationException {
    if (obj == null) {
      return;
    }
    if (obj instanceof Cuboid) {
      serializeCuboid((Cuboid) obj, node);
    } else if (obj instanceof Cylinder) {
      serializeCylinder((Cylinder) obj, node);
    } else if (obj instanceof Sphere) {
      serializeSphere((Sphere) obj, node);
    } else if (obj instanceof Slab) {
      serializeSlab((Slab) obj, node);
    } else {
      throw new SerializationException("Volume type " + obj.getClass().getName() + " is not recognized.");
    }
  }

  private Cuboid deserializeCuboid(ConfigurationNode node) throws SerializationException {
    Cuboid obj = new Cuboid(
        Nope.instance().hostSystem().domain(node.node("world").getString()),
        node.node("dimensions").node("min-x").require(Integer.class),
        node.node("dimensions").node("min-y").require(Integer.class),
        node.node("dimensions").node("min-z").require(Integer.class),
        node.node("dimensions").node("max-x").require(Integer.class),
        node.node("dimensions").node("max-y").require(Integer.class),
        node.node("dimensions").node("max-z").require(Integer.class));
    obj.name(node.node("name").getString());
    return obj;
  }

  private Cylinder deserializeCylinder(ConfigurationNode node) throws SerializationException {
    Cylinder obj = new Cylinder(
        Nope.instance().hostSystem().domain(node.node("world").getString()),
        node.node("dimensions").node("pos-x").require(Integer.class),
        node.node("dimensions").node("min-y").require(Integer.class),
        node.node("dimensions").node("max-y").require(Integer.class),
        node.node("dimensions").node("pos-z").require(Integer.class),
        node.node("dimensions").node("radius").require(Double.class));
    obj.name(node.node("name").getString());
    return obj;
  }

  private Sphere deserializeSphere(ConfigurationNode node) throws SerializationException {
    Sphere obj = new Sphere(
        Nope.instance().hostSystem().domain(node.node("world").getString()),
        node.node("dimensions").node("pos-x").require(Integer.class),
        node.node("dimensions").node("pos-y").require(Integer.class),
        node.node("dimensions").node("pos-z").require(Integer.class),
        node.node("dimensions").node("radius").require(Double.class));
    obj.name(node.node("name").getString());
    return obj;
  }

  private Slab deserializeSlab(ConfigurationNode node) throws SerializationException {
    Slab obj = new Slab(
        Nope.instance().hostSystem().domain(node.node("world").getString()),
        node.node("dimensions").node("min-y").require(Integer.class),
        node.node("dimensions").node("max-y").require(Integer.class));
    obj.name(node.node("name").getString());
    return obj;
  }

  private void serializeCuboid(@Nullable Cuboid obj, ConfigurationNode node) throws SerializationException {
    if (obj == null) {
      return;
    }
    if (obj.name() != null) {
      node.node("name").set(obj.name());
    }
    node.node("type").set("box");
    node.node("world").set(obj.domain().name());
    node.node("dimensions", "min-x").set(obj.minX());
    node.node("dimensions", "min-y").set(obj.minY());
    node.node("dimensions", "min-z").set(obj.minZ());
    node.node("dimensions", "max-x").set(obj.maxX());
    node.node("dimensions", "max-y").set(obj.maxY());
    node.node("dimensions", "max-z").set(obj.maxZ());
  }

  private void serializeCylinder(@Nullable Cylinder obj, ConfigurationNode node) throws SerializationException {
    if (obj == null) {
      return;
    }
    if (obj.name() != null) {
      node.node("name").set(obj.name());
    }
    node.node("type").set("cylinder");
    node.node("world").set(obj.domain().name());
    node.node("dimensions", "pos-x").set(obj.posX());
    node.node("dimensions", "min-y").set(obj.minY());
    node.node("dimensions", "max-y").set(obj.maxY());
    node.node("dimensions", "pos-z").set(obj.posZ());
    node.node("dimensions", "radius").set(obj.radius());
  }

  private void serializeSphere(@Nullable Sphere obj, ConfigurationNode node) throws SerializationException {
    if (obj == null) {
      return;
    }
    if (obj.name() != null) {
      node.node("name").set(obj.name());
    }
    node.node("type").set("sphere");
    node.node("world").set(obj.domain().name());
    node.node("dimensions", "pos-x").set(obj.posX());
    node.node("dimensions", "pos-y").set(obj.posY());
    node.node("dimensions", "pos-z").set(obj.posZ());
    node.node("dimensions", "radius").set(obj.radius());
  }

  private void serializeSlab(@Nullable Slab obj, ConfigurationNode node) throws SerializationException {
    if (obj == null) {
      return;
    }
    if (obj.name() != null) {
      node.node("name").set(obj.name());
    }
    node.node("type").set("slab");
    node.node("world").set(obj.domain().name());
    node.node("dimensions", "min-y").set(obj.minY());
    node.node("dimensions", "max-y").set(obj.maxY());
  }
}
