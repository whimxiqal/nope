package com.minecraftonline.nope.sponge.storage.configurate;

import com.minecraftonline.nope.common.host.Domain;
import com.minecraftonline.nope.common.storage.DomainDataHandler;
import java.util.function.Function;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ConfigurationLoader;

public class DomainConfigurateDataHandler extends SettingsConfigurateDataHandler implements DomainDataHandler {

  private final Function<ResourceKey, ConfigurationLoader<CommentedConfigurationNode>> loader;

  public DomainConfigurateDataHandler(Function<ResourceKey, ConfigurationLoader<CommentedConfigurationNode>> loader) {
    this.loader = loader;
  }

  @Override
  public void save(Domain domain) {
    try {
      CommentedConfigurationNode root = settingCollectionRoot(domain);
      root.comment("Settings for world " + domain.name());
      loader.apply(ResourceKey.resolve(domain.id())).save(root);
    } catch (ConfigurateException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void load(Domain domain) {
    try {
      CommentedConfigurationNode root = loader.apply(ResourceKey.resolve(domain.id())).load();
      domain.setAll(deserializeSettings(root.node("settings").childrenList()));
    } catch (ConfigurateException e) {
      e.printStackTrace();
    }
  }

}
