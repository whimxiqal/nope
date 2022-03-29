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

import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingValue;
import me.pietelite.nope.sponge.api.config.SettingValueConfigSerializer;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

/**
 * The serializer for values stored in {@link SettingKey.Unary}s.
 */
public class UnarySettingValueConfigSerializer
    implements SettingValueConfigSerializer<SettingKey.Manager.Unary<?>> {

  @Override
  @SuppressWarnings("unchecked")
  public Class<SettingKey.Manager.Unary<?>> managerClass() {
    return (Class<SettingKey.Manager.Unary<?>>) (Class<?>) SettingKey.Manager.Unary.class;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <X,
      Y extends SettingValue<X>,
      Z extends SettingKey.Manager<X, Y>> CommentedConfigurationNode serialize(Z managerGeneral,
                                                                               Y valueGeneral)
      throws SerializationException {
    SettingValue.Unary<X> value = (SettingValue.Unary<X>) valueGeneral;
    CommentedConfigurationNode node = CommentedConfigurationNode.root();
    node.set(value.get());
    return node;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <X,
      Y extends SettingValue<X>,
      Z extends SettingKey.Manager<X, Y>> Y deserialize(Z managerGeneral,
                                                        ConfigurationNode configNode)
      throws SerializationException {
    SettingKey.Manager.Unary<X> manager = (SettingKey.Manager.Unary<X>) managerGeneral;
    return (Y) SettingValue.Unary.of(configNode.require(manager.dataType()));
  }
}
