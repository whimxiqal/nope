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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.common.storage.Destructible;
import me.pietelite.nope.common.struct.Location;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link Host} that contains all points inside a group of {@link Volume}s.
 * There can be arbitrarily many of them and can be user-created.
 */
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

  /**
   * Set the priority and update the whole system to ensure that priorities
   * do not conflict with each other.
   *
   * @param priority the priority
   * @throws IllegalArgumentException if an invalid priority is given
   */
  public void setPriority(int priority) throws IllegalArgumentException {
    if (priority < 0) {
      throw new IllegalArgumentException("Cannot set a negative priority");
    }
    if (priority >= Integer.MAX_VALUE) {
      this.destroy();
      return;
    }
    this.priority = priority;
    this.save();
    ensurePriority();
  }

  /**
   * Set the priority of each intersecting zone up by one that has the same priority.
   */
  public void ensurePriority() {
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

  /**
   * Remove a {@link Volume}.
   *
   * @param index the index at which to remove the zone
   * @return the removed volume
   */
  public Volume remove(int index) {
    Volume volume = volumes.remove(index);
    save();
    volume.domain().volumes().remove(volume, true);
    return volume;
  }

  /**
   * Remove a {@link Volume}.
   *
   * @param volume the specific volume to move
   * @return true if it was removed
   */
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

  public Zone shallowCopy(String newName) {
    return new Zone(newName, parent, priority, volumes);
  }

}
