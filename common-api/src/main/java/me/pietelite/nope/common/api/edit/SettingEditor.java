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

/**
 * Editor for a setting on a profile.
 */
public interface SettingEditor {

  /**
   * Whether there is a value set for this setting.
   *
   * @return true if set
   */
  boolean hasValue();

  /**
   * Unset the setting value, if one is set.
   *
   * @return true if a setting value was removed, or false if there was none
   */
  boolean unsetValue();

  /**
   * Whether there is a target on this setting.
   *
   * @return true if there is a target
   */
  boolean hasTarget();

  /**
   * Gets an editor for the target on this setting.
   * If there is no target set on this setting, calling this method will not create one;
   * only operations in the returned {@link TargetEditor} can create a target on this setting.
   *
   * @return the editor
   */
  TargetEditor editTarget();

}
