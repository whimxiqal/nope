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

package com.minecraftonline.nope.common.setting;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
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
  public final JsonElement dataToJsonGenerified(Set<T> set) {
    if (set.isEmpty()) {
      return new JsonPrimitive("empty");
    }
    final JsonArray jsonArray = new JsonArray();
    for (T element : set) {
      jsonArray.add(elementToJsonGenerified(element));
    }
    return jsonArray;
  }

  protected abstract JsonElement elementToJsonGenerified(T element);

  @Override
  public final Set<T> dataFromJsonGenerified(JsonElement jsonElement) {
    if (jsonElement.isJsonArray()) {
      return Lists.newLinkedList(jsonElement.getAsJsonArray())
          .stream()
          .map(this::elementFromJsonGenerified)
          .collect(Collectors.toSet());
    } else if (jsonElement.isJsonPrimitive()) {
      if (jsonElement.getAsString().equalsIgnoreCase("empty")) {
        return Sets.newHashSet();
      }
    }
    throw new ParseSettingException("Couldn't deserialize the data from setting " + getId());
  }

  protected abstract T elementFromJsonGenerified(JsonElement jsonElement);

  @NotNull
  @Override
  public final String print(Set<T> data) {
    return data.stream()
        .map(this::printElement)
        .sorted()
        .collect(Collectors.joining(", "));
  }

  @NotNull
  public abstract String printElement(T element);
}
