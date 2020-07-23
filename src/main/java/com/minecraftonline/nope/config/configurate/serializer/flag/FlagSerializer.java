/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
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

package com.minecraftonline.nope.config.configurate.serializer.flag;

import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.flags.Flag;
import com.minecraftonline.nope.control.flags.FlagUtil;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public class FlagSerializer implements TypeSerializer<Flag> {
  @Nullable
  @Override
  public Flag deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
    if (value.isVirtual()) {
      return null;
    }
    Flag<?> defaultFlag = getDefaultFlag(value);
    Flag<?> newFlag;
    if (defaultFlag.shouldUseSerializeForConfigurate()) {
      String s = value.getString();
      if (s == null) {
        return null;
      }
      newFlag = FlagUtil.makeFlag(defaultFlag, defaultFlag.deserialize(s));
    }
    else {
      Object val = value.getValue(TypeToken.of(defaultFlag.getFlagType()));
      if (val == null) {
        return null;
      }
      newFlag = FlagUtil.makeFlag(defaultFlag, val);
    }
    FlagUtil.deserializeGroup(newFlag, value);
    return newFlag;
  }

  public static Flag<?> getDefaultFlag(ConfigurationNode node) {
    String key = (String) node.getKey();
    if (key == null) {
      throw new IllegalArgumentException("Cannot find default flag for node with no key!");
    }
    Optional<Setting<?>> setting = Settings.REGISTRY_MODULE.getById(key);
    if (!setting.isPresent()) {
      throw new IllegalArgumentException("Setting for flag node did not exist");
    }
    Object obj = setting.get().getDefaultValue();
    if (!(obj instanceof Flag)) {
      throw new IllegalArgumentException("Setting was not for a flag!");
    }
    return (Flag<?>)obj;
  }

  @Override
  public void serialize(@NonNull TypeToken<?> type, @Nullable Flag obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
    if (obj == null) {
      return;
    }
    Flag<?> defaultFlag = getDefaultFlag(value);
    if (defaultFlag.shouldUseSerializeForConfigurate()) {
      value.setValue(serializeFlag(defaultFlag, obj));
    }
    else {
      serializeFlag(obj, value);
    }
    FlagUtil.serializeGroup(obj, value);
  }

  @SuppressWarnings("UnstableApiUsage")
  private static <T> void serializeFlag(Flag<T> flag, ConfigurationNode node) throws ObjectMappingException {
    node.setValue(TypeToken.of(flag.getFlagType()), flag.getValue());
  }

  // If this throws a classcastexception, it means we have a default value, that doesn't have the same type
  // (same generic) as the value. This should never happen
  @SuppressWarnings("unchecked")
  private static <T> String serializeFlag(Flag<T> defaultFlag, Flag<?> toSerialize) {
    return defaultFlag.serialize((Flag<T>) toSerialize);
  }

  public static class CanHandle implements Predicate<TypeToken<?>> {

    @Override
    public boolean test(TypeToken<?> typeToken) {
      return typeToken.getRawType().isAssignableFrom(Flag.class);
    }
  }
}
