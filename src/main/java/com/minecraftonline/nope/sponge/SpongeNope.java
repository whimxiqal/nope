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

package com.minecraftonline.nope.sponge;

import com.google.inject.Inject;
import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.sponge.mixin.collision.CollisionHandler;
import com.minecraftonline.nope.sponge.command.NopeCommandRoot;
import com.minecraftonline.nope.sponge.command.general.CommandTree;
import com.minecraftonline.nope.sponge.context.ZoneContextCalculator;
import com.minecraftonline.nope.sponge.game.listener.DynamicSettingListeners;
import com.minecraftonline.nope.sponge.game.listener.StaticSettingListeners;
import com.minecraftonline.nope.sponge.game.movement.PlayerMovementHandler;
import com.minecraftonline.nope.sponge.host.HoconHostTreeImplStorage;
import com.minecraftonline.nope.common.host.HostTreeAdapter;
import com.minecraftonline.nope.common.host.HostTree;
import com.minecraftonline.nope.sponge.key.NopeKeys;
import com.minecraftonline.nope.sponge.key.zonewand.ImmutableZoneWandManipulator;
import com.minecraftonline.nope.sponge.key.zonewand.ZoneWandHandler;
import com.minecraftonline.nope.sponge.key.zonewand.ZoneWandManipulator;
import com.minecraftonline.nope.common.setting.SettingLibrary;
import com.minecraftonline.nope.sponge.util.Extra;
import com.minecraftonline.nope.sponge.util.Format;
import java.io.IOException;
import java.nio.file.Path;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.plugin.jvm.Plugin;

/**
 * The main class and entrypoint for the entire plugin.
 */
@Plugin(name = "nope", dependencies = {@Dependency(id = "worldedit", optional = true)})
public class SpongeNope extends Nope {

  @Getter
  private static SpongeNope instance;
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
  private HostTreeAdapter hostTreeAdapter;
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
   * Save state, which consists of the {@link HostTreeAdapter}.
   */
  public void saveState() {
    try {
      if (isValid()) {
        hostTreeAdapter.save(ZONE_CONFIG_FILENAME);
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
        hostTreeAdapter.save(ZONE_CONFIG_BACKUP_FILENAME);
      }
    } catch (Exception e) {
      setValid(false);
      e.printStackTrace();
    }
  }

  /**
   * Loads plugin state from storage, which consists of host
   * information from the {@link HostTreeAdapter}.
   */
  public void loadState() {
    try {
      if (isValid()) {
        HostTreeAdapter freshTree = new HostTree(
            new HoconHostTreeImplStorage(),
            SpongeNope.GLOBAL_HOST_NAME,
            s -> "_world-" + s,
            "[a-zA-Z0-9\\-\\.][a-zA-Z0-9_\\-\\.]*");
        freshTree.load(ZONE_CONFIG_FILENAME);

        // Set or replace the host tree
        this.hostTreeAdapter = freshTree;
      }
    } catch (IOException e) {
      setValid(false);
      Sponge.getServer().getConsole().sendMessage(Format.error(e.getMessage()));
      e.printStackTrace();
    }
  }

}
