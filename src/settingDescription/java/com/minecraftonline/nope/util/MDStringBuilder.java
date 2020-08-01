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
 */

package com.minecraftonline.nope.util;

@SuppressWarnings("unused")
public class MDStringBuilder {
  private StringBuilder builder;

  public MDStringBuilder() {
    builder = new StringBuilder();
  }

  public MDStringBuilder(int capacity) {
    builder = new StringBuilder(capacity);
  }

  /**
   * Appends a title with the following string as text,
   * this has 1 '#' size. Also appends a newline
   * @param s String Title to append
   */
  public void appendTitleLine(String s) {
    appendTitleLine(s, 1);
  }

  /**
   * Appends a title with text S and size size. size is amount of '#'
   * Appends a new line
   * @param s
   * @param size
   */
  public void appendTitleLine(String s, int size) {
    if (length() != 0) append("\n\r");
    for (int i = 0; i < size; i++) {
      append('#');
    }
    append(' ');
    append(s);
    append("\n\r\n\r");
  }

  public void appendBullet(String s) {
    append("* ");
    append(s);
  }

  public String build() {
    return builder.toString();
  }

  // Delegate methods:

  public MDStringBuilder append(Object obj) {
    builder.append(obj);
    return this;
  }

  public MDStringBuilder append(StringBuffer sb) {
    builder.append(sb);
    return this;
  }

  public MDStringBuilder append(CharSequence s) {
    builder.append(s);
    return this;
  }

  public MDStringBuilder append(CharSequence s, int start, int end) {
    builder.append(s, start, end);
    return this;
  }

  public MDStringBuilder append(char[] str) {
    builder.append(str);
    return this;
  }

  public MDStringBuilder append(char[] str, int offset, int len) {
    builder.append(str, offset, len);
    return this;
  }

  public MDStringBuilder append(boolean b) {
    builder.append(b);
    return this;
  }

  public MDStringBuilder append(char c) {
    builder.append(c);
    return this;
  }

  public MDStringBuilder append(int i) {
    builder.append(i);
    return this;
  }

  public MDStringBuilder append(long lng) {
    builder.append(lng);
    return this;
  }

  public MDStringBuilder append(float f) {
    builder.append(f);
    return this;
  }

  public MDStringBuilder append(double d) {
    builder.append(d);
    return this;
  }

  public MDStringBuilder appendCodePoint(int codePoint) {
    builder.appendCodePoint(codePoint);
    return this;
  }

  public MDStringBuilder delete(int start, int end) {
    builder.delete(start, end);
    return this;
  }

  public MDStringBuilder deleteCharAt(int index) {
    builder.deleteCharAt(index);
    return this;
  }

  public MDStringBuilder replace(int start, int end, String str) {
    builder.replace(start, end, str);
    return this;
  }

  public MDStringBuilder insert(int index, char[] str, int offset, int len) {
    builder.insert(index, str, offset, len);
    return this;
  }

  public MDStringBuilder insert(int offset, Object obj) {
    builder.insert(offset, obj);
    return this;
  }

  public MDStringBuilder insert(int offset, String str) {
    builder.insert(offset, str);
    return this;
  }

  public MDStringBuilder insert(int offset, char[] str) {
    builder.insert(offset, str);
    return this;
  }

  public MDStringBuilder insert(int dstOffset, CharSequence s) {
    builder.insert(dstOffset, s);
    return this;
  }

  public MDStringBuilder insert(int dstOffset, CharSequence s, int start, int end) {
    builder.insert(dstOffset, s, start, end);
    return this;
  }

  public MDStringBuilder insert(int offset, boolean b) {
    builder.insert(offset, b);
    return this;
  }

  public MDStringBuilder insert(int offset, char c) {
    builder.insert(offset, c);
    return this;
  }

  public MDStringBuilder insert(int offset, int i) {
    builder.insert(offset, i);
    return this;
  }

  public MDStringBuilder insert(int offset, long l) {
    builder.insert(offset, l);
    return this;
  }

  public MDStringBuilder insert(int offset, float f) {
    builder.insert(offset, f);
    return this;
  }

  public MDStringBuilder insert(int offset, double d) {
    builder.insert(offset, d);
    return this;
  }

  public int indexOf(String str) {
    return builder.indexOf(str);
  }

  public int indexOf(String str, int fromIndex) {
    return builder.indexOf(str, fromIndex);
  }

  public int lastIndexOf(String str) {
    return builder.lastIndexOf(str);
  }

  public int lastIndexOf(String str, int fromIndex) {
    return builder.lastIndexOf(str, fromIndex);
  }

  public StringBuilder reverse() {
    return builder.reverse();
  }

  public int length() {
    return builder.length();
  }

  public int capacity() {
    return builder.capacity();
  }

  public void ensureCapacity(int minimumCapacity) {
    builder.ensureCapacity(minimumCapacity);
  }

  public void trimToSize() {
    builder.trimToSize();
  }

  public void setLength(int newLength) {
    builder.setLength(newLength);
  }

  public char charAt(int index) {
    return builder.charAt(index);
  }

  public int codePointAt(int index) {
    return builder.codePointAt(index);
  }

  public int codePointBefore(int index) {
    return builder.codePointBefore(index);
  }

  public int codePointCount(int beginIndex, int endIndex) {
    return builder.codePointCount(beginIndex, endIndex);
  }

  public int offsetByCodePoints(int index, int codePointOffset) {
    return builder.offsetByCodePoints(index, codePointOffset);
  }

  public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
    builder.getChars(srcBegin, srcEnd, dst, dstBegin);
  }

  public void setCharAt(int index, char ch) {
    builder.setCharAt(index, ch);
  }

  public String substring(int start) {
    return builder.substring(start);
  }

  public CharSequence subSequence(int start, int end) {
    return builder.subSequence(start, end);
  }

  public String substring(int start, int end) {
    return builder.substring(start, end);
  }

  /*public IntStream chars() {
    return builder.chars();
  }

  public IntStream codePoints() {
    return builder.codePoints();
  }*/

  public MDStringBuilder append(String s) {
    builder.append(s);
    return this;
  }
}
