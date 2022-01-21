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
