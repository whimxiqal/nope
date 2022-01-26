/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Pieter Svenson
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

package com.minecraftonline.nope.sponge.command.parameters;

import lombok.Getter;
import lombok.experimental.Accessors;

public final class ParameterValueTypes {

  public enum SettingValueAlterType {
    SET("set", "Sets the value directly"),
    SET_NOT("setnot", "Sets all except the given values"),
    CONCATENATE("add", "Adds elements onto the targeted list of values"),
    REMOVE("remove", "Removes elements from the targeted list of values"),
    NONE("none", "Sets an empty list of values"),
    ALL("all", "Sets a full list of values");

    @Getter
    @Accessors(fluent = true)
    private final String command;

    @Getter
    @Accessors(fluent = true)
    private final String description;

    SettingValueAlterType(String command, String description) {
      this.command = command;
      this.description = description;
    }
  }

  private ParameterValueTypes() {
  }
}
