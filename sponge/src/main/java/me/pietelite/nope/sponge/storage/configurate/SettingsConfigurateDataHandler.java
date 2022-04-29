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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.setting.Setting;
import me.pietelite.nope.common.setting.SettingCollection;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingValue;
import me.pietelite.nope.common.setting.Target;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.config.SettingValueConfigSerializerRegistrar;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;

/**
 * A data handler for storing {@link Setting}s using Configurate.
 */
public abstract class SettingsConfigurateDataHandler {

  private final SettingValueConfigSerializerRegistrar serializerRegistrar;

  protected SettingsConfigurateDataHandler(SettingValueConfigSerializerRegistrar serializerRegistrar) {
    this.serializerRegistrar = serializerRegistrar;
  }

  protected final CommentedConfigurationNode settingCollectionRoot(SettingCollection settings)
      throws SerializationException {
    CommentedConfigurationNode root = CommentedConfigurationNode.root(ConfigurationOptions.defaults());
    root.node("settings").set(serializeSettings(settings));
    return root;
  }

  protected final CommentedConfigurationNode serializeSettings(SettingCollection settings)
      throws SerializationException {
    CommentedConfigurationNode node = CommentedConfigurationNode.root();
    for (Setting<?, ?> setting : settings.settings()) {
      CommentedConfigurationNode settingNode = node.node(setting.key().id());
      settingNode.comment(setting.key().description());

      if (setting.value() != null) {
        serializeSetting(settingNode, setting);
      }

      CommentedConfigurationNode targetNode = settingNode.node("target").set(Target.class, setting.target());
    }
    return node;
  }

  private <T, V extends SettingValue<T>> void serializeSetting(CommentedConfigurationNode settingNode,
                                                               Setting<T, V> setting)
      throws SerializationException {
    settingNode.node("value")
        .set(serializerRegistrar.serializerOf(setting.key().manager())
            .serialize(setting.key().manager(), setting.value()));
  }

  protected final Collection<Setting<?, ?>> deserializeSettings(
      Map<Object, ? extends ConfigurationNode> settingNodes) throws SerializationException {
    List<Setting<?, ?>> settings = new LinkedList<>();
    for (Map.Entry<Object, ? extends ConfigurationNode> entry : settingNodes.entrySet()) {
      String keyId = Objects.requireNonNull((String) entry.getKey());
      if (Nope.instance().settingKeys().containsId(keyId)) {
        deserializeSetting(entry.getValue(), settings, Nope.instance()
            .settingKeys()
            .get(keyId));
      } else {
        SpongeNope.instance().logger().error("Cannot deserialize setting "
            + keyId
            + " because no setting exists with that ID");
      }
    }
    return settings;
  }

  private <X, Y extends SettingValue<X>> void deserializeSetting(ConfigurationNode settingNode,
                                                                 List<Setting<?, ?>> settings,
                                                                 SettingKey<X, Y, ?> key)
      throws SerializationException {
    Y value = serializerRegistrar.serializerOf(key.manager()).deserialize(key.manager(),
        settingNode.node("value"));
    Target target = settingNode.node("target").get(Target.class);
    settings.add(Setting.ofUnchecked(key, value, target));
  }

}
