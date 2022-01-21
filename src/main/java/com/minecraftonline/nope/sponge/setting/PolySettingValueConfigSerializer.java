/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Pieter Svenson
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

package com.minecraftonline.nope.sponge.setting;

import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingValue;
import com.minecraftonline.nope.sponge.api.config.SettingValueConfigSerializer;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class PolySettingValueConfigSerializer implements SettingValueConfigSerializer<SettingKey.Manager.Poly<?>> {

  @Override
  public Class<SettingKey.Manager.Poly<?>> managerClass() {
    return (Class<SettingKey.Manager.Poly<?>>) (Class<?>) SettingKey.Manager.Poly.class;
  }

  @Override
  public <X,
      Y extends SettingValue<X>,
      Z extends SettingKey.Manager<X, Y>> CommentedConfigurationNode serialize(Z managerGeneral,
                                                                               Y valueGeneral)
      throws SerializationException {
    SettingKey.Manager.Poly<X> manager = (SettingKey.Manager.Poly<X>) managerGeneral;
    SettingValue.Poly<X> value = (SettingValue.Poly<X>) valueGeneral;
    CommentedConfigurationNode node = CommentedConfigurationNode.root();
    node.node("type").set(value.behavior().name().toLowerCase());
    switch (value.behavior()) {
      case DECLARATIVE:
        node.node("set").setList(String.class, value.additive().stream()
            .map(manager::printElement)
            .collect(Collectors.toList()));
        return node;
      case MANIPULATIVE:
        node.node("adding").setList(String.class, value.additive()
            .stream()
            .map(manager::printElement)
            .collect(Collectors.toList()));
        node.node("subtracting").setList(String.class, value.subtractive()
            .stream()
            .map(manager::printElement)
            .collect(Collectors.toList()));
        return node;
      default:
        throw new IllegalStateException("Unknown behavior type: " + value.behavior());

    }
  }

  @Override
  public <X,
      Y extends SettingValue<X>,
      Z extends SettingKey.Manager<X, Y>> Y deserialize(Z managerGeneral,
                                                        ConfigurationNode configNode)
      throws SerializationException {
    SettingKey.Manager.Poly<X> manager = (SettingKey.Manager.Poly<X>) managerGeneral;
    Map<Object, ? extends ConfigurationNode> children = configNode.childrenMap();
    switch (SettingValue.Poly.Behavior.valueOf(children.get("type").require(String.class).toUpperCase())) {
      case DECLARATIVE:
        return (Y) SettingValue.Poly
            .declarative(Objects.requireNonNull(children.get("set").getList(String.class))
                .stream()
                .map(manager::parseElement)
                .collect(Collectors.toSet()));
      case MANIPULATIVE:
        return (Y) SettingValue.Poly.manipulative(Objects.requireNonNull(children.get("adding")
                    .getList(String.class))
                .stream()
                .map(manager::parseElement)
                .collect(Collectors.toSet()),
            Objects.requireNonNull(children.get("subtracting")
                    .getList(String.class))
                .stream()
                .map(manager::parseElement)
                .collect(Collectors.toSet()));
      default:
        throw new SerializationException("Serialized poly value type could not be read "
            + "(must be DECLARATIVE or MANIPULATIVE)");
    }  }
}
