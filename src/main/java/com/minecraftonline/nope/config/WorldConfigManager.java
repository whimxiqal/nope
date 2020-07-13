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
    regions.save();
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
      region.set(setting, node.getNode((Object[]) path.split(".")).getValue(TypeToken.of(setting.getTypeClass())));
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

  private void loadFlags(Region region, CommentedConfigurationNode flagsNode) {
    for (Map.Entry<Object, ? extends CommentedConfigurationNode> flagEntry : flagsNode.getChildrenMap().entrySet()) {
      String flagId = flagEntry.getKey().toString();
      if (flagId.contains("-group")) {
        continue; // look for the not -group then get -group if its there
      }
      Optional<Setting<?>> setting = Settings.REGISTRY_MODULE.getById(flagId);

      try {
        if (setting.isPresent() && setting.get().getDefaultValue() instanceof Flag<?>) {
          Setting<Flag<?>> flag = (Setting<Flag<?>>) setting.get(); // But it is checked tho
          setValue(region, flag, flag.getDefaultValue(), flagEntry.getValue(), flagsNode);
        }
      } catch (ObjectMappingException e) {
        Nope.getInstance().getLogger().error("Error reading region flag: " + flagId, e);
      }
    }
  }

  private void saveFlags(Region region, CommentedConfigurationNode flagsNode) {
    for (Map.Entry<Setting<?>, ?> setting : region.getSettingMap().entrySet()) {
      if (setting.getValue() instanceof Flag<?>) {
        Flag<?> flag = (Flag<?>) setting.getValue();
        String id = setting.getKey().getId();
        flagsNode.getNode(id).setValue(flag.getValue());
        if (flag.getGroup() != Flag.TargetGroup.ALL) {
          flagsNode.getNode(id + "-group").setValue(flag.getGroup().toString());
        }
      }
    }
  }

  /**
   * Nothing to see here. Don't ask questions.
   * Setting is raw, because when its not raw, compiler goes roar
   * Java generics great fun 10/10
   */
  @SuppressWarnings("unchecked")
  private static <T> void setValue(Host host, Setting setting, Flag<T> defaultFlag, ConfigurationNode flags, ConfigurationNode node) throws ObjectMappingException {
    T value = node.getValue(TypeToken.of(defaultFlag.getFlagType()));
    Flag<T> flag = new Flag<>(value, defaultFlag.getFlagType());
    host.set(setting, flag);

    String group = flags.getNode(setting.getId() + "-group").getValue(TypeTokens.STRING_TOKEN);
    if (group != null) {
      flag.setGroup(Flag.TargetGroup.valueOf(group.toUpperCase()));
    }
  }
}
