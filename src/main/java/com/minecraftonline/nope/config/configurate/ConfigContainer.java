/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
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

package com.minecraftonline.nope.config.configurate;

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
      return resolvePath(path, this.getConfigNode()).getValue(typeToken);
    } catch (ObjectMappingException e) {
      Nope.getInstance().getLogger().error("Invalid config value", e);
    }
    return null;
  }

  public <V> void setNodeValue(String path, V value) {
    setNodeValue(path, value, this.getConfigNode());
  }

  @SuppressWarnings({"unchecked", "UnstableApiUsage"})
  public static <V> void setNodeValue(String path, @Nullable V value, ConfigurationNode node) {
    try {
      ConfigurationNode configNode = resolvePath(path, node);
      if (value == null) {
        configNode.setValue(null);
      }
      else {
        configNode.setValue(TypeToken.of((Class<V>)value.getClass()), value);
      }
    } catch (ObjectMappingException e) {
      Nope.getInstance().getLogger().error("Error setting node value", e);
    }
  }

  private static ConfigurationNode resolvePath(String path, ConfigurationNode node) {
    return node.getNode((Object[]) path.split("[.]"));
  }
}