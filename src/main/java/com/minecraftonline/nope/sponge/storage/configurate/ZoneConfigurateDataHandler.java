package com.minecraftonline.nope.sponge.storage.configurate;

import com.minecraftonline.nope.common.host.Domain;
import com.minecraftonline.nope.common.host.Zone;
import com.minecraftonline.nope.common.storage.ZoneDataHandler;
import com.minecraftonline.nope.common.struct.Cuboid;
import com.minecraftonline.nope.common.struct.Cylinder;
import com.minecraftonline.nope.common.struct.Sphere;
import com.minecraftonline.nope.common.struct.Volume;
import com.minecraftonline.nope.sponge.SpongeNope;
import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

public class ZoneConfigurateDataHandler extends SettingsConfigurateDataHandler implements ZoneDataHandler {

  private final BiFunction<ResourceKey, String, ConfigurationLoader<CommentedConfigurationNode>> loader;
  private final BiFunction<ResourceKey, String, Path> filePath;

  public ZoneConfigurateDataHandler(BiFunction<ResourceKey, String, ConfigurationLoader<CommentedConfigurationNode>> loader,
                                    BiFunction<ResourceKey, String, Path> filePath) {
    this.loader = loader;
    this.filePath = filePath;
  }

  @Override
  public void destroy(Zone zone) {
    File file = filePath.apply(ResourceKey.resolve(zone.domain().id()), zone.name()).toFile();
    if (file.exists()) {
      if (!file.delete()) {
        SpongeNope.instance().logger().error("Error when trying to destroy zone "
            + zone.name()
            + " by deleting its configuration file");
      }
    }
  }

  @Override
  public void save(Zone zone) {
    try {
      CommentedConfigurationNode root = settingCollectionRoot(zone);
      root.comment("Settings for Zone " + zone.name());
      if (zone.parent() instanceof Domain) {
        root.node("parent", "type").set("world");
        root.node("parent", "name").set(zone.domain().id());
      } else {
        root.node("parent", "type").set("zone");
        root.node("parent", "name").set(zone.parent().name());
      }
      CommentedConfigurationNode volumes = root.node("volumes");
      volumes.set(zone.volumes().stream().map(volume -> {
        try {
          if (volume instanceof Cuboid) {
            return node((Cuboid) volume);
          } else if (volume instanceof Cylinder) {
            return node((Cylinder) volume);
          } else if (volume instanceof Sphere) {
            return node((Sphere) volume);
          } else {
            throw new IllegalStateException("Unknown volume type: " + volume.getClass());
          }
        } catch (SerializationException e) {
          e.printStackTrace();
          return null;
        }
      }).filter(Objects::nonNull).collect(Collectors.toList()));
      loader.apply(ResourceKey.resolve(zone.domain().id()), zone.name()).save(root);
    } catch (ConfigurateException e) {
      e.printStackTrace();
    }
  }

  private ConfigurationNode volumeRoot(Volume volume) throws SerializationException {
    ConfigurationNode node = CommentedConfigurationNode.root();
    if (volume.name() != null) {
      node.node("name").set(volume.name());
    }
    return node;
  }

  private ConfigurationNode node(Cuboid cuboid) throws SerializationException {
    ConfigurationNode node = volumeRoot(cuboid);
    node.node("type").set("cuboid");
    node.node("dimensions", "min-x").set(cuboid.minX());
    node.node("dimensions", "min-y").set(cuboid.minY());
    node.node("dimensions", "min-z").set(cuboid.minZ());
    node.node("dimensions", "max-x").set(cuboid.maxX());
    node.node("dimensions", "max-y").set(cuboid.maxY());
    node.node("dimensions", "max-z").set(cuboid.maxZ());
    return node;
  }

  private ConfigurationNode node(Cylinder cylinder) throws SerializationException {
    ConfigurationNode node = volumeRoot(cylinder);
    node.node("type").set("cylinder");
    node.node("dimensions", "pos-x").set(cylinder.posX());
    node.node("dimensions", "min-y").set(cylinder.minY());
    node.node("dimensions", "max-y").set(cylinder.maxY());
    node.node("dimensions", "pos-z").set(cylinder.posZ());
    node.node("dimensions", "radius").set(cylinder.radius());
    return node;
  }

  private ConfigurationNode node(Sphere sphere) throws SerializationException {
    ConfigurationNode node = volumeRoot(sphere);
    node.node("type").set("sphere");
    node.node("dimensions", "pos-x").set(sphere.posX());
    node.node("dimensions", "pos-y").set(sphere.posY());
    node.node("dimensions", "pos-z").set(sphere.posZ());
    node.node("dimensions", "radius").set(sphere.radius());
    return node;
  }

}
