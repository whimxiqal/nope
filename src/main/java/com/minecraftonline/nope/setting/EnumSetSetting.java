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

package com.minecraftonline.nope.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumSetSetting<E extends Enum<E>> extends SetSetting<E> {
  private final Class<E> enumClass;

  public EnumSetSetting(String id, Set<E> defaultValue, Class<E> enumClass) {
    super(id, defaultValue);
    this.enumClass = enumClass;
  }

  @Override
  public JsonElement elementToJsonGenerified(E value) {
    return new JsonPrimitive(value.name().toLowerCase());
  }

  @Override
  public E elementFromJsonGenerified(JsonElement jsonElement) {
    return Enum.valueOf(enumClass, jsonElement.getAsString().toUpperCase());
  }

  @Override
  public Set<E> parse(String s) throws ParseSettingException {
    Set<E> set = new HashSet<>();
    for (String token : s.split(SettingLibrary.SET_SPLIT_REGEX)) {
      try {
        set.add(Enum.valueOf(enumClass, token.toUpperCase()));
      } catch (IllegalArgumentException ex) {
        throw new ParseSettingException(token + " is not a valid "
            + enumClass.getSimpleName()
            + " type. "
            + (
            (enumClass.getEnumConstants().length <= 8)
                ? "Allowed types: "
                + Arrays.stream(enumClass.getEnumConstants()).map(e ->
                e.toString().toLowerCase()).collect(Collectors.joining(", "))
                : ""));
      }
    }
    return set;
  }

  @Nonnull
  @Override
  public Text printElement(E element) {
    return Text.of(element.name().toLowerCase());
  }
}
