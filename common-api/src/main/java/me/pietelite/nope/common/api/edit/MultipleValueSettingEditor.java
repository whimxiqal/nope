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

package me.pietelite.nope.common.api.edit;

import java.util.NoSuchElementException;
import me.pietelite.nope.common.api.struct.AltSet;

/**
 * An editor for a setting that evaluates to an {@link AltSet} of values.
 * The value of this setting can either be <i>declarative</i> or <i>manipulative</i>.
 *
 * <p>Being declarative means that if this setting is ever evaluated,
 * no other setting values at lower priority hosts will be considered and only this value will be returned.
 *
 * <p>Being manipulative means that if this setting is ever evaluated,
 * it will alter (manipulate) a changing value before passing this mutable value on down to lesser-priority
 * profiles and hosts. So, this setting will only have a hand in changing the value
 * but never fully defining it.
 *
 * @param <T> the type of element in the evaluated list
 */
public interface MultipleValueSettingEditor<T> extends SettingEditor {

  /**
   * Sets the setting value to "all", so this setting will evaluate to a full set
   * of all possible values. This is a declarative value type.
   */
  void setAll();

  /**
   * Sets the setting value to "none", so this setting will evaluate to an empty set.
   * This is a declarative value type.
   */
  void setNone();

  /**
   * Whether the value on this setting is declarative.
   *
   * @return true if declarative
   */
  boolean isDeclarative();

  /**
   * Get the declarative value on this setting.
   *
   * @return the declarative value
   * @throws NoSuchElementException if this setting is not declarative and therefore has no such value
   */
  AltSet<T> getDeclarative() throws NoSuchElementException;

  /**
   * Set a declarative {@link AltSet} of values.
   *
   * @param values the declarative value
   */
  void setDeclarative(AltSet<T> values);

  /**
   * Whether the value on this setting is manipulative.
   *
   * @return true if manipulative
   */
  boolean isManipulative();

  /**
   * Set a manipulative component to this setting value.
   *
   * @param set  the manipulative {@link AltSet} to define
   * @param type the type of effect this {@link AltSet} will have. If "additive", the {@link AltSet}
   *             will be added to the mutable list during setting evaluation.
   *             Similarly, if "subtractive", the {@link AltSet} will be subtracted from the mutable list.
   */
  void setManipulative(AltSet<T> set, ManipulativeType type);

  /**
   * Get a manipulative component of the value on this setting.
   *
   * @param type the component which to return
   * @return the manipulative component
   * @throws NoSuchElementException if this setting is not manipulative and therefore has no such component
   */
  AltSet<T> getManipulative(ManipulativeType type) throws NoSuchElementException;

  /**
   * A manipulative-type setting has two components: "additive" and "subtractive".
   * The additive component is added to the mutable value during setting evaluation
   * and the subtractive component is subtracted.
   */
  enum ManipulativeType {
    ADDITIVE,
    SUBTRACTIVE
  }

}
