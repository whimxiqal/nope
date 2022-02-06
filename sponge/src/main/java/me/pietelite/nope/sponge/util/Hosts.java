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

package me.pietelite.nope.sponge.util;

import java.util.Optional;
import org.spongepowered.api.service.context.Context;

public final class Hosts {

  public static String nameToContextKey(String name) {
    return "nope.host." + name;
  }

  /**
   * Statically convert a {@link Context} key into the name of a host.
   *
   * @param key the context key
   * @return the name of the encoded host
   */
  public static Optional<String> contextKeyToName(String key) {
    if (!isContextKey(key)) {
      return Optional.empty();
    }
    return Optional.of(key.substring(10));
  }

  /**
   * Checks whether the given string is a context key encoding a host's name.
   *
   * @param key the string that may be a key
   * @return true if key
   */
  public static boolean isContextKey(String key) {
    if (key == null || key.length() < 11) {
      return false;
    }
    return key.startsWith("nope.host.");
  }

  private Hosts() {
  }
}
