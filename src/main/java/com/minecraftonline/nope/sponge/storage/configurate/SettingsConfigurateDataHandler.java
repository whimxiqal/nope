package com.minecraftonline.nope.sponge.storage.configurate;

import com.minecraftonline.nope.common.setting.Setting;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingKeys;
import com.minecraftonline.nope.common.setting.Target;
import com.minecraftonline.nope.common.math.Volume;
import com.minecraftonline.nope.sponge.storage.configurate.serializer.VolumeTypeSerializer;
import io.leangen.geantyref.TypeToken;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;

public abstract class SettingsConfigurateDataHandler {

  protected final CommentedConfigurationNode settingCollectionRoot(SettingCollection settings) throws SerializationException {
    CommentedConfigurationNode root = CommentedConfigurationNode.root(ConfigurationOptions.defaults()
        .serializers(builder ->
            builder.register(Volume.class, new VolumeTypeSerializer())));
    root.node("settings").set(serializeSettings(settings));
    return root;
  }

  protected final CommentedConfigurationNode serializeSettings(SettingCollection settings) throws SerializationException {
    CommentedConfigurationNode node = CommentedConfigurationNode.root();
    for (Setting<?> setting : settings.settings()) {
      CommentedConfigurationNode settingNode = node.node(setting.key().id());
      settingNode.comment(setting.key().description());

      if (setting.value() != null) {
        settingNode.node("data").set(setting.key().serializeData(setting.value()));
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

  protected final Collection<Setting<?>> deserializeSettings(List<? extends ConfigurationNode> settingNodes) throws SerializationException {
    List<Setting<?>> settings = new LinkedList<>();
    for (ConfigurationNode settingNode : settingNodes) {
      SettingKey<?> key = SettingKeys.get(settingNode.getString());
      Object data = null;
      if (!settingNode.node("data").virtual()) {
        data = settingNode.node("data").require(key.type());
      }

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
      settings.add(Setting.ofUnchecked(key, data, target));
    }
    return settings;
  }

}
