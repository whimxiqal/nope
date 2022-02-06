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

package me.pietelite.nope.common.host;

import me.pietelite.nope.common.setting.SettingCollection;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.struct.Container;
import me.pietelite.nope.common.struct.Location;
import me.pietelite.nope.common.struct.Named;
import me.pietelite.nope.common.util.Validate;

/**
 * A class to store Settings based on graphical locations.
 */
public abstract class Host extends SettingCollection implements Container, Named {

  private final String name;
  protected int priority;

  /**
   * Default constructor.
   *
   * @param name     the name
   * @param priority the priority
   */
  public Host(String name, int priority) {
    if (Validate.invalidSettingCollectionName(name)) {
      throw new IllegalArgumentException("Invalid host name: " + name);
    }
    this.name = name;
    this.priority = priority;
  }

  /**
   * Check if a Location exists within this host.
   *
   * @param location the location
   * @return true if within the host
   */
  public abstract boolean contains(Location location);

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Host && ((Host) obj).name.equals(this.name);
  }

  @Override
  public String name() {
    return name;
  }

  public int priority() {
    return priority;
  }

}
