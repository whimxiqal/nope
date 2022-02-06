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

import java.util.regex.Pattern;

/**
 * Utility class for validating input.
 */
public final class Validate {

  private Validate() {
  }

  /**
   * Ensures that the input is in the kebab case format.
   *
   * @param name            the input string
   * @throws IllegalArgumentException the exception to throw if fails
   */
  public static boolean invalidSettingCollectionName(String name) throws IllegalArgumentException {
    return !Pattern.compile("^[a-zA-Z0-9\\-()&_]*$").matcher(name).matches();
  }

  /**
   * Ensures that the input is in the kebab case format, but also allows periods.
   *
   * @param s            the input string
   * @throws IllegalArgumentException the exception to throw if fails
   */
  public static void checkPermissionName(String s)
      throws IllegalArgumentException {
    if (!Pattern.compile("^[a-z\\-.]*$").matcher(s).matches()) {
      throw new IllegalArgumentException(
          "Invalid permission id: \""
              + s
              + "\". Valid ids only contain characters 'a-z', '-', and '.'.");
    }
  }

}
