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
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingLibrary;
import com.minecraftonline.nope.common.struct.Location;
import com.minecraftonline.nope.common.util.Formatter;
import com.minecraftonline.nope.sponge.command.NopeCommandRoot;
import com.minecraftonline.nope.sponge.context.ZoneContextCalculator;
import com.minecraftonline.nope.sponge.key.NopeKeys;
import com.minecraftonline.nope.sponge.listener.StaticSettingListeners;
import com.minecraftonline.nope.sponge.listener.dynamic.DynamicSettingListeners;
import com.minecraftonline.nope.sponge.mixin.collision.CollisionHandler;
import com.minecraftonline.nope.sponge.movement.PlayerMovementHandler;
import com.minecraftonline.nope.sponge.util.Extra;
import com.minecraftonline.nope.sponge.util.SpongeFormatter;
import com.minecraftonline.nope.sponge.util.SpongeLogger;
import com.minecraftonline.nope.sponge.wand.SelectionHandler;
import io.leangen.geantyref.TypeToken;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.LoadedGameEvent;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StoppedGameEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.context.ContextService;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

/**
 * The main class and entrypoint for the entire plugin.
 */
@Plugin(value = "nope")
public class SpongeNope extends Nope {

  @Getter
  @Accessors(fluent = true)
  private static SpongeNope instance;
  @Getter
  @Accessors(fluent = true)
  private final Formatter<Component, TextColor> formatter = new SpongeFormatter();
  @Getter
  private final SelectionHandler selectionHandler = new SelectionHandler();
  @Inject
  @Getter
  private PluginContainer pluginContainer;
  @Inject
  @Getter
  @ConfigDir(sharedRoot = false)
  private Path configDir;
  @Getter
  private CollisionHandler collisionHandler;
  @Getter
  private PlayerMovementHandler playerMovementHandler;
  @Getter
  @Setter
  private boolean valid = true;

  public SpongeNope() {
    super(new SpongeLogger());
  }

  public static <V> V calc(@NotNull SettingKey<V> key,
                           @NotNull ServerLocation location) {
    return instance().hostSystem().lookupAnonymous(key, new Location(
        location.blockX(),
        location.blockY(),
        location.blockZ(),
        instance().hostSystem().getDomain(location.worldKey().formatted())
    ));
  }

  public static <V> V calc(@NotNull final SettingKey<V> key,
                           @NotNull final Location location,
                           @NotNull final User user) {
    return instance().hostSystem().lookup(key, user.uniqueId(), location);
  }

  /**
   * Pre-initialize hook.
   *
   * @param event the event
   */
  @Listener
  public void onConstruct(ConstructPluginEvent event) {
    Extra.printSplashscreen();

    // Set general static variables
    Nope.instance(this);
    instance = this;
    path(configDir);

    SettingLibrary.initialize();
    if (configDir.toFile().mkdirs()) {
      logger().info("Created directories for Nope configuration");
    }

    collisionHandler = new CollisionHandler();
    playerMovementHandler = new PlayerMovementHandler();

    NopeKeys.ZONE_WAND = Key.builder()
        .type(new TypeToken<Value<Boolean>>() {
        })
        .key(ResourceKey.builder().namespace("nope").value("zonewand").build())
        .build();

    DataRegistration.builder()
        .dataKey(NopeKeys.ZONE_WAND)
        .build();

  }

  /**
   * Nope's server start event hook method.
   *
   * @param event the event
   */
  @Listener
  public void onLoadedGame(LoadedGameEvent event) {
    loadState();
    saveStateBackup();

    DynamicSettingListeners.register();
    StaticSettingListeners.register();

    Sponge.serviceProvider()
        .provide(ContextService.class)
        .ifPresent(service -> service.registerContextCalculator(new ZoneContextCalculator()));

  }

  @Listener
  public void onCommandRegistering(RegisterCommandEvent<Command.Parameterized> event) {
    // Register entire Nope command tree
    NopeCommandRoot commandRoot = new NopeCommandRoot();
    event.register(this.pluginContainer,
        commandRoot.build(),
        commandRoot.getPrimaryAlias(),
        commandRoot.getAliases().subList(1, commandRoot.getAliases().size()).toArray(new String[0]));
  }

  @Listener
  public void onServerStopping(StoppedGameEvent event) {
    saveState();
  }

  @Listener
  public void refresh(RefreshGameEvent event) {
    loadState();
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
        .build());
  }
}
