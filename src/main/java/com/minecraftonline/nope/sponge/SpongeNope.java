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
import com.minecraftonline.nope.common.settingnew.SettingKey;
import com.minecraftonline.nope.common.settingnew.SettingKeys;
import com.minecraftonline.nope.common.struct.Location;
import com.minecraftonline.nope.sponge.api.SettingKeyRegistrationEvent;
import com.minecraftonline.nope.sponge.api.SettingListenerRegistrationEvent;
import com.minecraftonline.nope.sponge.command.RootCommand;
import com.minecraftonline.nope.sponge.context.ZoneContextCalculator;
import com.minecraftonline.nope.sponge.key.NopeKeys;
import com.minecraftonline.nope.sponge.listener.dynamic.DynamicHandler;
import com.minecraftonline.nope.sponge.listenernew.NopeSettingListeners;
import com.minecraftonline.nope.sponge.listenernew.SettingListenerStore;
import com.minecraftonline.nope.sponge.mixin.collision.CollisionHandler;
import com.minecraftonline.nope.sponge.storage.yaml.YamlDataHandler;
import com.minecraftonline.nope.sponge.util.Extra;
import com.minecraftonline.nope.sponge.util.SpongeLogger;
import com.minecraftonline.nope.sponge.tool.SelectionHandler;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.*;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryType;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.context.ContextService;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

/**
 * The main class and entrypoint for the entire plugin.
 *
 * @author Pieter Svenson
 */
@Plugin("nope")
public class SpongeNope extends Nope {

  @Getter
  @Accessors(fluent = true)
  private static SpongeNope instance;
  @Getter
  @Accessors(fluent = true)
  private final SelectionHandler selectionHandler = new SelectionHandler();
  @Getter
  @Accessors(fluent = true)
  private SettingListenerStore settingListeners;
  @Getter
  @Accessors(fluent = true)
  private RootCommand rootCommand;
  @Getter
  @Accessors(fluent = true)
  private PluginContainer pluginContainer;
  @Inject
  @Getter
  @ConfigDir(sharedRoot = false)
  private Path configDir;
  @Getter
  private CollisionHandler collisionHandler;
  //  @Getter
//  private PlayerMovementHandler playerMovementHandler;
  @Getter
  @Setter
  private boolean valid = true;

  @Inject
  public SpongeNope(final PluginContainer plugin) {
    super(new SpongeLogger());
    this.pluginContainer = plugin;
  }

  /**
   * Pre-initialize hook.
   *
   * @param event the event
   */
  @Listener
  public void onConstruct(ConstructPluginEvent event) {
    // Set general static variables
    Nope.instance(this);
    instance = this;
    path(configDir);

    if (configDir.toFile().mkdirs()) {
      logger().info("Created directories for Nope configuration");
    }


    // Post setting key and setting listener events
    SettingKeys.registerTo(instance().settingKeys());
    Sponge.eventManager().post(new SettingKeyRegistrationEvent(
        (settingKey) -> {
          instance().settingKeys().register(settingKey);
        },
        event.game(),
        event.cause(),
        event.source(),
        event.context()
    ));
    instance().settingKeys().lock();

    this.settingListeners = new SettingListenerStore(settingKeys());
    NopeSettingListeners.get().forEach(this.settingListeners::register);
    Sponge.eventManager().post(new SettingListenerRegistrationEvent(
        registration -> {
          instance().settingListeners().register(registration);
        },
        event.game(),
        event.cause(),
        event.source(),
        event.context()
    ));


    Sponge.eventManager().registerListeners(this.pluginContainer, selectionHandler);
//    collisionHandler = new CollisionHandler();
//    playerMovementHandler = new PlayerMovementHandler();

    DataRegistration.builder().dataKey(NopeKeys.SELECTION_TOOL_CUBOID).build();
  }

  @Listener
  public void onRegisterDataEvent(RegisterDataEvent event) {
    NopeKeys.SELECTION_TOOL_CUBOID = Key.from(this.pluginContainer, "cuboid_tool", Boolean.class);
    event.register(DataRegistration.of(NopeKeys.SELECTION_TOOL_CUBOID, ItemStack.class));

    NopeKeys.SELECTION_TOOL_CYLINDER = Key.from(this.pluginContainer, "cylinder_tool", Boolean.class);
    event.register(DataRegistration.of(NopeKeys.SELECTION_TOOL_CYLINDER, ItemStack.class));

    NopeKeys.SELECTION_TOOL_SPHERE = Key.from(this.pluginContainer, "sphere_tool", Boolean.class);
    event.register(DataRegistration.of(NopeKeys.SELECTION_TOOL_SPHERE, ItemStack.class));

    NopeKeys.SELECTION_TOOL_SLAB = Key.from(this.pluginContainer, "slab_tool", Boolean.class);
    event.register(DataRegistration.of(NopeKeys.SELECTION_TOOL_SLAB, ItemStack.class));
  }
  /**
   * Nope's server start event hook method.
   *
   * @param event the event
   */
  @Listener
  public void onLoadedGame(LoadedGameEvent event) {
    Extra.printSplashscreen();

    data(new YamlDataHandler(configDir));
    hostSystem(data().loadSystem());
    hostSystem().addAllZones(data().zones().load());

    DynamicHandler.register();
//    StaticSettingListeners.register();

    Sponge.serviceProvider()
        .provide(ContextService.class)
        .ifPresent(service -> service.registerContextCalculator(new ZoneContextCalculator()));

  }

  @Listener
  public void onCommandRegistering(RegisterCommandEvent<Command.Parameterized> event) {
    // Register entire Nope command tree
    this.rootCommand = new RootCommand();
    event.register(this.pluginContainer,
        rootCommand.parameterized(),
        rootCommand.primaryAlias(),
        rootCommand.aliases().size() > 1
            ? rootCommand.aliases().subList(1, rootCommand.aliases().size()).toArray(new String[0])
            : new String[0]);
  }

  @Listener
  public void refresh(RefreshGameEvent event) {
    loadState();
  }

  public void loadState() {
    hostSystem(data().loadSystem());
    hostSystem().addAllZones(data().zones().load());
    templateSet(data().templates().load());
  }

  @Override
  public boolean hasPermission(UUID playerUuid, String permission) {
    return false;
  }

  @Override
  public void scheduleAsyncIntervalTask(Runnable runnable, int interval, TimeUnit intervalUnit) {
    Sponge.asyncScheduler().submit(Task.builder()
        .execute(runnable)
        .interval(interval, intervalUnit)
        .plugin(this.pluginContainer)
        .build());
  }
}
