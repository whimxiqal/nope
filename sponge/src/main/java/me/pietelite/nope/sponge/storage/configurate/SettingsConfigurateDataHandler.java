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

import io.leangen.geantyref.TypeToken;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.common.setting.Setting;
import me.pietelite.nope.common.setting.SettingCollection;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingValue;
import me.pietelite.nope.common.setting.Target;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.config.SettingValueConfigSerializerRegistrar;
import me.pietelite.nope.sponge.storage.configurate.serializer.VolumeTypeSerializer;
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
    CommentedConfigurationNode root = CommentedConfigurationNode.root(ConfigurationOptions.defaults()
        .serializers(builder ->
            builder.register(Volume.class, new VolumeTypeSerializer())));
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

      if (setting.target() != null) {
        CommentedConfigurationNode targetNode = settingNode.node("target");
        targetNode.node("permissions").set(setting.requireTarget().permissions());
        if (!setting.requireTarget().users().isEmpty()) {
          CommentedConfigurationNode usersNode;
          if (setting.requireTarget().isWhitelist()) {
            usersNode = targetNode.node("whitelist");
          } else {
            usersNode = targetNode.node("blacklist");
          }
          usersNode.set(setting.requireTarget().users());
          usersNode.comment("whitelist - affects only users listed; "
              + "blacklist - affects everyone other than users listed");
        }
      }
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
    Target target = null;
    if (!settingNode.node("target").virtual()) {
      ConfigurationNode targetNode = settingNode.node("target");
      target = Target.all();
      if (!targetNode.node("permissions").virtual()) {
        target.permissions().putAll(targetNode.node("permissions").childrenMap()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(entry -> (String) entry.getKey(),
                entry -> entry.getValue().getBoolean())));
      }
      if (!targetNode.node("whitelist").virtual()) {
        target.whitelist();
        target.users().addAll(targetNode.node("whitelist").require(new TypeToken<Set<UUID>>() {
        }));
      } else if (!targetNode.node("blacklist").virtual()) {
        target.blacklist();
        target.users().addAll(targetNode.node("blacklist").require(new TypeToken<Set<UUID>>() {
        }));
      }
    }
    settings.add(Setting.ofUnchecked(key, value, target));
  }

}
