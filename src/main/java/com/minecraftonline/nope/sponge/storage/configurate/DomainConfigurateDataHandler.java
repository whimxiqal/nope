package com.minecraftonline.nope.sponge.storage.configurate;

import com.minecraftonline.nope.common.host.Domain;
import com.minecraftonline.nope.common.storage.DomainDataHandler;
import com.minecraftonline.nope.sponge.api.config.SettingValueConfigSerializerRegistrar;
import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ConfigurationLoader;

public class DomainConfigurateDataHandler extends SettingsConfigurateDataHandler implements DomainDataHandler {

  private final Function<ResourceKey, ConfigurationLoader<CommentedConfigurationNode>> loader;

  public DomainConfigurateDataHandler(Function<ResourceKey, ConfigurationLoader<CommentedConfigurationNode>> loader,
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
      loader.apply(ResourceKey.resolve(domain.id())).save(root);
    } catch (ConfigurateException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void load(@NotNull Domain domain) {
    try {
      CommentedConfigurationNode root = loader.apply(ResourceKey.resolve(domain.id())).load();
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
