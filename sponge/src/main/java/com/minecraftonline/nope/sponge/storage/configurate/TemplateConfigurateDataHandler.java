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
    // TODO add default templates here
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
