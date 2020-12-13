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
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.TypeTokens;

import java.nio.file.Path;

@Plugin(id = "nope", dependencies = @Dependency(id = "worldedit"))
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
  private CollisionHandler collisionHandler;

  private PermissionDescription overridePermission;

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
    onLoad();

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

    Sponge.getServiceManager().getRegistration(PermissionService.class).ifPresent(registration -> {
      PermissionService service = registration.getProvider();

      this.overridePermission = service.newDescriptionBuilder(this)
          .id("nope.region.override")
          .description(Text.of("Overrides any region flags that prevent you doing things."))
          .assign(PermissionDescription.ROLE_ADMIN, true)
          .register();
    });

    Sponge.getEventManager().registerListeners(this, regionWandHandler);
    FlagListeners.registerAll();
  }

  public void onLoad() {
    globalConfigManager.loadAll();
    globalConfigManager.fillSettings(globalHost);
    this.regionConfigManager = globalConfigManager;
    regionWandHandler = new RegionWandHandler();
    collisionHandler = new CollisionHandler();
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

  @Listener
  public void reload(GameReloadEvent e) {
    this.globalConfigManager.saveAll();
    globalConfigManager = new HoconGlobalConfigurateConfigManager(configDir);
    // Also reset globalConfigManager, because thats not done in onLoad()
    onLoad();
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

  public CollisionHandler getCollisionHandler() {
    return collisionHandler;
  }

  public ConfigManager getRegionConfigManager() {
    return regionConfigManager;
  }

  public boolean canOverrideRegion(Subject subject) {
    return subject.hasPermission(this.overridePermission.getId());
  }
}
