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
import com.google.gson.JsonElement;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.SettingLibrary;
import com.minecraftonline.nope.config.configurate.supplier.ConfigLoaderSupplier;
import com.minecraftonline.nope.control.GlobalRegion;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.RegularRegion;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.WorldHost;
import com.minecraftonline.nope.util.NopeTypeTokens;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.world.World;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

public class WorldConfigurateConfigManager extends ConfigurateConfigManager {
  private UUID world;
  private WorldHost worldHost;
  //private Map<String, Region> regionConfig = new HashMap<>();
  private ConfigContainer<CommentedConfigurationNode> regions;

  public WorldConfigurateConfigManager(Path configDir, World world, ConfigLoaderSupplier configLoaderSupplier) {
    super(configDir.resolve(world.getName()), "config", configLoaderSupplier);
    this.world = world.getUniqueId();
  }

  @Override
  public void loadExtra() {
    this.worldHost = new WorldHost(world);
    Path regions = this.configDir.resolve("regions" + CONFIG_FILE_EXTENSION);
    loadRegions(regions);
  }

  @Override
  public void saveExtra() {
    saveRegions();
    this.regions.save();
  }

  public WorldHost getWorldHost() {
    return this.worldHost;
  }

  public void loadRegions(Path path) {
    regions = new ConfigContainer<>(configLoaderSupplier.createConfigLoader(path));
    regions.load();
    for (Map.Entry<Object, ? extends CommentedConfigurationNode> entry : regions.getConfigNode().getChildrenMap().entrySet()) {
      String key = entry.getKey().toString();
      boolean isGlobalRegion = key.equals("__global__");
      Region region;
      if (isGlobalRegion) {
        region = new GlobalRegion(world);
      }
      else {
        region = new RegularRegion(world);
      }

      for (Setting<?> setting : Settings.REGISTRY_MODULE.getByApplicability(Setting.Applicability.REGION)) {
        if (isGlobalRegion && (setting == Settings.REGION_MIN || setting == Settings.REGION_MAX)) {
          continue; // Not applicable to global regions
        }
        setting.getConfigurationPath().ifPresent(confPath -> setValue(region, setting, key + "." + confPath));
      }
      this.worldHost.addRegion(key, region);
    }
    if (this.worldHost.getRegions().get("__global__") == null) {
      this.worldHost.addRegion("__global__", new GlobalRegion(world));
    }
  }

  private <T extends Serializable> void setValue(Region region, Setting<T> setting, String path) {
    T value = this.regions.getNodeValue(path, TypeToken.of(setting.getTypeClass()));
    if (value == null) {
      return;
    }
    region.set(setting, value);
  }

  public void saveRegions() {
    for (Map.Entry<String, Region> entry : this.worldHost.getRegions().entrySet()) {
      CommentedConfigurationNode regionNode = this.regions.getConfigNode().getNode(entry.getKey());
      for (Setting<?> setting : Settings.REGISTRY_MODULE.getByApplicability(Setting.Applicability.REGION)) {
        // Intentionally able to be null, which means if we unset a flag, it will give a null value here, removing it from config
        // instead of not removing it.
        Object value = entry.getValue().getSettingValue(setting).orElse(null);
        setting.getConfigurationPath().ifPresent(confPath -> ConfigContainer.setNodeValue(confPath, value, regionNode));
        // TODO: Uncomment line below when we have a way to get all settings.
        //setRegionValue(setting, region, regionNode.getNode((Object[])setting.getInfo().getPath().split("\\."));
      }
    }
  }

  /*private <T> void setRegionValue(SettingLibrary.Setting<T> setting, HostTree.Region region, ConfigurationNode node) {
    T value = region.getSetting(setting);
    JsonElement jsonElement = setting.serialize(value);
    try {
      node.setValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN, jsonElement);
    } catch (ObjectMappingException e) {
      Nope.getInstance().getLogger().error("Error setting Setting '" + setting.getInfo().getId() + "' in region '" + region.getName() + "', . Skipping save", e);
    }
  }*/

  @Override
  public void removeRegion(UUID worldUUID, String region) {
    if (!worldUUID.equals(this.world)) {
      Nope.getInstance().getLogger().error("remove region was called on the wrong world config manager!");
      return;
    }
    this.regions.getConfigNode().removeChild(region);
  }


}
