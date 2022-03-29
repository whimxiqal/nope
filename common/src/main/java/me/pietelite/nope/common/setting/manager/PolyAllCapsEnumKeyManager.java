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

package me.pietelite.nope.common.setting.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.struct.Described;
import me.pietelite.nope.common.struct.HashAltSet;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link SettingKey.Manager} for {@link SettingKey.Poly}s to handle
 * multiple values retrieved from an enum type value.
 *
 * @param <E> the enum type
 * @param <S> the set of values stored
 */
public class PolyAllCapsEnumKeyManager<E extends Enum<E> & Described, S extends HashAltSet<E>>
    extends SettingKey.Manager.Poly<E, S> {

  private final Class<E> clazz;
  private final Supplier<S> setConstructor;

  /**
   * Generic constructor.
   *
   * @param clazz          the class of enum
   * @param setConstructor the constructor for the special set of values
   */
  public PolyAllCapsEnumKeyManager(Class<E> clazz, Supplier<S> setConstructor) {
    this.clazz = clazz;
    this.setConstructor = setConstructor;
  }

  @Override
  @NotNull
  public String printElement(E element) {
    return element.name().toLowerCase();
  }

  @Override
  public E parseElement(String element) throws SettingKey.ParseSettingException {
    return Enum.valueOf(clazz, element.toUpperCase());
  }

  @Override
  public S createSet() {
    return this.setConstructor.get();
  }

  @Override
  public @NotNull Map<String, Object> elementOptionsWithoutGroups() {
    Map<String, Object> map = new HashMap<>();
    try {
      for (E val : clazz.getEnumConstants()) {
        map.put(val.name().toLowerCase(), val.description());
      }
    } catch (Exception e) {
      e.printStackTrace();
      return map;
    }
    return map;
  }
}
