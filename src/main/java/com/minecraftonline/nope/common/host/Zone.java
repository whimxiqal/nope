package com.minecraftonline.nope.common.host;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.storage.Destructible;
import com.minecraftonline.nope.common.struct.Location;
import com.minecraftonline.nope.common.math.Volume;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class Zone extends Host implements Child<Zone>, Destructible {

  protected final List<Volume> volumes = Lists.newLinkedList();

  @Nullable
  private final Zone parent;

  public Zone(String name, @Nullable Zone parent, int priority, Volume... volumes) {
    this(name, parent, priority, Arrays.asList(volumes));
  }

  public Zone(String name, @Nullable Zone parent, int priority, Collection<Volume> volumes) {
    super(name, priority);
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
    return ImmutableList.copyOf(volumes);
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
