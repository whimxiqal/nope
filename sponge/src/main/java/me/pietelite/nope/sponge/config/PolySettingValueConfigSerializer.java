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

package me.pietelite.nope.sponge.config;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import me.pietelite.nope.common.api.struct.AltSet;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingValue;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

/**
 * A serializer of setting values for values stored with {@link SettingKey.Poly}s.
 */
public class PolySettingValueConfigSerializer
    implements SettingValueConfigSerializer<SettingKey.Manager.Poly<?, ?>> {

  @Override
  @SuppressWarnings("unchecked")
  public Class<SettingKey.Manager.Poly<?, ?>> managerClass() {
    return (Class<SettingKey.Manager.Poly<?, ?>>) (Class<?>) SettingKey.Manager.Poly.class;
  }

  @Override
  public <X,
      Y extends SettingValue<X>,
      Z extends SettingKey.Manager<X, Y>> CommentedConfigurationNode serialize(Z managerGeneral,
                                                                               Y valueGeneral)
      throws SerializationException {
    return serializePoly(managerGeneral, valueGeneral);
  }

  @SuppressWarnings("unchecked")
  private <X,
      Y extends SettingValue<X>,
      Z extends SettingKey.Manager<X, Y>,
      S extends AltSet<X>> CommentedConfigurationNode serializePoly(Z managerGeneral, Y valueGeneral)
      throws SerializationException {
    SettingKey.Manager.Poly<X, S> manager = (SettingKey.Manager.Poly<X, S>) managerGeneral;
    SettingValue.Poly<X, S> value = (SettingValue.Poly<X, S>) valueGeneral;
    CommentedConfigurationNode node = CommentedConfigurationNode.root();
    node.node("type").set(value.declarative() ? "declarative" : "manipulative");
    if (value.declarative()) {
      node.node("set").set(serializeSet(manager, value.additive()));
      return node;
    } else if (value.manipulative()) {
      node.node("add").set(serializeSet(manager, value.additive()));
      node.node("subtract").set(serializeSet(manager, value.subtractive()));
      return node;
    } else {
      throw new IllegalStateException("Unknown behavior type");
    }
  }

  private <X,
      Z extends SettingKey.Manager.Poly<X, S>,
      S extends AltSet<X>> CommentedConfigurationNode serializeSet(Z manager,
                                                                   S set) throws SerializationException {
    CommentedConfigurationNode node = CommentedConfigurationNode.root();
    node.node("values").setList(String.class, set.set().stream()
        .map(manager::printElement)
        .collect(Collectors.toList()));
    if (set.inverted()) {
      node.node("type").set("all-except");
    } else {
      node.node("type").set("all-of");
    }
    return node;
  }

  @Override
  public <X,
      Y extends SettingValue<X>,
      Z extends SettingKey.Manager<X, Y>> Y deserialize(Z managerGeneral,
                                                        ConfigurationNode configNode)
      throws SerializationException {
    return deserializePoly(managerGeneral, configNode);
  }

  @SuppressWarnings("unchecked")
  private <X,
      Y extends SettingValue<X>,
      Z extends SettingKey.Manager<X, Y>,
      S extends AltSet<X>> Y deserializePoly(Z managerGeneral,
                                             ConfigurationNode configNode)
      throws SerializationException {
    SettingKey.Manager.Poly<X, S> manager = (SettingKey.Manager.Poly<X, S>) managerGeneral;
    Map<Object, ? extends ConfigurationNode> children = configNode.childrenMap();
    String parsedType = children.get("type").require(String.class);
    if (parsedType.equalsIgnoreCase("declarative")) {
      return (Y) SettingValue.Poly.declarative(deserializeSet(manager, children.get("set")));
    } else if (parsedType.equalsIgnoreCase("manipulative")) {
      return (Y) SettingValue.Poly.manipulative(deserializeSet(manager, children.get("add")),
          deserializeSet(manager, children.get("subtract")));
    } else {
      throw new SerializationException("Serialized poly value type could not be read "
          + "(must be 'set' or 'alter')");
    }
  }

  private <X,
      Z extends SettingKey.Manager.Poly<X, S>,
      S extends AltSet<X>> S deserializeSet(Z manager,
                                            ConfigurationNode node) throws SerializationException {
    S set = manager.emptySet();
    Objects.requireNonNull(node.node("values").getList(String.class)).forEach(s ->
        set.add(manager.parseElement(s)));
    String type = Objects.requireNonNull(node.node("type").getString());
    // do nothing if it was "all-of"
    if (type.equalsIgnoreCase("all-except")) {
      set.invert();
    } else {
      throw new SerializationException("Serialized set type could not be read "
          + "(must be 'all-of' or 'all-except')");
    }
    return set;
  }
}
