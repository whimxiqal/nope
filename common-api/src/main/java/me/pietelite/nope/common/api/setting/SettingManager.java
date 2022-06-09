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

package me.pietelite.nope.common.api.setting;

import me.pietelite.nope.common.api.struct.AltSet;

/**
 * A manager for a setting key. Setting keys are used to set a value that determines the behavior
 * somewhere in the server. That behavior must be handled in one or more custom event handlers,
 * but the evaluation of the setting whenever the value is requested will use Nope's robust
 * inheritance structure. The setting key must first be registered at the beginning of the server's lifecycle.
 *
 * @param <T> the data type for the setting
 * @param <B> the builder type, for constructing and registering a setting key
 */
public interface SettingManager<T, B extends SettingKeyBuilder<T, B>> {

  /**
   * Returns a {@link SettingKeyBuilder} to build a key for settings with {@link T} values.
   *
   * @param id the identifier of the setting
   * @return the builder
   */
  B settingKeyBuilder(String id);

  /**
   * A setting manager for a "unary" type setting key.
   * These types return a single value when evaluated.
   *
   * @param <T> the data type
   */
  interface Unary<T> extends SettingManager<T, SettingKeyBuilder.Unary<T>> {

  }

  /**
   * A setting manager for a "poly" type setting key.
   * These types return multiple values when evaluated.
   *
   * @param <T> the data type
   */
  interface Poly<T> extends SettingManager<AltSet<T>, SettingKeyBuilder.Poly<T>> {

    /**
     * Gets a mutable empty {@link AltSet} of types {@link T}.
     *
     * @return the set
     */
    AltSet<T> emptySet();

    /**
     * Gets a mutable full {@link AltSet} of types {@link T}.
     *
     * @return the set
     */
    AltSet<T> fullSet();

  }

}
