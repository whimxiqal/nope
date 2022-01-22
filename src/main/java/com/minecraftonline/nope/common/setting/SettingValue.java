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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

public abstract class SettingValue<T> implements Serializable {

  public static class Unary<T> extends SettingValue<T> implements Serializable {
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

  @Accessors(fluent = true)
  public static class Poly<T> extends SettingValue<Set<T>> implements Serializable  {
    private final Set<T> additive = new HashSet<>();
    private final Set<T> subtractive = new HashSet<>();
    @Getter
    private Behavior behavior = Behavior.MANIPULATIVE;

    private Poly() {
    }

    public static <X> Poly<X> additive(@NotNull Set<X> positive) {
      Poly<X> value = new Poly<>();
      value.additive.addAll(positive);
      return value;
    }

    public static <X> Poly<X> negative(@NotNull Set<X> negative) {
      Poly<X> value = new Poly<>();
      value.subtractive.addAll(negative);
      return value;
    }

    public static <X> Poly<X> manipulative(@NotNull Set<X> positive, @NotNull Set<X> negative) {
      Poly<X> value = new Poly<>();
      value.additive.addAll(positive);
      value.subtractive.addAll(negative);

      // Remove any shared values... having them in both places doesn't make sense
      Set<X> sharedValues = new HashSet<>(value.additive);
      sharedValues.retainAll(value.subtractive);
      value.additive.removeAll(sharedValues);
      value.subtractive.removeAll(sharedValues);

      return value;
    }

    public static <X> Poly<X> declarative(@NotNull Set<X> set) {
      Poly<X> value = new Poly<>();
      value.behavior = Behavior.DECLARATIVE;
      value.additive.addAll(set);
      return value;
    }

    public static <X> Poly<X> empty() {
      return new Poly<>();
    }

    public static <X> Poly<X> combine(Poly<X> first, Poly<X> second) {
      if (first.behavior != second.behavior) {
        throw new IllegalArgumentException("The arguments must be of the same declarative type");
      }
      Poly<X> value = new Poly<>();
      value.behavior = first.behavior;
      value.additive.addAll(first.additive);
      value.additive.addAll(second.additive);
      value.subtractive.addAll(first.subtractive);
      value.subtractive.addAll(second.subtractive);
      return value;
    }

    public void applyTo(Set<T> set) {
      if (behavior == Behavior.DECLARATIVE) {
        set.clear();
      } else {
        set.removeAll(this.subtractive);
      }
      set.addAll(this.additive);
    }

    public Set<T> additive() {
      return new HashSet<>(additive);
    }

    public Set<T> subtractive() {
      return new HashSet<>(subtractive);
    }

    public boolean declarative() {
      return behavior == Behavior.DECLARATIVE;
    }

    public boolean manipulative() {
      return behavior == Behavior.MANIPULATIVE;
    }

    public enum Behavior {
      DECLARATIVE,
      MANIPULATIVE
    }

  }
}
