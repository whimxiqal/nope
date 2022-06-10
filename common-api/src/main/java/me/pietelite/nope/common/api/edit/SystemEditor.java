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
   *
   * @param name the name of the domain
   * @return the editor
   * @throws NoSuchElementException if no domain exists with that name
   */
  HostEditor editDomain(String name) throws NoSuchElementException;

  /**
   * Gets all the scopes. A scope manages ownership of many components that should be
   * managed by only one party. Delineating between components created under one scope
   * versus another is primarily an organizational feature.
   *
   * @return all registered scopes
   */
  Set<String> scopes();

  /**
   * Gets an editor for a specific scope.
   * All user-created components are created within a scope so other scopes
   * don't accidentally edit them.
   * This is so that developers may safely create their scenes, profiles, etc.
   * without worrying that someone else might accidentally overwrite or delete their component.
   *
   * <p>There is no need to "create" a scope; it will be created once an operation in the
   * {@link ScopeEditor} is performed.
   *
   * @param scope the name of the scope
   * @return the editor for the scope
   */
  ScopeEditor editScope(String scope) throws NoSuchElementException;

  /**
   * Registers a new scope, if one wasn't already created.
   * There is no way to unregister a scope, so register a scope only once or a few times per game.
   *
   * @param scope the name of the scope; must be a combination of lowercase letters and hyphens
   * @throws IllegalArgumentException if the name is invalid
   */
  void registerScope(String scope) throws IllegalArgumentException;

}
