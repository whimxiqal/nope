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

package me.pietelite.nope.sponge.storage.configurate;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Cylinder;
import me.pietelite.nope.common.math.Sphere;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.common.storage.SceneDataHandler;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.config.SettingValueConfigSerializerRegistrar;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

/**
 * The {@link SceneDataHandler} implemented with Configurate.
 */
public class SceneConfigurateDataHandler extends SettingsConfigurateDataHandler implements SceneDataHandler {

  private final Function<String, ConfigurationLoader<CommentedConfigurationNode>> loader;
  private final Function<String, Path> filePath;
  private final Supplier<Collection<ConfigurationLoader<CommentedConfigurationNode>>> allLoader;

  public SceneConfigurateDataHandler(Function<String, ConfigurationLoader<CommentedConfigurationNode>> loader,
                                     Function<String, Path> filePath,
                                     Supplier<Collection<ConfigurationLoader<CommentedConfigurationNode>>>
                                        allLoader,
                                     SettingValueConfigSerializerRegistrar serializerRegistrar) {
    super(serializerRegistrar);
    this.loader = loader;
    this.filePath = filePath;
    this.allLoader = allLoader;
  }

  @Override
  public void destroy(Scene scene) {
    File file = filePath.apply(scene.name()).toFile();
    if (file.exists()) {
      if (!file.delete()) {
        SpongeNope.instance().logger().error("Error when trying to destroy scene "
            + scene.name()
            + " by deleting its configuration file");
      }
    }
  }

  @Override
  public void save(Scene scene) {
    try {
      CommentedConfigurationNode root = settingCollectionRoot(scene);
      root.node("name").set(scene.name());
      root.node("settings").comment("Settings for Scene " + scene.name());
      root.node("priority").set(scene.priority());
      root.node("parent").set(scene.parent().map(Scene::name).orElse(null));
      root.node("volumes").setList(Volume.class, scene.volumes());
      loader.apply(scene.name()).save(root);
    } catch (ConfigurateException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Collection<Scene> load() {
    Map<String, Scene> zones = new HashMap<>();
    // Need to queue other loaders if the one given does not have their parent loaded yet
    Map<String, List<ConfigurationLoader<CommentedConfigurationNode>>> queue = new HashMap<>();
    for (ConfigurationLoader<CommentedConfigurationNode> loader : allLoader.get()) {
      load(zones, queue, loader);
    }
    return zones.values();
  }

  private void load(Map<String, Scene> zones,
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
      Nope.instance().logger().error("Error loading Scene: " + e.getMessage());
      e.printStackTrace();
      return;
    }
    if (name == null) {
      return;
    }

    try {
      String parentName = root.node("parent").get(String.class);
      Scene parent;
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
        Nope.instance().logger().error("Failed parsing volumes for scene: " + name);
        throw e;
      }
      Scene scene = new Scene(name, parent, priority, volumes);
      scene.setAll(deserializeSettings(root.node("settings").childrenMap()));
      zones.put(name, scene);
      if (queue.containsKey(name)) {
        queue.get(name).forEach(queuedLoader -> load(zones, queue, queuedLoader));
      }
    } catch (ConfigurateException e) {
      Nope.instance().logger().error(String.format("Error loading Scene %s: " + e.getMessage()
          + ". Is it in the right format?", name));
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

  private ConfigurationNode serializeCylinder(Cylinder cylinder) throws SerializationException {
    ConfigurationNode node = volumeRoot(cylinder);
    node.node("type").set("cylinder");
    node.node("world").set(cylinder.domain().name());
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
    node.node("world").set(sphere.domain().name());
    node.node("dimensions", "pos-x").set(sphere.posX());
    node.node("dimensions", "pos-y").set(sphere.posY());
    node.node("dimensions", "pos-z").set(sphere.posZ());
    node.node("dimensions", "radius").set(sphere.radius());
    return node;
  }

}
