/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

package com.minecraftonline.nope.sponge.storage.configurate;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.host.Universe;
import com.minecraftonline.nope.common.storage.UniverseDataHandler;
import com.minecraftonline.nope.sponge.api.config.SettingValueConfigSerializerRegistrar;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ConfigurationLoader;

public class UniverseConfigurateDataHandler extends SettingsConfigurateDataHandler implements UniverseDataHandler {

  private final ConfigurationLoader<CommentedConfigurationNode> loader;

  public UniverseConfigurateDataHandler(ConfigurationLoader<CommentedConfigurationNode> loader,
                                        SettingValueConfigSerializerRegistrar serializerRegistrar) {
    super(serializerRegistrar);
    this.loader = loader;
  }

  @Override
  public void save(Universe universe) {
    try {
      CommentedConfigurationNode root = settingCollectionRoot(universe);
      loader.save(root);
    } catch (ConfigurateException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Universe load() {
    Universe universe = new Universe(Nope.GLOBAL_HOST_NAME);
    try {
      CommentedConfigurationNode node = loader.load();
      if (!node.virtual()) {
        universe.setAll(deserializeSettings(node.node("settings").childrenMap()));
      }
      return universe;
    } catch (ConfigurateException e) {
      e.printStackTrace();
      return null;
    }
  }

}
