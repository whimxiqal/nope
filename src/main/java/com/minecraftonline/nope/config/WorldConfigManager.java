package com.minecraftonline.nope.config;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.config.supplier.ConfigLoaderSupplier;
import com.minecraftonline.nope.control.GlobalRegion;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.RegularRegion;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.WorldHost;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.world.World;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Map;

public class WorldConfigManager extends ConfigManager {
  private World world;
  private WorldHost worldHost;
  //private Map<String, Region> regionConfig = new HashMap<>();
  private ConfigContainer<CommentedConfigurationNode> regions;

  public WorldConfigManager(Path configDir, World world, ConfigLoaderSupplier configLoaderSupplier) {
    super(configDir.resolve(world.getName()), "config", configLoaderSupplier);
    this.world = world;
  }

  @Override
  public void loadExtra() {
    this.worldHost = new WorldHost(world.getUniqueId());
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
        region = new GlobalRegion(world.getUniqueId());
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
    if (this.worldHost.getRegions().get("__global_)") == null) {
      this.worldHost.addRegion("__global__", new GlobalRegion(world.getUniqueId()));
    }
  }

  private <T extends Serializable> void setValue(Region region, Setting<T> setting, String path) {
    T value = this.regions.getNodeValue(path, TypeToken.of((Class<T>)setting.getClass()));
    if (value == null) {
      return;
    }
    region.set(setting, value);
  }

  public void saveRegions() {
    for (Map.Entry<String, Region> entry : this.worldHost.getRegions().entrySet()) {
      CommentedConfigurationNode regionNode = this.regions.getConfigNode().getNode(entry.getKey());
      for (Map.Entry<Setting<?>, ?> settingEntry : entry.getValue().getSettingMap().entrySet()) {
        settingEntry.getKey().getConfigurationPath().ifPresent(confPath -> {
          ConfigContainer.setNodeValue(confPath, settingEntry.getValue(), regionNode);
        });
      }
    }
  }
}
