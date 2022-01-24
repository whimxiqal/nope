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

package com.minecraftonline.nope.common.setting.manager;

import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingValue;
import com.minecraftonline.nope.common.struct.Described;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class AllCapsEnumKeyManager<E extends Enum<E> & Described> extends SettingKey.Manager.Unary<E> {

  private final Class<E> clazz;

  public AllCapsEnumKeyManager(Class<E> clazz) {
    this.clazz = clazz;
  }

  @Override
  public Class<E> dataType() throws SettingKey.ParseSettingException {
    return clazz;
  }

  @Override
  public E parseData(String data) throws SettingKey.ParseSettingException {
    return Enum.valueOf(clazz, data.toUpperCase());
  }

  @Override
  public @NotNull Map<String, Object> elementOptions() {
    return Arrays.stream(clazz.getEnumConstants())
        .collect(Collectors.toMap(e -> e.name().toLowerCase(), Described::description));
  }

  @Override
  public @NotNull String printData(@NotNull E value) {
    return value.name().toLowerCase();
  }

}
