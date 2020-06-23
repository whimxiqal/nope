/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
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
 *
 */

package minecraftonline.nope.permission;

import minecraftonline.nope.util.Validate;

import javax.annotation.Nonnull;

/**
 * A wrapper for permission nodes.
 */
public class Permission {

  private final String perm;

  private Permission(@Nonnull final String perm) {
    this.perm = perm;
  }

  /**
   * Factory method of a {@link Permission}.
   *
   * @param perm the string representation
   * @return generated {@link Permission}
   * @throws IllegalArgumentException if permission is not in config format
   *                                  (kebab case with periods)
   */
  public static Permission of(@Nonnull final String perm) throws IllegalArgumentException {
    Validate.checkConfigFormat(perm,
        "Invalid permission id: "
        + perm
        + ". Valid ids only contain characters 'a-z', '-', and '.'.");
    return new Permission(perm);
  }

  @Nonnull
  public String get() {
    return perm;
  }

  @Override
  public String toString() {
    return perm;
  }

}
