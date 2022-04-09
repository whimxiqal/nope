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

import java.util.Objects;
import java.util.function.Function;
import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.storage.DomainDataHandler;
import me.pietelite.nope.sponge.config.SettingValueConfigSerializerRegistrar;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ConfigurationLoader;

/**
 * Data handler for persistent storage of {@link Domain} information using Configurate.
 */
public class DomainConfigurateDataHandler
    extends SettingsConfigurateDataHandler implements DomainDataHandler {

  private final Function<String, ConfigurationLoader<CommentedConfigurationNode>> loader;

  /**
   * Generic constructor.
   *
   * @param loader              the configuration loader
   * @param serializerRegistrar serialization registrar
   */
  public DomainConfigurateDataHandler(Function<String, ConfigurationLoader<CommentedConfigurationNode>>
                                          loader,
                                      SettingValueConfigSerializerRegistrar serializerRegistrar) {
    super(serializerRegistrar);
    this.loader = loader;
  }

  @Override
  public void save(@NotNull Domain domain) {
    Objects.requireNonNull(domain);
    try {
      CommentedConfigurationNode root = settingCollectionRoot(domain);
      root.comment("Settings for world " + domain.name());
      loader.apply(domain.name()).save(root);
    } catch (ConfigurateException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void load(@NotNull Domain domain) {
    try {
      CommentedConfigurationNode root = loader.apply(domain.name()).load();
      if (root.node("settings").virtual()) {
        // No settings, so this file was likely not created yet.
        root.node("settings").set(null);
      } else {
        domain.setAll(deserializeSettings(root.node("settings").childrenMap()));
      }
    } catch (ConfigurateException e) {
      e.printStackTrace();
    }
  }

}
