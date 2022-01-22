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

package com.minecraftonline.nope.sponge.config;

import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingValue;
import com.minecraftonline.nope.sponge.api.config.SettingValueConfigSerializer;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class UnarySettingValueConfigSerializer implements SettingValueConfigSerializer<SettingKey.Manager.Unary<?>> {

  @Override
  public Class<SettingKey.Manager.Unary<?>> managerClass() {
    return (Class<SettingKey.Manager.Unary<?>>) (Class<?>) SettingKey.Manager.Unary.class;
  }

  @Override
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
  public <X,
      Y extends SettingValue<X>,
      Z extends SettingKey.Manager<X, Y>> Y deserialize(Z managerGeneral,
                                                        ConfigurationNode configNode)
      throws SerializationException {
    SettingKey.Manager.Unary<X> manager = (SettingKey.Manager.Unary<X>) managerGeneral;
    return (Y) SettingValue.Unary.of(configNode.require(manager.dataType()));
  }
}
