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

package me.pietelite.nope.common.util;

import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.NopeServiceProvider;
import me.pietelite.nope.common.api.edit.HostEditor;
import me.pietelite.nope.common.api.edit.ScopeEditor;

/**
 * Utility class for retrieving some information from the API.
 */
public final class ApiUtil {

  public static ScopeEditor editNopeScope() {
    return NopeServiceProvider.service().editSystem().editScope(Nope.NOPE_SCOPE);
  }

  /**
   * Get an editor for a Host.
   *
   * @param name the name of the host
   * @return the host editor
   */
  public static HostEditor editHost(String name) {
    if (name.equals(Nope.GLOBAL_ID)) {
      return NopeServiceProvider.service().editSystem().editGlobal();
    }
    if (Nope.instance().system().domains().containsKey(name)) {
      return NopeServiceProvider.service().editSystem().editDomain(name);
    }
    return editNopeScope().editScene(name);
  }

}
