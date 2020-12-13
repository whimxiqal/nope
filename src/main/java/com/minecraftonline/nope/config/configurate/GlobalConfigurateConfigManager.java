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

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.config.ConfigManager;
import com.minecraftonline.nope.config.configurate.hocon.HoconGlobalConfigurateConfigManager;
import com.minecraftonline.nope.config.configurate.serializer.TargetSetSerializer;
import com.minecraftonline.nope.config.configurate.serializer.Vector3dSerializer;
import com.minecraftonline.nope.config.configurate.serializer.Vector3iSerializer;
import com.minecraftonline.nope.config.configurate.serializer.flag.FlagSerializer;
import com.minecraftonline.nope.config.configurate.supplier.ConfigLoaderSupplier;
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
import java.util.UUID;

/**
 * Global config manager, contains world configs, who contain regions
 * Not abstract as it has no methods that need to be implemented, but
 * an implementation like {@link HoconGlobalConfigurateConfigManager}
 * is suggested
 */
public class GlobalConfigurateConfigManager extends ConfigurateConfigManager implements ConfigManager {
  private boolean sqlEnabled;
  private Map<UUID, WorldConfigurateConfigManager> worldConfigs = new HashMap<>();

  public GlobalConfigurateConfigManager(Path configDir, ConfigLoaderSupplier configLoaderSupplier) {
    super(configDir, "global", configLoaderSupplier);
  }

  @Override
  public void loadExtra() {
    Boolean sqlEnabled = getConfig().getNodeValue(Settings.SQL_ENABLE.getConfigurationPath().get(), TypeToken.of(Settings.SQL_ENABLE.getTypeClass()));
    this.sqlEnabled = sqlEnabled == null ? Settings.SQL_ENABLE.getDefaultValue() : sqlEnabled;
    for (World world : Sponge.getServer().getWorlds()) {
      WorldConfigurateConfigManager worldConfigManager = new WorldConfigurateConfigManager(configDir, world, configLoaderSupplier, this.sqlEnabled);
      worldConfigManager.loadAll();
      worldConfigs.put(world.getUniqueId(), worldConfigManager);
    }
  }

  public void saveExtra() {
    worldConfigs.values().forEach(WorldConfigurateConfigManager::saveAll);
  }

  @Override
  public void removeRegion(UUID worldUUID, String name) {
    this.worldConfigs.get(worldUUID).removeRegion(worldUUID, name);
  }

  /**
   * Load a world.
   * Does nothing if the given world is already loaded
   */
  @Nullable
  public WorldHost loadWorld(World world) {
    return worldConfigs.computeIfAbsent(world.getUniqueId(), k -> {
      WorldConfigurateConfigManager worldConfigManager = new WorldConfigurateConfigManager(configDir, world, configLoaderSupplier, this.sqlEnabled);
      worldConfigManager.loadAll();
      this.worldConfigs.put(world.getUniqueId(), worldConfigManager);
      return worldConfigManager;
    }).getWorldHost();
  }

  /**
   * Fill settings down to the lowest level (Fills worlds, and regions)
   *
   * @param globalHost Host to fill
   */
  public void fillSettings(GlobalHost globalHost) {
    for (Map.Entry<UUID, WorldConfigurateConfigManager> entry : this.worldConfigs.entrySet()) {
      WorldHost worldHost = entry.getValue().getWorldHost();
      fillSettings(worldHost, entry.getValue().getConfig());
      globalHost.addWorld(entry.getKey(), worldHost);
      // Region uses different method of filling
    }
  }

  public boolean isSqlEnabled() {
    return this.sqlEnabled;
  }

  /**
   * Shallow fill the given Host. This means it will only fill
   * the given host, and no sub-hosts, etc.
   *
   * @param host   Host to shallow fill
   * @param source ConfigContainer to get config from
   */
  private void fillSettings(Host host, ConfigContainer<CommentedConfigurationNode> source) {
    if (host instanceof Region) {
      return;
    }
    for (Setting<?> setting : Settings.REGISTRY_MODULE.getByApplicability(host.getApplicability())) {
      setting.getConfigurationPath().ifPresent(path -> setValue(host, path, source, setting));
    }
  }

  public WorldConfigurateConfigManager getWorldConfig(World world) {
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
