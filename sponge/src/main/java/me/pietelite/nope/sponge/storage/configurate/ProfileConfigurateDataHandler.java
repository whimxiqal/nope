/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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

package me.pietelite.nope.sponge.storage.configurate;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Profile;
import me.pietelite.nope.common.storage.ProfileDataHandler;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.config.SettingValueConfigSerializerRegistrar;
import me.pietelite.nope.sponge.storage.configurate.loader.DynamicIndividualConfigurationLoader;
import me.pietelite.nope.sponge.storage.configurate.loader.DynamicIndividualFilePath;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

public class ProfileConfigurateDataHandler extends SettingsConfigurateDataHandler implements ProfileDataHandler {

  private final DynamicIndividualConfigurationLoader loader;
  private final DynamicIndividualFilePath filePath;
  private final Collection<ConfigurationLoader<CommentedConfigurationNode>> allLoaders;

  public ProfileConfigurateDataHandler(DynamicIndividualConfigurationLoader loader,
                                       DynamicIndividualFilePath filePath,
                                       Collection<ConfigurationLoader<CommentedConfigurationNode>> allLoaders,
                                       SettingValueConfigSerializerRegistrar serializerRegistrar) {
    super(serializerRegistrar);
    this.loader = loader;
    this.filePath = filePath;
    this.allLoaders = allLoaders;
  }

  @Override
  public void destroy(Profile profile) {
    File file = filePath.path(profile.scope(), profile.name()).toFile();
    if (file.exists()) {
      if (!file.delete()) {
        SpongeNope.instance().logger().error("Error when trying to destroy profile "
            + profile.name()
            + " by deleting its configuration file");
      }
    }
  }

  @Override
  public void save(Profile profile) {
    try {
      ConfigurationLoader<CommentedConfigurationNode> loader = this.loader.loader(profile.scope(), profile.name());
      CommentedConfigurationNode root = loader.load();
      root.node("scope").set(profile.scope());
      root.node("name").set(profile.name());
      root.node("settings").set(serializeSettings(profile));
      root.node("settings").comment("Settings for Scene " + profile.name());
      loader.save(root);
    } catch (ConfigurateException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Collection<Profile> load() {
    Map<String, Profile> profiles = new HashMap<>();
    for (ConfigurationLoader<CommentedConfigurationNode> loader : allLoaders) {
      ConfigurationNode root;
      String scope;
      String name;
      try {
        root = loader.load();
        scope = root.node("scope").get(String.class);
        name = root.node("name").get(String.class);
      } catch (ConfigurateException e) {
        Nope.instance().logger().error("Error loading Profile: " + e.getMessage());
        e.printStackTrace();
        continue;
      }
      if (name == null) {
        continue;
      }

      try {
        Profile profile = new Profile(scope, name);
        profile.setAll(deserializeSettings(root.node("settings").childrenMap()));
        profiles.put(name, profile);
      } catch (ConfigurateException e) {
        Nope.instance().logger().error(String.format("Error loading Profile %s: " + e.getMessage()
            + ". Is it in the right format?", name));
        e.printStackTrace();
      }
    }
    if (!profiles.containsKey(Nope.GLOBAL_ID)) {
      Profile globalProfile = new Profile(Nope.NOPE_SCOPE, Nope.GLOBAL_ID);
      profiles.put(Nope.GLOBAL_ID, globalProfile);
      save(globalProfile);
    }
    return profiles.values();
  }

}
