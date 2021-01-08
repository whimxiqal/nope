/*
 * MIT License
 *
 * Copyright (c) 2021 MinecraftOnline
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

package com.minecraftonline.nope.setting;

import lombok.Getter;

import java.util.Map;
import java.util.function.Supplier;

public class Template implements Map.Entry<String, SettingMap> {

  @Getter
  private String name;
  private Supplier<SettingMap> supplier;

  public Template(String name, Supplier<SettingMap> supplier) {
    this.name = name;
    this.supplier = supplier;
  }

  @Override
  public int hashCode() {
    return this.name.hashCode();
  }

  @Override
  public String getKey() {
    return this.name;
  }

  @Override
  public SettingMap getValue() {
    return this.supplier.get();
  }

  @Override
  public SettingMap setValue(SettingMap value) {
    SettingMap prev = this.supplier.get();
    this.supplier = () -> value;
    return prev;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Template && this.name.equals(((Template) obj).name);
  }

  @Override
  public String toString() {
    return name;
  }
}
