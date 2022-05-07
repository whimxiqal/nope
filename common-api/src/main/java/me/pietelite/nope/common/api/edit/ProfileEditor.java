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
import me.pietelite.nope.common.api.struct.Named;

/**
 * An editor for a profile, which is the object that stores settings and therefore
 * defines the functionality for hosts.
 */
public interface ProfileEditor extends Named, NameEditor {

  /**
   * Gets an editor for the target on this whole profile.
   * The target is applied to each of its settings, as long as the setting doesn't already
   * have a target on it and the host which employs this profile at evaluation time doesn't have
   * a target overriding this one.
   *
   * @return the editor
   */
  TargetEditor editTarget();

  /**
   * Gets a generic editor for a specific setting on this profile.
   *
   * @param setting the setting id
   * @return the editor
   * @throws NoSuchElementException if no setting exists with the given id
   * @see #editSingleValueSetting(String, Class)
   * @see #editMultipleValueSetting(String, Class)
   */
  SettingEditor editSetting(String setting) throws NoSuchElementException;

  /**
   * Gets an editor for a single-value type setting.
   *
   * @param setting the setting id
   * @param type    the class type of the element evaluated from this setting
   * @param <T>     the type of element evaluated from this setting
   * @return the editor
   * @throws NoSuchElementException if no setting exists with the given id
   * @see #editMultipleValueSetting(String, Class)
   */
  <T> SingleValueSettingEditor<T> editSingleValueSetting(String setting, Class<T> type) throws NoSuchElementException;

  /**
   * Gets an editor for a multiple-value type setting.
   *
   * @param setting the setting id
   * @param type    the class type of element in the {@link me.pietelite.nope.common.api.struct.AltSet}
   *                evaluated from this setting
   * @param <T>     the type of element in the {@link me.pietelite.nope.common.api.struct.AltSet}
   *                *            evaluated from this setting
   * @return the editor
   * @throws NoSuchElementException if no setting exists with the given id
   * @see #editSingleValueSetting(String, Class)
   */
  <T> MultipleValueSettingEditor<T> editMultipleValueSetting(String setting, Class<T> type) throws NoSuchElementException;

  /**
   * Destroys the profile.
   */
  void destroy();

}
