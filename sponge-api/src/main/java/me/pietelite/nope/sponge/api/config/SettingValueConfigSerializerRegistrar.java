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

package me.pietelite.nope.sponge.api.config;

import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingValue;
import org.jetbrains.annotations.NotNull;

/**
 * A registrar for {@link SettingValueConfigSerializer}s.
 */
public interface SettingValueConfigSerializerRegistrar {

  /**
   * Register a serializer for purpose of serialization of setting values for i/o.
   *
   * @param serializer the serializer
   */
  void register(SettingValueConfigSerializer<?> serializer);

  /**
   * Get the serializer for some {@link me.pietelite.nope.common.setting.SettingKey.Manager}.
   *
   * @param manager the manager
   * @param <T>     the data type provided for a setting
   * @param <V>     the value stored in a setting
   * @param <M>     the manager of a setting key
   * @return the serializer for the data stored by the manager on behalf of its settings
   */
  @NotNull <T,
      V extends SettingValue<T>,
      M extends SettingKey.Manager<T, V>> SettingValueConfigSerializer<M> serializerOf(M manager);

}
