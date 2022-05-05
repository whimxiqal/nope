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

/**
 * The central access point for editing anything about the Nope "Host System".
 * The <i>Host System</i> is the entire structure of <code>Host</code>s and <code>Profile</code>s
 * which collectively dictate the value of settings at arbitrary positions throughout a server's many
 * Minecraft worlds.
 */
public interface SystemEditor {

  /**
   * Gets an editor for an existing generic host.
   *
   * @param name the name of the host
   * @return the editor
   * @throws NoSuchElementException if no host exists with that name
   */
  HostEditor editHost(String name) throws NoSuchElementException;

  /**
   * Gets an editor specifically for the "global host".
   * The global host encapsulates all locations on the server.
   *
   * @return the editor
   */
  HostEditor editGlobal();

  /**
   * Gets all domain names.
   *
   * @return the domain set
   */
  Set<String> domains();

  /**
   * Gets an editor for a domain host.
   * Equivalent to {@link #editHost(String)} if called with the name of a domain.
   *
   * @param name the name of the domain
   * @return the editor
   * @throws NoSuchElementException if no domain exists with that name
   */
  HostEditor editDomain(String name) throws NoSuchElementException;

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
   * @throws IllegalArgumentException if there already exists a host with the given name
   */
  void createScene(String name, int priority) throws IllegalArgumentException;

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
   */
  void createProfile(String name);

}
