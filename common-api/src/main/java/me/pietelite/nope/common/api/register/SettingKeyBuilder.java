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

package me.pietelite.nope.common.api.register;

import me.pietelite.nope.common.api.struct.AltSet;

/**
 * A builder for setting keys. Settings use both setting keys and setting values to maintain
 * a certain administrative configuration on the server,
 * and special event listeners may be registered to evaluate a final value under the specified setting key
 * and determine special game behavior dependent on location and permission.
 *
 * @param <T> the data type evaluated at any given location under the setting key
 * @param <B> the builder, for chaining
 */
public interface SettingKeyBuilder<T, B extends SettingKeyBuilder<T, B>> {

  /**
   * Set the value that will be considered "default" and will be used in any corresponding
   * event listeners if no settings dictate any other values should be used.
   *
   * @param defaultData the default data
   * @return the builder, for chaining
   */
  @Required
  B defaultValue(T defaultData);

  /**
   * Set the value that will be considered "natural".
   * The assumption is that if the value is ever set to the natural value,
   * then any corresponding event listeners should have no effect.
   * If no natural value is set, the default value is assumed to be natural.
   *
   * @param naturalData the natural data
   * @return the builder, for chaining
   * @see #defaultValue(Object)
   */
  B naturalValue(T naturalData);

  /**
   * A detailed description of the setting key.
   *
   * @param description the description
   * @return the builder, for chaining
   */
  @Required
  B description(String description);

  /**
   * A short description of the setting key. Keep this to as few words as possible.
   *
   * @param blurb the short description
   * @return the builder, for chaining
   */
  @Required
  B blurb(String blurb);

  /**
   * The category under which the setting key will be listed and sorted.
   *
   * @param category the category
   * @return the builder, for chaining
   */
  B category(SettingCategory category);

  /**
   * Specify that this key is implemented.
   * When a setting listener using this key through Nope, "functional" will automatically be set to true.
   * This should only be specified manually here if the setting listener(s) involving this setting key
   * are registered to the mod platform manually also.
   *
   * @return this builder, for chaining
   */
  B functional();

  /**
   * Specify that this key can only be set for the entire server, globally.
   *
   * @return this builder, for chaining
   */
  B global();

  /**
   * Specify that when a non-default value of this setting is set, it is restrictive for players.
   * This feature is intended to determine when unrestricted-type players should be able to ignore
   * restrictive types of changes to behavior.
   * For example, this plugin should generally not prevent administrators from breaking blocks where
   * breaking blocks is disallowed.
   *
   * @return this builder, for chaining
   */
  B playerRestrictive();

  /**
   * A setting key builder to build keys that are used to return single values.
   *
   * @param <T> the data type evaluated for events
   * @param <B> the builder, for chaining
   */
  interface Unary<T, B extends Unary<T, B>> extends SettingKeyBuilder<T, B> {

  }

  /**
   * A setting key builder to build keys that are used to return multiple values.
   *
   * @param <T> the data type of each element in the set
   * @param <S> the special type of set used in Nope evaluated for events
   * @param <B> the builder, for chaining
   */
  interface Poly<T, S extends AltSet<T>, B extends Poly<T, S, B>> extends SettingKeyBuilder<S, B> {

    /**
     * Set the default value as a full {@link S}.
     * If this is called, {@link #defaultValue} need not be called.
     *
     * @return the builder, for chaining
     * @see #defaultValue(Object)
     */
    B fillDefaultData();

    /**
     * Set the default value as an empty {@link S}.
     * If this is called, {@link #defaultValue} need not be called.
     *
     * @return the builder, for chaining
     * @see #defaultValue(Object)
     */
    B emptyDefaultData();

    /**
     * Set the natural value as a full {@link S}.
     *
     * @return the builder, for chaining
     * @see #naturalValue(Object)
     */
    B fillNaturalData();

    /**
     * Set the natural value as an empty {@link S}.
     *
     * @return the builder, for chaining
     * @see #naturalValue(Object)
     */
    B emptyNaturalData();

  }

}
