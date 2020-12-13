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

import com.minecraftonline.nope.config.ConfigManager;
import com.minecraftonline.nope.config.configurate.supplier.ConfigLoaderSupplier;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.Setting;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.nio.file.Path;
import java.util.UUID;

public abstract class ConfigurateConfigManager {

  public static final String CONFIG_FILE_EXTENSION = ".conf";

  protected Path configDir;
  protected String configFileName;
  protected ConfigLoaderSupplier configLoaderSupplier;
  private ConfigContainer<CommentedConfigurationNode> config = null;

  /**
   * Create a ConfigManager
   *
   * @param configDir            Path Directory to create config in
   * @param configName           Filename excluding extension
   * @param configLoaderSupplier ConfigLoaderSupplier A supplier for ConfigLoaders
   */
  public ConfigurateConfigManager(Path configDir, String configName, ConfigLoaderSupplier configLoaderSupplier) {
    this.configDir = configDir;
    this.configFileName = configName + CONFIG_FILE_EXTENSION;
    this.configLoaderSupplier = configLoaderSupplier;
  }

  public void loadAll() {
    this.configDir.toFile().mkdirs();
    this.config
        = new ConfigContainer<>(configLoaderSupplier.createConfigLoader(configDir.resolve(configFileName)));

    this.config.load();

    loadExtra();
  }

  public void saveAll() {
    this.config.save();

    saveExtra();
  }

  /**
   * Load extra config or other things wanted to be loaded
   * in {@link ConfigurateConfigManager#loadAll()}
   */
  protected void loadExtra() {
  }

  /**
   * Save extra config or other things wanted to be saved
   * in {@link ConfigurateConfigManager#saveAll()}
   */
  protected void saveExtra() {
  }

  public ConfigContainer<CommentedConfigurationNode> getConfig() {
    return this.config;
  }

  public abstract void removeRegion(UUID worldUUID, String name);
}
