package com.minecraftonline.nope.config;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.config.serializer.TargetSetSerializer;
import com.minecraftonline.nope.config.serializer.Vector3dSerializer;
import com.minecraftonline.nope.config.serializer.Vector3iSerializer;
import com.minecraftonline.nope.config.serializer.flag.FlagSerializer;
import com.minecraftonline.nope.config.supplier.ConfigLoaderSupplier;
import com.minecraftonline.nope.control.GlobalHost;
import com.minecraftonline.nope.control.Host;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.WorldHost;
import com.minecraftonline.nope.control.target.TargetSet;
import com.minecraftonline.nope.util.NopeTypeTokens;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.gradle.internal.impldep.com.google.api.client.repackaged.com.google.common.base.Preconditions.checkNotNull;

/**
 * Global config manager, contains world configs, who contain regions
 * Not abstract as it has no methods that need to be implemented, but
 * an implementation like {@link com.minecraftonline.nope.config.hocon.HoconGlobalConfigManager}
 * is suggested
 */
public class GlobalConfigManager extends ConfigManager {
  private Map<World, WorldConfigManager> worldConfigs = new HashMap<>();

  public GlobalConfigManager(Path configDir, ConfigLoaderSupplier configLoaderSupplier) {
    super(configDir, "global", configLoaderSupplier);
  }

  @Override
  public void loadExtra() {
    for (World world : Sponge.getServer().getWorlds()) {
      WorldConfigManager worldConfigManager = new WorldConfigManager(configDir, world, configLoaderSupplier);
      worldConfigManager.loadAll();
      worldConfigs.put(world, worldConfigManager);
    }
  }

  public void saveExtra() {
    worldConfigs.values().forEach(WorldConfigManager::saveAll);
  }

  /**
   * Load a world.
   * Does nothing if the given world is already loaded
   */
  @Nullable
  public WorldHost loadWorld(World world) {
    return worldConfigs.computeIfAbsent(world, k -> {
      WorldConfigManager worldConfigManager = new WorldConfigManager(configDir, world, configLoaderSupplier);
      worldConfigManager.loadAll();
      this.worldConfigs.put(world, worldConfigManager);
      return worldConfigManager;
    }).getWorldHost();
  }

  /**
   * Fill settings down to the lowest level (Fills worlds, and regions)
   *
   * @param globalHost Host to fill
   */
  public void fillSettings(GlobalHost globalHost) {
    for (Map.Entry<World, WorldConfigManager> entry : this.worldConfigs.entrySet()) {
      WorldHost worldHost = entry.getValue().getWorldHost();
      fillSettings(worldHost, entry.getValue().getConfig());
      globalHost.addWorld(entry.getKey(), worldHost);
      // Region uses different method of filling
    }
  }

  /**
   * Shallow fill the given Host. This means it will only fill
   * the given host, and no sub-hosts, etc.
   *
   * @param host   Host to shallow fill
   * @param source ConfigContainer to get config from
   */
  public void fillSettings(Host host, ConfigContainer<CommentedConfigurationNode> source) {
    if (host instanceof Region) {
      return;
    }
    for (Setting<?> setting : Settings.REGISTRY_MODULE.getByApplicability(host.getApplicability())) {
      setting.getConfigurationPath().ifPresent(path -> setValue(host, path, source, setting));
    }
  }

  public WorldConfigManager getWorldConfig(World world) {
    return this.worldConfigs.get(world);
  }

  /**
   * Just used so that generics can be used correctly
   */
  @SuppressWarnings("UnstableApiUsage")
  static <T extends Serializable> void setValue(Host host, String path, ConfigContainer<CommentedConfigurationNode> configContainer, Setting<T> setting) {
    host.set(setting, configContainer.getNodeValue(path, TypeToken.of(setting.getTypeClass())));
  }

  @Nullable
  private static TypeSerializerCollection typeSerializerCollection;
  public static TypeSerializerCollection getTypeSerializers() {
    if (typeSerializerCollection != null) {
      return typeSerializerCollection;
    }
    typeSerializerCollection = ConfigurationOptions.defaults().getSerializers().newChild()
        .registerType(TypeToken.of(TargetSet.class), new TargetSetSerializer())
        .registerType(TypeToken.of(Vector3d.class), new Vector3dSerializer())
        .registerType(TypeToken.of(Vector3i.class), new Vector3iSerializer())
        .registerType(NopeTypeTokens.FLAG_RAW_TOKEN, new FlagSerializer());
        /*.registerType(TypeToken.of(FlagBoolean.class), new FlagBooleanSerializer())
        .registerType(TypeToken.of(FlagDouble.class), new FlagDoubleSerializer())
        .registerType(TypeToken.of(FlagEntitySet.class), new FlagEntitySetSerializer())
        .registerType(TypeToken.of(FlagGameMode.class), new FlagGameModeSerializer())
        .registerType(TypeToken.of(FlagInteger.class), new FlagIntegerSerializer())
        .registerType(TypeToken.of(FlagState.class), new FlagStateSerializer())
        .registerType(TypeToken.of(FlagString.class), new FlagStringSerializer())
        .registerType(TypeToken.of(FlagStringSet.class), new FlagStringSetSerializer())
        .registerType(TypeToken.of(FlagVector3d.class), new FlagVector3dSerializer());*/
    return typeSerializerCollection;
  }
}
