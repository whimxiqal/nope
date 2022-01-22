package com.minecraftonline.nope.sponge.storage.configurate;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.math.Volume;
import com.minecraftonline.nope.common.setting.Setting;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingValue;
import com.minecraftonline.nope.common.setting.Target;
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
      deserializeSetting(entry.getValue(), settings, Nope.instance()
          .settingKeys()
          .get(Objects.requireNonNull((String) entry.getKey())));
    }
    return settings;
  }

  private <X, Y extends SettingValue<X>> void deserializeSetting(ConfigurationNode settingNode,
                                                                 List<Setting<?, ?>> settings,
                                                                 SettingKey<X, Y> key)
      throws SerializationException {
    // TODO Have to determine how we are going to think about serialization.
    //  On the one hand, we want to make sure that setting key managers can serialize and deserialize their values
    //  accurately and generically, i.e. the methods to do so much be abstract in the manager. This is so that we can
    //  can call serialize and deserialize here without absolutely knowing that we are using Unary and Poly (our own creation)
    //  This is all under the understanding and hope that others can create their own settings, and thus setting types,
    //  although Unary and Poly should really likely be the only ones ever needed.
    //  So, we could just abstractly create a Setting Key Manager abstract method that says "okay, handle this config node"?
    //  Wrong, because we also want to keep any Sponge functionality out of the Sponge package to keep a delineation
    //  between common and implementation-specific things. So, that method cannot exist.
    //  Proposed solution: have a generic value-serializer and value-deserializer in the manager that takes all possible inputs
    //  for any value type (Unary or Poly). No matter what, whether Unary or Poly, there will be data missing
    //  in the places to build the other. As in, the Unary data (one single value) will be given for the Unary value type
    //  but none of the Poly stuff will be given (add, subtract, behavior type), so they will be null.
    //  It will be up to the manager to handle the behavior given these data, so the Unary manager will require the Unary data
    //  and the Poly manager will require the Poly stuff.
    //  Not a great solution... because it's not what these constructors are supposed to do and it inherently blocks off
    //  the ability to create more types of settings because Unary and Poly are the only ones it is is built for.
    //  There needs to be another way using Maps, because we can serialize and deserialize if we know what we are looking
    //  for in them...
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
