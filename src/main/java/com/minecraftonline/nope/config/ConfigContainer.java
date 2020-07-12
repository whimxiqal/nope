package com.minecraftonline.nope.config;

import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.Nope;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import javax.annotation.Nullable;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Contains a ConfigurationLoader and provides access to easily load and save
 *
 * @param <T>
 */
public class ConfigContainer<T extends ConfigurationNode> {
  private ConfigurationLoader<T> configurationLoader;
  private T configNode = null;

  public ConfigContainer(ConfigurationLoader<T> configurationLoader) {
    this.configurationLoader = configurationLoader;
  }

  /**
   * Loads a config
   *
   * @return T for ease, although also stored, or null if loading failed
   * @throws IllegalStateException if there is an IOException
   */
  @Nullable
  public T load() {
    try {
      this.configNode = configurationLoader.load();
      return this.configNode;
    } catch (IOException e) {
      Nope.getInstance().getLogger().error("Error loading config", e);
    }
    return null;
  }

  /**
   * Saves a config
   *
   * @throws IllegalStateException if there is an IOException
   */
  public void save() {
    if (configNode == null) {
      return; // Nothing loaded
    }
    try {
      configurationLoader.save(configNode);
    } catch (IOException e) {
      Nope.getInstance().getLogger().error("Error saving config", e);
    }
  }

  public ConfigurationLoader<T> getConfigurationLoader() {
    return configurationLoader;
  }

  /**
   * Gets the loaded config node
   *
   * @return T
   * @throws IllegalStateException if load() has not been called, or did not succeed
   */
  public T getConfigNode() {
    return checkNotNull(configNode, "Attempt to get config node before it was loaded!");
  }

  /**
   * Gets a node value, with a path, split by the '.' delimiter
   *
   * @param path      Path to value
   * @param typeToken TypeToken of what type is expected
   * @param <V>       Value expected
   * @return V Value, or null if none is present or if value could not be mapped.
   */
  @Nullable
  public <V> V getNodeValue(String path, TypeToken<V> typeToken) {
    try {
      return configNode.getNode((Object[]) path.split("[.]")).getValue(typeToken);
    } catch (ObjectMappingException e) {
      Nope.getInstance().getLogger().error("Invalid config value", e);
    }
    return null;
  }
}
