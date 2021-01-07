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
import com.minecraftonline.nope.context.RegionContextCalculator;
import com.minecraftonline.nope.host.HoconHostTreeImplStorage;
import com.minecraftonline.nope.host.HostTree;
import com.minecraftonline.nope.key.NopeKeys;
import com.minecraftonline.nope.key.regionwand.ImmutableRegionWandManipulator;
import com.minecraftonline.nope.key.regionwand.RegionWandManipulator;
import com.minecraftonline.nope.host.HostTreeImpl;
import com.minecraftonline.nope.listener.DynamicSettingListeners;
import com.minecraftonline.nope.setting.SettingLibrary;
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
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.TypeTokens;

import java.nio.file.Path;

@Plugin(id = "nope")
public class Nope {

  public static final int WORLD_RADIUS = 100000;
  public static final int WORLD_HEIGHT = 256;
  public static String REPO_URL = "https://gitlab.com/minecraftonline/nope/";
  public static final String GLOBAL_HOST_NAME = "!global";
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

  private HostTree hostTree;

  private RegionWandHandler regionWandHandler;
  private CollisionHandler collisionHandler;

  private PermissionDescription overridePermission;

  @Listener
  public void onPreInitialize(GamePreInitializationEvent event) {
    logger.info("Pre-init");
    instance = this;
    SettingLibrary.initialize();
    if (configDir.toFile().mkdirs()) {
      logger.info("Created directories for Nope configuration");
    }
  }

  @Listener
  public void onInit(GameInitializationEvent event) {
    logger.info("Init");
    regionWandHandler = new RegionWandHandler();
    collisionHandler = new CollisionHandler();

    hostTree = new HostTreeImpl(
        new HoconHostTreeImplStorage(),
        Nope.GLOBAL_HOST_NAME,
        s -> "_world-" + s,
        "[a-zA-Z0-9\\-\\.][a-zA-Z0-9_\\-\\.]*");

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
  }

  @Listener
  public void onServerStart(GameStartedServerEvent event) {
    Extra.printSplashscreen();

    hostTree.load(); // Need worlds to have loaded first.
    DynamicSettingListeners.register();
    Sponge.getServiceManager()
        .provide(PermissionService.class)
        .ifPresent(service ->
            service.registerContextCalculator(new RegionContextCalculator()));

    // Register entire Nope command tree
    commandTree = new NopeCommandTree();
    commandTree.register();

  }

  @Listener
  public void onWorldLoad(LoadWorldEvent e) {
    // TODO load additional worlds.
  }

  @Listener
  public void onServerStopping(GameStoppingServerEvent event) {
    hostTree.save();
  }

  @Listener
  public void reload(GameReloadEvent e) {
    // Do not add anything else here or you will break the /nope reload command.
    reload();
  }

  public void reload() {
    hostTree.load();
  }

  public static Nope getInstance() {
    return instance;
  }

  public Logger getLogger() {
    return logger;
  }

  public PluginContainer getPluginContainer() {
    return pluginContainer;
  }

  public RegionWandHandler getRegionWandHandler() {
    return regionWandHandler;
  }

  public CollisionHandler getCollisionHandler() {
    return collisionHandler;
  }

  public HostTree getHostTree() {
    return hostTree;
  }

  public boolean canOverrideRegion(Subject subject) {
    return subject.hasPermission(this.overridePermission.getId());
  }

  public Path getConfigDir() {
    return this.configDir;
  }
}
