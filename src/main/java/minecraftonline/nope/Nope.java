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
 *
 */

package minecraftonline.nope;

import com.google.inject.Inject;
import minecraftonline.nope.command.common.NopeCommandTree;
import minecraftonline.nope.config.GlobalConfigManager;
import minecraftonline.nope.config.hocon.HoconGlobalConfigManager;
import minecraftonline.nope.control.GlobalHost;
import minecraftonline.nope.util.Reference;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

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

  private GlobalConfigManager globalConfigManager;

  private GlobalHost globalHost;

  @Listener
  public void onPreInitialize(GamePreInitializationEvent event) {
    instance = this;
  }

  @Listener
  public void onServerStart(GameStartedServerEvent event) {
    // Register entire Nope command tree
    commandTree = new NopeCommandTree();
    commandTree.register();

    // Load config
    globalConfigManager = new HoconGlobalConfigManager(configDir);
    globalConfigManager.loadAll();
  }

  @Listener
  public void onServerStopping(GameStoppingServerEvent event) {
    globalConfigManager.saveAll();
  }

  @Listener
  public void onLoadWorld(LoadWorldEvent event) {
    // Possible that a new world has been created, however at the start we already load all known worlds
    globalHost.addWorldIfNotPresent(event.getTargetWorld());
  }

  public static Nope getInstance() {
    return instance;
  }

  public Logger getLogger() {
    return logger;
  }

  public GlobalConfigManager getGlobalConfigManager() {return globalConfigManager;}

  public PluginContainer getPluginContainer() {
    return pluginContainer;
  }
}
