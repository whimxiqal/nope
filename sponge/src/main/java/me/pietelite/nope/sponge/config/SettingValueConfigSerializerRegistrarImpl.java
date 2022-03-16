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

import java.util.LinkedList;
import java.util.List;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingValue;
import me.pietelite.nope.sponge.api.config.SettingValueConfigSerializer;
import me.pietelite.nope.sponge.api.config.SettingValueConfigSerializerRegistrar;
import org.jetbrains.annotations.NotNull;

public class SettingValueConfigSerializerRegistrarImpl implements SettingValueConfigSerializerRegistrar {

  private final List<SettingValueConfigSerializer<?>> serializers = new LinkedList<>();

  @Override
  public void register(SettingValueConfigSerializer<?> serializer) {
    if (serializers.stream().anyMatch(ser -> ser.managerClass().equals(serializer.managerClass()))) {
      throw new IllegalArgumentException("A serializer already exists of type: " + serializer.getClass().getName());
    }
    this.serializers.add(serializer);
  }

  @Override
  @NotNull
  @SuppressWarnings("unchecked")
  public <T, V extends SettingValue<T>, M extends SettingKey.Manager<T, V>> SettingValueConfigSerializer<M> serializerOf(M manager) {
    try {
      return (SettingValueConfigSerializer<M>) serializers.stream()
          .filter(ser -> ser.managerClass().isInstance(manager))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("No serializer found stored for class " + manager.getClass().getName()));
    } catch (ClassCastException e) {
      throw new IllegalStateException("Serializer was not stored with the proper type associated with class " + manager.getClass().getName(), e);
    }
  }


}
