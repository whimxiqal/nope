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

import java.util.List;
import java.util.NoSuchElementException;

/**
 * An editor of a Host.
 */
public interface HostEditor {

  /**
   * The name of this host.
   *
   * @return the name
   */
  String name();

  /**
   * The list of profiles that dictate the effect this host has on its occupants.
   *
   * @return the profiles
   * @see SystemEditor#editProfile(String)
   * @see ProfileEditor
   */
  List<String> profiles();

  /**
   * Add a profile to this host, thus partially dictating the effect this host has on its occupants.
   *
   * @param name  the name of the profile to add
   * @param index the index in the list at which to insert the profile.
   *              It should be the size of the list of profiles to insert it at the back.
   * @throws IndexOutOfBoundsException if the index is less than 0 or greater than the size of the list
   * @throws NoSuchElementException    if there isn't a profile with the given name
   * @throws IllegalArgumentException  if the given profile is already present in the list
   */
  void addProfile(String name, int index)
      throws IndexOutOfBoundsException, NoSuchElementException, IllegalArgumentException;

  /**
   * Removes a profile from this list by name, thus removing the profiles' effects
   * from this host's occupants.
   *
   * @param name the name of the profile to remove
   * @throws IllegalArgumentException if the profile cannot be removed
   */
  void removeProfile(String name) throws NoSuchElementException, IllegalArgumentException;

  /**
   * Removes a profile from this list by index, thus removing the profiles' effects
   * from this host's occupants.
   *
   * @param index the index in the list of profiles at which to remove the profile
   * @throws IndexOutOfBoundsException if the index does not fit within the size of the list
   * @throws IllegalArgumentException  if the profile cannot be removed
   */
  void removeProfile(int index) throws IndexOutOfBoundsException, IllegalArgumentException;

  /**
   * Whether there is a target for this host set on the profile at the given index.
   *
   * @param index the index
   * @return true if a target has been set
   * @throws IndexOutOfBoundsException if the index does not fit within the size of the list
   */
  boolean hasTarget(int index) throws IndexOutOfBoundsException;

  /**
   * Gets an editor for the target for this host set on the profile with the given name.
   *
   * @param name the name of the profile on which the edited target resides
   * @return the editor
   * @throws NoSuchElementException   if no profile exists with that name
   * @throws IllegalArgumentException if you may not edit the target on the given profile
   */
  TargetEditor editTarget(String name) throws NoSuchElementException, IllegalArgumentException;

  /**
   * Gets an editor for the target for this host set on the profile at the given index.
   *
   * @param index the index of the profile in the profile list
   * @return the editor
   * @throws IndexOutOfBoundsException if the index does not fit within the size of the list
   * @throws IllegalArgumentException  if you may not edit the target on the profile at that index
   */
  TargetEditor editTarget(int index) throws IndexOutOfBoundsException, IllegalArgumentException;

  /**
   * The priority of the host. If a host has a higher priority than another, its profiles' settings'
   * values will be favored. 
   * Only scene's priorities may be altered.
   *
   * @return the priority
   * @see SystemEditor#editScene(String) 
   * @see SceneEditor
   */
  int priority();

}
