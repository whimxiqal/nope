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

package me.pietelite.nope.common.storage;

import me.pietelite.nope.common.host.Global;
import me.pietelite.nope.common.host.HostSystem;
import me.pietelite.nope.common.host.Scene;

/**
 * A handler for all persistent storage I/O.
 */
public interface DataHandler {

  /**
   * Persistent data storage for the {@link Global}.
   *
   * @return the handler
   */
  UniverseDataHandler universe();

  /**
   * Persistent data storage for the {@link me.pietelite.nope.common.host.Domain}s.
   *
   * @return the handler
   */
  DomainDataHandler domains();

  /**
   * Persistent data storage for the {@link Scene}s.
   *
   * @return the handler
   */
  SceneDataHandler scenes();

  /**
   * Persistent data storage for the {@link me.pietelite.nope.common.host.Profile}s.
   *
   * @return the handler
   */
  ProfileDataHandler profiles();

  /**
   * Persistent data storage for the {@link HostSystem}.
   *
   * @return the handler
   */
  HostSystem loadSystem();

}
