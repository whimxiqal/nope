package com.minecraftonline.nope.config;

import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.config.supplier.ConfigLoaderSupplier;
import com.minecraftonline.nope.control.flags.Flag;
import com.minecraftonline.nope.control.GlobalRegion;
import com.minecraftonline.nope.control.Host;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.RegularRegion;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.WorldHost;
import javafx.util.Pair;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.util.TypeTokens;
import org.spongepowered.api.world.World;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WorldConfigManager extends ConfigManager {
  private World world;
  private WorldHost worldHost;
  private Map<String, Region> regionConfig = new HashMap<>();
  private ConfigContainer<CommentedConfigurationNode> regions;

  public WorldConfigManager(Path configDir, World world, ConfigLoaderSupplier configLoaderSupplier) {
    super(configDir.resolve(world.getName()), "config", configLoaderSupplier);
    this.world = world;
  }

  @Override
  public void loadExtra() {
    this.worldHost = new WorldHost(world.getUniqueId());
    Path regions = this.configDir.resolve("regions" + CONFIG_FILE_EXTENSION);
    try {
      loadRegions(regions);
    } catch (ObjectMappingException e) {
      Nope.getInstance().getLogger().error("Error loading regions", e);
    }
  }

  @Override
  public void saveExtra() {
    saveRegions();
    this.regions.save();
  }

  public WorldHost getWorldHost() {
    return this.worldHost;
  }

  public Collection<Region> getAllRegions() {
    return this.regionConfig.values();
  }

  public Optional<Region> getRegion(String id) {
    return Optional.ofNullable(this.regionConfig.get(id));
  }

  public void loadRegions(Path path) throws ObjectMappingException {
    regions = new ConfigContainer<>(configLoaderSupplier.createConfigLoader(path));
    regions.load();
    for (Map.Entry<Object, ? extends CommentedConfigurationNode> entry : regions.getConfigNode().getChildrenMap().entrySet()) {
      boolean isGlobalRegion = entry.getKey().toString().equals("__global__");
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
        setting.getConfigurationPath().ifPresent(confPath -> setValue(region, setting, entry.getValue(), confPath));
      }
    }
  }

  private static <T extends Serializable> void setValue(Region region, Setting<T> setting, CommentedConfigurationNode node, String path) {
    try {
      T value = node.getNode((Object[]) path.split(".")).getValue(TypeToken.of(setting.getTypeClass()));
      if (value == null) {
        return;
      }
      region.set(setting, value);
    } catch (ObjectMappingException e) {
      Nope.getInstance().getLogger().error("Error loading region setting", e);
    }
  }

  public void saveRegions() {
    for (Map.Entry<String, Region> entry : this.regionConfig.entrySet()) {
      CommentedConfigurationNode regionNode = this.regions.getConfigNode().getNode(entry.getKey());
      for (Map.Entry<Setting<?>, ?> settingEntry : entry.getValue().getSettingMap().entrySet()) {
        settingEntry.getKey().getConfigurationPath().ifPresent(confPath -> {
          ConfigurationNode node = regionNode.getNode((Object[])confPath.split("."));
          node.setValue(settingEntry.getValue());
        });
      }
    }
  }
}
