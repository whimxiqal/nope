package com.minecraftonline.nope.sponge.storage.configurate;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.math.Volume;
import com.minecraftonline.nope.common.setting.Setting;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingValue;
import com.minecraftonline.nope.common.setting.Target;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.api.config.SettingValueConfigSerializerRegistrar;
import com.minecraftonline.nope.sponge.storage.configurate.serializer.VolumeTypeSerializer;
import io.leangen.geantyref.TypeToken;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;

public abstract class SettingsConfigurateDataHandler {

  private final SettingValueConfigSerializerRegistrar serializerRegistrar;

  protected SettingsConfigurateDataHandler(SettingValueConfigSerializerRegistrar serializerRegistrar) {
    this.serializerRegistrar = serializerRegistrar;
  }

  protected final CommentedConfigurationNode settingCollectionRoot(SettingCollection settings) throws SerializationException {
    CommentedConfigurationNode root = CommentedConfigurationNode.root(ConfigurationOptions.defaults()
        .serializers(builder ->
            builder.register(Volume.class, new VolumeTypeSerializer())));
    root.node("settings").set(serializeSettings(settings));
    return root;
  }

  protected final CommentedConfigurationNode serializeSettings(SettingCollection settings) throws SerializationException {
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
          usersNode.comment("whitelist - affects only users listed; blacklist - affects everyone other than users listed");
        }
      }
    }
    return node;
  }

  private final <T, V extends SettingValue<T>> void serializeSetting(CommentedConfigurationNode settingNode,
                                                                     Setting<T, V> setting)
      throws SerializationException {
    settingNode.node("value")
        .set(serializerRegistrar.serializerOf(setting.key().manager())
            .serialize(setting.key().manager(), setting.value()));
  }

  protected final Collection<Setting<?, ?>> deserializeSettings(Map<Object, ? extends ConfigurationNode> settingNodes) throws SerializationException {
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
    Y value = serializerRegistrar.serializerOf(key.manager()).deserialize(key.manager(), settingNode.node("value"));
    Target target = null;
    if (!settingNode.node("target").virtual()) {
      ConfigurationNode targetNode = settingNode.node("target");
      target = Target.all();
      if (!targetNode.node("permissions").virtual()) {
        target.permissions().putAll(targetNode.node("permissions").require(new TypeToken<Map<? extends String, ? extends Boolean>>() {
        }));
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
