package com.minecraftonline.nope.sponge.storage.configurate;

import com.minecraftonline.nope.common.setting.template.Template;
import com.minecraftonline.nope.common.storage.TemplateDataHandler;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

public class TemplateConfigurateDataHandler extends SettingsConfigurateDataHandler implements TemplateDataHandler {

  private final ConfigurationLoader<CommentedConfigurationNode> loader;

  public TemplateConfigurateDataHandler(ConfigurationLoader<CommentedConfigurationNode> loader) {
    this.loader = loader;
  }

  @Override
  public void save(Collection<Template> templates) {
    CommentedConfigurationNode root = CommentedConfigurationNode.root();
    for (Template template : templates) {
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
  public Collection<Template> load() {
    List<Template> list = new LinkedList<>();
    try {
      CommentedConfigurationNode root = loader.load();
      for (Map.Entry<Object, CommentedConfigurationNode> entry : root.childrenMap().entrySet()) {
        list.add(new Template(entry.getKey().toString(),
            entry.getValue().node("description").getString("Unknown function"),
            deserializeSettings(entry.getValue().node("settings").childrenList())));
      }
    } catch (ConfigurateException e) {
      e.printStackTrace();
    }
    return list;
  }
}
