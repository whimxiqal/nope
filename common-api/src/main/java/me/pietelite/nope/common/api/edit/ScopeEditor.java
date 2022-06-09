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
import java.util.Set;
import me.pietelite.nope.common.api.struct.Named;

/**
 * An editor for scoped components. Any user-created components must be edited in a {@link ScopeEditor}
 * so those components do not interfere with other ones made from other developers.
 */
public interface ScopeEditor extends Named {

  /**
   * Gets all scene names.
   *
   * @return the scene set
   */
  Set<String> scenes();

  /**
   * Gets an editor for a specific scene.
   *
   * @param name the name of the scene
   * @return the editor
   * @throws NoSuchElementException if no scene exists with that name
   */
  SceneEditor editScene(String name) throws NoSuchElementException;

  /**
   * Creates a scene with the given name and priority.
   *
   * @param name     the name of the scene to create
   * @param priority the priority of the scene
   * @return an editor for this scene
   * @throws IllegalArgumentException if there already exists a host with the given name
   *                                  or the name provided is not valid
   */
  SceneEditor createScene(String name, int priority) throws IllegalArgumentException;

  /**
   * Gets all profile names.
   *
   * @return the profile set
   */
  Set<String> profiles();

  /**
   * Gets an editor for a specific profile.
   *
   * @param name the name of the profile
   * @return the editor
   * @throws NoSuchElementException if no profile exists with that name
   */
  ProfileEditor editProfile(String name) throws NoSuchElementException;

  /**
   * Creates a profile with the given name.
   *
   * @param name the name of the profile
   * @return an editor for this profile
   */
  ProfileEditor createProfile(String name);

}
