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

package com.minecraftonline.nope.common.setting;

import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public abstract class SettingValue<T> {

  public static class Unary<T> extends SettingValue<T> {
    private T data;

    public static <X> Unary<X> of(X data) {
      Unary<X> value = new Unary<>();
      value.data = data;
      return value;
    }

    public T get() {
      return data;
    }

    public void set(T value) {
      this.data = value;
    }
  }

  public static class Poly<T> extends SettingValue<Set<T>> {
    private final Set<T> additive = new HashSet<>();
    private final Set<T> subtractive = new HashSet<>();
    private final boolean declarative;

    private Poly(boolean declarative) {
      this.declarative = declarative;
    }

    public static <X> Poly<X> additive(@NotNull Set<X> positive) {
      Poly<X> value = new Poly<>(false);
      value.additive.addAll(positive);
      return value;
    }

    public static <X> Poly<X> negative(@NotNull Set<X> negative) {
      Poly<X> value = new Poly<>(false);
      value.subtractive.addAll(negative);
      return value;
    }

    public static <X> Poly<X> of(@NotNull Set<X> positive, @NotNull Set<X> negative) {
      Poly<X> value = new Poly<>(false);
      value.additive.addAll(positive);
      value.subtractive.addAll(negative);
      return value;
    }

    public static <X> Poly<X> declarative(@NotNull Set<X> set) {
      Poly<X> value = new Poly<>(true);
      value.additive.addAll(set);
      return value;
    }

    public void applyTo(Set<T> set) {
      if (declarative) {
        set.clear();
      } else {
        set.removeAll(this.subtractive);
      }
      set.addAll(this.additive);
    }

  }
}
