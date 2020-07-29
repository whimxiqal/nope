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

package com.minecraftonline.nope;

import com.google.inject.Inject;
import com.minecraftonline.nope.command.common.NopeCommandTree;
import com.minecraftonline.nope.config.ConfigManager;
import com.minecraftonline.nope.config.configurate.GlobalConfigurateConfigManager;
import com.minecraftonline.nope.config.configurate.hocon.HoconGlobalConfigurateConfigManager;
import com.minecraftonline.nope.control.GlobalHost;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.key.NopeKeys;
import com.minecraftonline.nope.key.regionwand.ImmutableRegionWandManipulator;
import com.minecraftonline.nope.key.regionwand.RegionWandManipulator;
import com.minecraftonline.nope.listener.flag.FlagListeners;
import com.minecraftonline.nope.util.Extra;
import com.minecraftonline.nope.util.Reference;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.TypeTokens;

import java.nio.file.Path;

@Plugin(
    id = Reference.ID,
    name = Reference.NAME,
    description = Reference.DESCRIPTION,
    url = Reference.URL,
    authors = {"PietElite", "tyhdefu", "14mRh4X0r"},
    version = Reference.VERSION,
    dependencies = {@Dependency(id = "worldedit")}
)
public class Nope {

  private static Nope instance;

  // Injections

  @Inject
  private Logger logger;

  @Inject
  private PluginContainer pluginContainer;

  @Inject
  @ConfigDir(sharedRoot = false)
  private Path configDir;

  // Custom fields

  NopeCommandTree commandTree;

  private GlobalConfigurateConfigManager globalConfigManager;
  private ConfigManager regionConfigManager;

  private GlobalHost globalHost = new GlobalHost();

  private RegionWandHandler regionWandHandler;

  @Listener
  public void onPreInitialize(GamePreInitializationEvent event) {
    instance = this;
    Settings.load();
    Extra.printSplashscreen();

    // Load config
    globalConfigManager = new HoconGlobalConfigurateConfigManager(configDir);
  }

  @Listener
  public void onInit(GameInitializationEvent event) {
    globalConfigManager.loadAll();
    globalConfigManager.fillSettings(globalHost);
    this.regionConfigManager = globalConfigManager;

    NopeKeys.REGION_WAND = Key.builder()
        .type(TypeTokens.BOOLEAN_VALUE_TOKEN)
        .id("noperegionwand")
        .name("Nope region wand")
        .query(DataQuery.of("noperegionwand"))
        .build();

    DataRegistration.builder()
        .dataClass(RegionWandManipulator.class)
        .immutableClass(ImmutableRegionWandManipulator.class)
        .builder(new RegionWandManipulator.Builder())
        .id("nope-region-wand")
        .name("Nope region wand")
        .build();

    regionWandHandler = new RegionWandHandler();
    Sponge.getEventManager().registerListeners(this, regionWandHandler);
    FlagListeners.registerAll();
  }

  @Listener
  public void onServerStart(GameStartedServerEvent event) {

    // Register entire Nope command tree
    commandTree = new NopeCommandTree();
    commandTree.register();
  }

  @Listener
  public void onServerStopping(GameStoppingServerEvent event) {
    globalConfigManager.saveAll();
  }

  @Listener
  public void onLoadWorld(LoadWorldEvent event) {
    globalHost.addWorldIfNotPresent(event.getTargetWorld());
  }

  public static Nope getInstance() {
    return instance;
  }

  public Logger getLogger() {
    return logger;
  }

  public GlobalConfigurateConfigManager getGlobalConfigManager() {
    return globalConfigManager;
  }

  public PluginContainer getPluginContainer() {
    return pluginContainer;
  }

  public GlobalHost getGlobalHost() {
    return globalHost;
  }

  public RegionWandHandler getRegionWandHandler() {
    return regionWandHandler;
  }

  public ConfigManager getRegionConfigManager() {
    return regionConfigManager;
  }
}
