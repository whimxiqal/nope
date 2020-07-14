package com.minecraftonline.nope.config;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.config.serializer.TargetSetSerializer;
import com.minecraftonline.nope.config.serializer.Vector3dSerializer;
import com.minecraftonline.nope.config.serializer.flag.FlagBooleanSerializer;
import com.minecraftonline.nope.config.serializer.flag.FlagDoubleSerializer;
import com.minecraftonline.nope.config.serializer.flag.FlagEntitySetSerializer;
import com.minecraftonline.nope.config.serializer.flag.FlagGameModeSerializer;
import com.minecraftonline.nope.config.serializer.flag.FlagIntegerSerializer;
import com.minecraftonline.nope.config.serializer.flag.FlagStateSerializer;
import com.minecraftonline.nope.config.serializer.flag.FlagStringSerializer;
import com.minecraftonline.nope.config.serializer.flag.FlagStringSetSerializer;
import com.minecraftonline.nope.config.serializer.flag.FlagVector3dSerializer;
import com.minecraftonline.nope.config.supplier.ConfigLoaderSupplier;
import com.minecraftonline.nope.control.GlobalHost;
import com.minecraftonline.nope.control.Host;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.WorldHost;
import com.minecraftonline.nope.control.flags.FlagBoolean;
import com.minecraftonline.nope.control.flags.FlagDouble;
import com.minecraftonline.nope.control.flags.FlagEntitySet;
import com.minecraftonline.nope.control.flags.FlagGameMode;
import com.minecraftonline.nope.control.flags.FlagInteger;
import com.minecraftonline.nope.control.flags.FlagState;
import com.minecraftonline.nope.control.flags.FlagString;
import com.minecraftonline.nope.control.flags.FlagStringSet;
import com.minecraftonline.nope.control.flags.FlagVector3d;
import com.minecraftonline.nope.control.target.TargetSet;
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

  private ConfigurationOptions configurationOptions;

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
   * Add an additional world, one that did not exist when
   * the server started.
   * Does nothing if the given world is already loaded
   */
  public void loadAdditionalWorld(World world) {
    worldConfigs.computeIfAbsent(world, k -> {
      if (configDir == null) throw new NullPointerException("configDir");
      if (world == null) throw new NullPointerException("world");
      if (configLoaderSupplier == null) throw new NullPointerException("configLoaderSupplier");
      WorldConfigManager worldConfigManager = new WorldConfigManager(configDir, world, configLoaderSupplier);
      worldConfigManager.loadAll();
      return worldConfigManager;
    });
  }

  /**
   * Fill settings down to the lowest level (Fills worlds, and regions)
   *
   * @param globalHost Host to fill
   */
  public void fillSettings(GlobalHost globalHost) {
    for (Map.Entry<World, WorldConfigManager> entry : this.worldConfigs.entrySet()) {
      WorldHost worldHost = globalHost.getWorld(entry.getKey());
      fillSettings(worldHost, entry.getValue().getConfig());
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
        .registerType(TypeToken.of(FlagBoolean.class), new FlagBooleanSerializer())
        .registerType(TypeToken.of(FlagDouble.class), new FlagDoubleSerializer())
        .registerType(TypeToken.of(FlagEntitySet.class), new FlagEntitySetSerializer())
        .registerType(TypeToken.of(FlagGameMode.class), new FlagGameModeSerializer())
        .registerType(TypeToken.of(FlagInteger.class), new FlagIntegerSerializer())
        .registerType(TypeToken.of(FlagState.class), new FlagStateSerializer())
        .registerType(TypeToken.of(FlagString.class), new FlagStringSerializer())
        .registerType(TypeToken.of(FlagStringSet.class), new FlagStringSetSerializer())
        .registerType(TypeToken.of(FlagVector3d.class), new FlagVector3dSerializer());
    return typeSerializerCollection;
  }
}
