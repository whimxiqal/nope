/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

package com.minecraftonline.nope.common.setting.template;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.setting.Setting;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.struct.Named;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

public class Template extends SettingCollection implements Named {

  @Getter
  @Accessors(fluent = true)
  private final String name;
  @Getter
  @Accessors(fluent = true)
  private final String description;

  public Template(@NotNull String name,
                  @NotNull String description,
                  Setting<?, ?>... settings) {
    this.name = name;
    this.description = description;
    Stream.of(settings).forEach(this::set);
  }

  public Template(@NotNull String name,
                  @NotNull String description,
                  Iterable<Setting<?, ?>> settings) {
    this.name = name;
    this.description = description;
    settings.forEach(this::set);
  }

  @Override
  public int hashCode() {
    return this.name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Template && this.name.equals(((Template) obj).name);
  }

  @Override
  public void save() {
    Nope.instance().data().templates().save(Nope.instance().hostSystem().templates());
  }

}
