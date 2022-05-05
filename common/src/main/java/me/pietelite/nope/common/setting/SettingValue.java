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

package me.pietelite.nope.common.setting;

import java.io.Serializable;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.pietelite.nope.common.api.struct.AltSet;
import org.jetbrains.annotations.NotNull;

/**
 * A value to be stored under a {@link SettingKey} in a {@link Setting}s.
 *
 * @param <T> the data type
 */
public abstract class SettingValue<T> implements Serializable {

  /**
   * A {@link SettingValue} that, when evaluated through the system, returns a single {@link T}.
   *
   * @param <T> the data type
   */
  public static class Unary<T> extends SettingValue<T> implements Serializable {
    private T data;

    /**
     * A static factory method.
     *
     * @param data the data to store in the value
     * @param <X>  the data type
     * @return a new {@link SettingValue.Unary}
     */
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

  /**
   * A {@link SettingValue} that, when evaluated through the system, returns multiple {@link T}.
   *
   * @param <T> the data type
   */
  public abstract static class Poly<T, S extends AltSet<T>>
      extends SettingValue<S> implements Serializable {

    private Poly() {
    }

    /**
     * A static factory method for a "manipulative" setting value.
     * This means that this type will manipulate a value being evaluated by the system
     * and simply add or subtract values from the mutable set, as opposed to the "declarative" type.
     *
     * @param additive    a set of data that will be additive during an evaluation
     * @param subtractive a set of data that will be subtractive during an evaluation
     * @param <X>         the data type
     * @param <Y>         the set of data type
     * @return a new {@link SettingValue.Unary}
     */
    public static <X, Y extends AltSet<X>> Poly<X, Y> manipulative(@NotNull Y additive,
                                                                   @NotNull Y subtractive) {
      return new Manipulative<>(additive, subtractive);
    }

    /**
     * A static factory method for a "declarative" setting value.
     * This means that this type will completely update a value being evaluated by the system,
     * as opposed to the "manipulative" type.
     *
     * @param set a set of data that will replace any current mutable evaluation
     * @param <X> the data type
     * @param <Y> the set of data type
     * @return a new {@link SettingValue.Unary}
     */
    public static <X, Y extends AltSet<X>> Poly<X, Y> declarative(@NotNull Y set) {
      return new Declarative<>(set);
    }

    /**
     * Get whether this a "manipulative" type, versus a "declarative" type.
     *
     * @return true if manipulative
     * @see #manipulative(AltSet, AltSet)
     */
    @SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
    public abstract boolean manipulative();

    /**
     * Get whether this is a "declarative" type, versus "manipulative" type.
     *
     * @return true if declarative
     * @see #declarative(AltSet)
     */
    @SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
    public abstract boolean declarative();

    /**
     * Apply this value to a mutable set.
     *
     * @param set the incoming set
     * @return the resultant set, after passing through this value
     */
    public abstract S applyTo(S set);

    public abstract S additive();

    public abstract S subtractive();

    private static class Declarative<T, S extends AltSet<T>> extends Poly<T, S> {
      private final S set;

      Declarative(S set) {
        this.set = set;
      }

      @Override
      public S applyTo(S set) {
        return this.set;
      }

      @Override
      public S additive() {
        return set;
      }

      @Override
      public S subtractive() {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean declarative() {
        return true;
      }

      @Override
      public boolean manipulative() {
        return false;
      }
    }

    @Getter
    @Accessors(fluent = true)
    private static class Manipulative<T, S extends AltSet<T>> extends Poly<T, S> {
      private final S additive;
      private final S subtractive;

      private Manipulative(S additive, S subtractive) {
        this.additive = additive;
        this.subtractive = subtractive;

        // Cannot have values in both, so remove one from the other
        // For convention's sake, we go with additive terms to keep
        this.subtractive.removeAll(additive);
      }

      @Override
      public S applyTo(S set) {
        set.addAll(this.additive);
        set.removeAll(this.subtractive);
        return set;
      }

      @Override
      public boolean declarative() {
        return false;
      }

      @Override
      public boolean manipulative() {
        return true;
      }
    }

  }

}
