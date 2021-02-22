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
import com.minecraftonline.nope.bridge.collision.CollisionHandler;
import com.minecraftonline.nope.command.common.NopeCommandTree;
import com.minecraftonline.nope.context.ZoneContextCalculator;
import com.minecraftonline.nope.game.listener.StaticSettingListeners;
import com.minecraftonline.nope.game.movement.PlayerMovementHandler;
import com.minecraftonline.nope.host.HoconHostTreeImplStorage;
import com.minecraftonline.nope.host.HostTree;
import com.minecraftonline.nope.key.NopeKeys;
import com.minecraftonline.nope.key.zonewand.ZoneWandHandler;
import com.minecraftonline.nope.key.zonewand.ImmutableZoneWandManipulator;
import com.minecraftonline.nope.key.zonewand.ZoneWandManipulator;
import com.minecraftonline.nope.host.HostTreeImpl;
import com.minecraftonline.nope.game.listener.DynamicSettingListeners;
import com.minecraftonline.nope.setting.SettingLibrary;
import com.minecraftonline.nope.util.Extra;
import lombok.Getter;
import lombok.Setter;
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
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.util.TypeTokens;

import java.nio.file.Path;

@Plugin(id = "nope")
public class Nope {

  public static final String GLOBAL_HOST_NAME = "_global";
  public static final int WORLD_HEIGHT = 256;
  public static final int WORLD_RADIUS = 100000;
  public static String REPO_URL = "https://gitlab.com/minecraftonline/nope/";
  @Getter
  private static Nope instance;
  @Getter
  NopeCommandTree commandTree;
  @Inject
  @Getter
  private Logger logger;
  @Inject
  @Getter
  private PluginContainer pluginContainer;
  @Inject
  @Getter
  @ConfigDir(sharedRoot = false)
  private Path configDir;
  @Getter
  private HostTree hostTree;
  @Getter
  private ZoneWandHandler zoneWandHandler;
  @Getter
  private CollisionHandler collisionHandler;
  @Getter
  private PlayerMovementHandler playerMovementHandler;
  @Getter
  @Setter
  private boolean valid = true;

  @Listener
  public void onPreInitialize(GamePreInitializationEvent event) {
    instance = this;
    SettingLibrary.initialize();
    if (configDir.toFile().mkdirs()) {
      logger.info("Created directories for Nope configuration");
    }
  }

  @Listener
  public void onInit(GameInitializationEvent event) {
    zoneWandHandler = new ZoneWandHandler();
    collisionHandler = new CollisionHandler();
    playerMovementHandler = new PlayerMovementHandler();

    hostTree = new HostTreeImpl(
        new HoconHostTreeImplStorage(),
        Nope.GLOBAL_HOST_NAME,
        s -> "_world-" + s,
        "[a-zA-Z0-9\\-\\.][a-zA-Z0-9_\\-\\.]*");

    NopeKeys.ZONE_WAND = Key.builder()
        .type(TypeTokens.BOOLEAN_VALUE_TOKEN)
        .id("nopezonewand")
        .name("Nope zone wand")
        .query(DataQuery.of("nopezonewand"))
        .build();

    DataRegistration.builder()
        .dataClass(ZoneWandManipulator.class)
        .immutableClass(ImmutableZoneWandManipulator.class)
        .builder(new ZoneWandManipulator.Builder())
        .id("nope-zone-wand")
        .name("Nope zone wand")
        .build();

    Sponge.getEventManager().registerListeners(this, zoneWandHandler);
  }

  @Listener
  public void onServerStart(GameStartedServerEvent event) {
    Extra.printSplashscreen();
    loadState();

    DynamicSettingListeners.register();
    StaticSettingListeners.register();
    playerMovementHandler.register();

    Sponge.getServiceManager()
        .provide(PermissionService.class)
        .ifPresent(service ->
            service.registerContextCalculator(new ZoneContextCalculator()));

    // Register entire Nope command tree
    commandTree = new NopeCommandTree();
    commandTree.register();

  }

  @Listener
  public void onWorldLoad(LoadWorldEvent event) {
    // TODO load additional worlds.
  }

  @Listener
  public void onServerStopping(GameStoppingServerEvent event) {
    saveState();
  }

  @Listener
  public void reload(GameReloadEvent event) {
    loadState();
  }

  public void saveState() {
    try {
      if (isValid()) {
        hostTree.save();
      }
    } catch (Exception e) {
      setValid(false);
      e.printStackTrace();
    }
  }

  public void loadState() {
    try {
      if (isValid()) {
        hostTree.load();
      }
    } catch (Exception e) {
      setValid(false);
      e.printStackTrace();
    }
  }

}
