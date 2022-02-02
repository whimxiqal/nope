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

package com.minecraftonline.nope.common.host;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.math.Volume;
import com.minecraftonline.nope.common.storage.Destructible;
import com.minecraftonline.nope.common.struct.Location;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class Zone extends Host implements Child<Zone>, Destructible {

  protected final List<Volume> volumes = new LinkedList<>();

  @Nullable
  private final Zone parent;

  public Zone(String name, @Nullable Zone parent, int priority, Volume... volumes) {
    this(name, parent, priority, Arrays.asList(volumes));
  }

  public Zone(String name, @Nullable Zone parent, int priority, Collection<Volume> volumes) {
    super(name, priority);
    if (name.startsWith("_")) {
      throw new IllegalArgumentException("A zone name may not start with an underscore");
    }
    this.parent = parent;
    this.volumes.addAll(volumes);
  }

  public void setPriority(int priority) throws IllegalArgumentException {
    if (priority < 0) {
      throw new IllegalArgumentException("Cannot set a negative priority");
    }
    this.priority = priority;
    this.save();
    ensurePriority();
  }

  public void ensurePriority() {
    // Push the priority of all intersecting zones up by one (perhaps more, recursively)
    volumes().forEach(volume -> volume.domain().volumes()
        .intersecting(this)
        .stream()
        .filter(other -> !other.equals(this))
        .filter(other -> this.priority() == other.priority())
        .forEach(zone -> zone.setPriority(this.priority + 1)));
  }

  public List<Volume> volumes() {
    return new LinkedList<>(volumes);
  }

  public Volume remove(int index) {
    Volume volume = volumes.remove(index);
    save();
    volume.domain().volumes().remove(volume, true);
    return volume;
  }

  public boolean remove(Volume volume) {
    boolean removed = volumes.remove(volume);
    save();
    volume.domain().volumes().remove(volume, true);
    return removed;
  }

  @Override
  public Optional<Zone> parent() {
    return Optional.ofNullable(parent);
  }

  @Override
  public boolean contains(Location location) {
    return volumes.stream().anyMatch(volume -> volume.containsPoint(location));
  }

  @Override
  public void destroy() {
    Nope.instance().data().zones().destroy(this);
  }

  @Override
  public void save() {
    Nope.instance().data().zones().save(this);
  }

}
