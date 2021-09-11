/*
 * MIT License
 *
 * Copyright (c) 2021 MinecraftOnline
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
 *
 */

package com.minecraftonline.nope.common.setting.keys;

import com.minecraftonline.nope.common.setting.SettingKey;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * An abstract class to define a setting key which
 * contains contains a set of values.
 *
 * @param <T> the type which is stored in a set
 */
public abstract class SetSettingKey<T> extends SettingKey<Set<T>> {

  protected SetSettingKey(String id, Set<T> defaultData) {
    super(id, defaultData);
  }

  @Override
  public final Object serializeDataGenerified(Set<T> set) {
    List<Object> objects = new LinkedList<>();
    set.forEach(element -> objects.add(serializeElement(element)));
    return objects;
  }

  protected abstract Object serializeElement(T element);

  @Override
  public final Set<T> deserializeDataGenerified(Object serialized) {
    List<?> objects = (List<?>) serialized;
    Set<T> set = new HashSet<>();
    objects.forEach(object -> set.add(deserializeElement(serialized)));
    return set;
  }

  protected abstract T deserializeElement(Object serialized);

  @NotNull
  @Override
  public final String print(@NotNull Set<T> data) {
    return data.stream()
        .map(this::printElement)
        .sorted()
        .collect(Collectors.joining(", "));
  }

  @NotNull
  public abstract String printElement(T element);
}
