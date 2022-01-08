package com.minecraftonline.nope.sponge.storage.configurate;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.host.Zone;
import com.minecraftonline.nope.common.storage.ZoneDataHandler;
import com.minecraftonline.nope.common.math.Cylinder;
import com.minecraftonline.nope.common.math.Sphere;
import com.minecraftonline.nope.common.math.Volume;
import com.minecraftonline.nope.sponge.SpongeNope;
import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

public class ZoneConfigurateDataHandler extends SettingsConfigurateDataHandler implements ZoneDataHandler {

  private final Function<String, ConfigurationLoader<CommentedConfigurationNode>> loader;
  private final Function<String, Path> filePath;
  private final Supplier<Collection<ConfigurationLoader<CommentedConfigurationNode>>> allLoader;

  public ZoneConfigurateDataHandler(Function<String, ConfigurationLoader<CommentedConfigurationNode>> loader,
                                    Function<String, Path> filePath,
                                    Supplier<Collection<ConfigurationLoader<CommentedConfigurationNode>>> allLoader) {
    this.loader = loader;
    this.filePath = filePath;
    this.allLoader = allLoader;
  }

  @Override
  public void destroy(Zone zone) {
    File file = filePath.apply(zone.name()).toFile();
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
      root.node("name").set(zone.name());
      root.node("settings").comment("Settings for Zone " + zone.name());
      root.node("priority").set(zone.priority());
      root.node("parent").set(zone.parent().map(Zone::name).orElse(null));
      CommentedConfigurationNode volumes = root.node("volumes");
      volumes.setList(Volume.class, zone.volumes());
      loader.apply(zone.name()).save(root);
    } catch (ConfigurateException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Collection<Zone> load() {
    Map<String, Zone> zones = new HashMap<>();
    // Need to queue other loaders if the one given does not have their parent loaded yet
    Map<String, List<ConfigurationLoader<CommentedConfigurationNode>>> queue = new HashMap<>();
    for (ConfigurationLoader<CommentedConfigurationNode> loader : allLoader.get()) {
      load(zones, queue, loader);
    }
    return zones.values();
  }

  private void load(Map<String, Zone> zones,
                    Map<String, List<ConfigurationLoader<CommentedConfigurationNode>>> queue,
                    ConfigurationLoader<CommentedConfigurationNode> loader) {
    ConfigurationNode root;
    String name;
    // TODO do this for other configurate data handlers...
    //  i.e. put root and name before its own try/catch clause and add an error log message
    //  instead of a print stack trace
    try {
      root = loader.load();
      name = root.node("name").get(String.class);
    } catch (ConfigurateException e) {
      Nope.instance().logger().error("Error loading Zone: " + e.getMessage());
      return;
    }
    if (name == null) {
      return;
    }

    try {
      String parentName = root.node("parent").get(String.class);
      Zone parent;
      if (parentName == null) {
        parent = null;
      } else if (zones.containsKey(parentName)) {
        parent = zones.get(parentName);
      } else {
        queue.computeIfAbsent(parentName, p -> new LinkedList<>()).add(loader);
        return;
      }
      int priority = root.node("priority").getInt();
      List<Volume> volumes;
      try {
        volumes = root.node("volumes").getList(Volume.class);
      } catch (Exception e) {
        Nope.instance().logger().error("Failed parsing volumes for zone: " + name);
        throw e;
      }
      zones.put(name, new Zone(name, parent, priority, volumes));
      if (queue.containsKey(name)) {
        queue.get(name).forEach(queuedLoader -> load(zones, queue, queuedLoader));
      }
    } catch (ConfigurateException e) {
      Nope.instance().logger().error(String.format("Error loading Zone %s: " + e.getMessage(), name));
    }
  }

  private ConfigurationNode volumeRoot(Volume volume) throws SerializationException {
    ConfigurationNode node = CommentedConfigurationNode.root();
    if (volume.name() != null) {
      node.node("name").set(volume.name());
    }
    return node;
  }

  private ConfigurationNode serializeCylinder(Cylinder cylinder) throws SerializationException {
    ConfigurationNode node = volumeRoot(cylinder);
    node.node("type").set("cylinder");
    node.node("world").set(cylinder.domain().id());
    node.node("dimensions", "pos-x").set(cylinder.posX());
    node.node("dimensions", "min-y").set(cylinder.minY());
    node.node("dimensions", "max-y").set(cylinder.maxY());
    node.node("dimensions", "pos-z").set(cylinder.posZ());
    node.node("dimensions", "radius").set(cylinder.radius());
    return node;
  }

  private ConfigurationNode serializeSphere(Sphere sphere) throws SerializationException {
    ConfigurationNode node = volumeRoot(sphere);
    node.node("type").set("sphere");
    node.node("world").set(sphere.domain().id());
    node.node("dimensions", "pos-x").set(sphere.posX());
    node.node("dimensions", "pos-y").set(sphere.posY());
    node.node("dimensions", "pos-z").set(sphere.posZ());
    node.node("dimensions", "radius").set(sphere.radius());
    return node;
  }

}
