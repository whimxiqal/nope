package com.minecraftonline.nope.config;

import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.config.supplier.ConfigLoaderSupplier;
import com.minecraftonline.nope.control.Flag;
import com.minecraftonline.nope.control.GlobalRegion;
import com.minecraftonline.nope.control.Host;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.RegularRegion;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.WorldHost;
import com.minecraftonline.nope.control.target.TargetSet;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.util.TypeTokens;
import org.spongepowered.api.world.World;

import java.io.File;
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
    loadRegions(regions);
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

  private void loadRegions(Path path) {
    regions = new ConfigContainer<>(configLoaderSupplier.createConfigLoader(path));
    regions.load();
    for (Map.Entry<Object, ? extends CommentedConfigurationNode> entry : regions.getConfigNode().getChildrenMap().entrySet()) {
      CommentedConfigurationNode regionNode = entry.getValue();
      try {
        Region region;
        if (entry.getKey().toString().equals("__global__")) {
          region = new GlobalRegion(world.getUniqueId());
        }
        else {
          regionNode.getNode("min");
          region = new RegularRegion(world,
              regionNode.getNode("min").getValue(TypeTokens.VECTOR_3D_TOKEN),
              regionNode.getNode("max").getValue(TypeTokens.VECTOR_3D_TOKEN)
          );
        }
        region.setOwners(regionNode.getNode("owners").getValue(TypeToken.of(TargetSet.class)));
        region.setMembers(regionNode.getNode("members").getValue(TypeToken.of(TargetSet.class)));

        region.setPriority(regionNode.getNode("priority").getInt());

        // TODO: add type https://worldguard.enginehub.org/en/latest/regions/storage/#yaml

        loadFlags(region, entry.getValue().getNode("flags"));

        this.regionConfig.put(entry.getKey().toString(), region);
      } catch (ObjectMappingException e) {
        Nope.getInstance().getLogger().error("Error loading region: '" + entry.getKey().toString() + "'", e);
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
