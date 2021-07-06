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
import com.minecraftonline.nope.command.NopeCommandRoot;
import com.minecraftonline.nope.command.common.CommandTree;
import com.minecraftonline.nope.context.ZoneContextCalculator;
import com.minecraftonline.nope.game.listener.DynamicSettingListeners;
import com.minecraftonline.nope.game.listener.StaticSettingListeners;
import com.minecraftonline.nope.game.movement.PlayerMovementHandler;
import com.minecraftonline.nope.host.HoconHostTreeImplStorage;
import com.minecraftonline.nope.host.HostTree;
import com.minecraftonline.nope.host.HostTreeImpl;
import com.minecraftonline.nope.key.NopeKeys;
import com.minecraftonline.nope.key.zonewand.ImmutableZoneWandManipulator;
import com.minecraftonline.nope.key.zonewand.ZoneWandHandler;
import com.minecraftonline.nope.key.zonewand.ZoneWandManipulator;
import com.minecraftonline.nope.setting.SettingLibrary;
import com.minecraftonline.nope.util.Extra;
import com.minecraftonline.nope.util.Format;
import java.io.IOException;
import java.nio.file.Path;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.util.TypeTokens;

/**
 * The main class and entrypoint for the entire plugin.
 */
@Plugin(id = "nope", dependencies = {@Dependency(id = "worldedit", optional = true)})
public class Nope {

  public static final String GLOBAL_HOST_NAME = "_global";
  public static final String ZONE_CONFIG_FILENAME = "zones.conf";
  public static final String ZONE_CONFIG_BACKUP_FILENAME = "zones-backup.conf";

  public static final int WORLD_DEPTH = 512;
  public static final int WORLD_RADIUS = 100000;
  public static final int MAX_HOST_COUNT = 100000;
  public static final String REPO_URL = "https://gitlab.com/minecraftonline/nope/";
  @Getter
  private static Nope instance;
  @Getter
  CommandTree commandTree;
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

  /**
   * Pre-initialize hook.
   *
   * @param event the event
   */
  @Listener
  public void onPreInitialize(GamePreInitializationEvent event) {
    instance = this;
    SettingLibrary.initialize();
    if (configDir.toFile().mkdirs()) {
      logger.info("Created directories for Nope configuration");
    }
  }

  /**
   * On initialization hook.
   *
   * @param event the event
   */
  @Listener
  @SuppressWarnings("UnstableApiUsage")
  public void onInit(GameInitializationEvent event) {
    Extra.printSplashscreen();
    zoneWandHandler = new ZoneWandHandler();
    collisionHandler = new CollisionHandler();
    playerMovementHandler = new PlayerMovementHandler();

    NopeKeys.ZONE_WAND = Key.builder()
        .type(TypeTokens.BOOLEAN_VALUE_TOKEN)
        .id("nopezonewand")
        .name("Nope zone wand")
        .query(ZoneWandManipulator.QUERY)
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

  /**
   * Nope's server start event hook method.
   *
   * @param event the event
   */
  @Listener
  public void onServerStart(GameStartedServerEvent event) {
    loadState();
    saveStateBackup();

    DynamicSettingListeners.register();
    StaticSettingListeners.register();

    Sponge.getServiceManager()
        .provide(PermissionService.class)
        .ifPresent(service ->
            service.registerContextCalculator(new ZoneContextCalculator()));

    // Register entire Nope command tree
    commandTree = new CommandTree(new NopeCommandRoot());
    commandTree.register();

  }

  @Listener
  public void onServerStopping(GameStoppingServerEvent event) {
    saveState();
  }

  @Listener
  public void reload(GameReloadEvent event) {
    loadState();
  }

  /**
   * Save state, which consists of the {@link HostTree}.
   */
  public void saveState() {
    try {
      if (isValid()) {
        hostTree.save(ZONE_CONFIG_FILENAME);
      }
    } catch (Exception e) {
      setValid(false);
      e.printStackTrace();
    }
  }

  /**
   * Saves the state to a backup location.
   */
  public void saveStateBackup() {
    try {
      if (isValid()) {
        hostTree.save(ZONE_CONFIG_BACKUP_FILENAME);
      }
    } catch (Exception e) {
      setValid(false);
      e.printStackTrace();
    }
  }

  /**
   * Loads plugin state from storage, which consists of host
   * information from the {@link HostTree}.
   */
  public void loadState() {
    try {
      if (isValid()) {
        HostTree freshTree = new HostTreeImpl(
            new HoconHostTreeImplStorage(),
            Nope.GLOBAL_HOST_NAME,
            s -> "_world-" + s,
            "[a-zA-Z0-9\\-\\.][a-zA-Z0-9_\\-\\.]*");
        freshTree.load(ZONE_CONFIG_FILENAME);

        // Set or replace the host tree
        this.hostTree = freshTree;
      }
    } catch (IOException e) {
      setValid(false);
      Sponge.getServer().getConsole().sendMessage(Format.error(e.getMessage()));
      e.printStackTrace();
    }
  }

}
