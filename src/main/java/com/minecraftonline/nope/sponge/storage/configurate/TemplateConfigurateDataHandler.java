package com.minecraftonline.nope.sponge.storage.configurate;

import com.minecraftonline.nope.common.setting.template.Template;
import com.minecraftonline.nope.common.setting.template.TemplateSet;
import com.minecraftonline.nope.common.storage.TemplateDataHandler;
import com.minecraftonline.nope.sponge.api.config.SettingValueConfigSerializerRegistrar;
import java.util.Map;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

public class TemplateConfigurateDataHandler extends SettingsConfigurateDataHandler implements TemplateDataHandler {

  private final ConfigurationLoader<CommentedConfigurationNode> loader;

  public TemplateConfigurateDataHandler(ConfigurationLoader<CommentedConfigurationNode> loader,
                                        SettingValueConfigSerializerRegistrar serializerRegistrar) {
    super(serializerRegistrar);
    this.loader = loader;
  }

  @Override
  public void save(TemplateSet set) {
    CommentedConfigurationNode root = CommentedConfigurationNode.root();
    for (Template template : set.templates()) {
      try {
        root.node(template.name(), "description").set(template.description());
        root.node(template.name(), "settings").set(serializeSettings(template));
      } catch (SerializationException e) {
        e.printStackTrace();
      }
    }
    try {
      loader.save(root);
    } catch (ConfigurateException e) {
      e.printStackTrace();
    }
  }

  @Override
  public TemplateSet load() {
    TemplateSet set = new TemplateSet();
    Template.INITIAL.forEach(set::add);
    try {
      CommentedConfigurationNode root = loader.load();
      for (Map.Entry<Object, CommentedConfigurationNode> entry : root.childrenMap().entrySet()) {
        set.add(new Template(entry.getKey().toString(),
            entry.getValue().node("description").getString("Unknown function"),
            deserializeSettings(entry.getValue().node("settings").childrenMap())));
      }
    } catch (ConfigurateException e) {
      e.printStackTrace();
    }
    return set;
  }
}
