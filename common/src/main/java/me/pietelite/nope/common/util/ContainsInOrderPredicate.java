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

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A {@link Predicate} that tells whether the tested strings contain letters
 * in the same order as provided in the original constructor's string.
 * <b>The letters do not have to consecutive.</b>
 * This is supposed to allow for more lenient tab-completion.
 */
public class ContainsInOrderPredicate implements Predicate<String> {

  private final Pattern pattern;

  /**
   * Generic constructor.
   * The series of letters in the input string here will be checked against
   * all the strings in the tests to see if those test strings
   * contain these letters in the given order.
   *
   * @param letters the input string
   */
  public ContainsInOrderPredicate(String letters) {
    if (letters.isEmpty()) {
      this.pattern = Pattern.compile(".*");
    } else {
      this.pattern = Pattern.compile(".*" + Arrays.stream(letters.substring(0,
                  Math.min(letters.length(), 32))
              .split(""))
          .map(letter -> "[" + Pattern.quote(letter.toLowerCase() + letter.toUpperCase()) + "]")
          .collect(Collectors.joining(".*"))
          + ".*");
    }
  }

  @Override
  public boolean test(String s) {
    return pattern.matcher(s).matches();
  }

}
