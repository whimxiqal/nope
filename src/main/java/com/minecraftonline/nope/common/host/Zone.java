package com.minecraftonline.nope.common.host;

import com.google.common.collect.Lists;
import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.storage.Destructible;
import com.minecraftonline.nope.common.storage.ZoneDataHandler;
import com.minecraftonline.nope.common.struct.Location;
import com.minecraftonline.nope.common.struct.Volume;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.experimental.Accessors;

public class Zone extends Domained<Domained<?>> implements Destructible {

  @Getter
  @Accessors(fluent = true)
  protected final Domain domain;

  protected final List<Volume> volumes = Lists.newLinkedList();

  public Zone(String name, Domained<?> parent, int priority, Domain domain, Volume... volumes) {
    super(name, parent, priority);
    this.domain = domain;
    this.volumes.addAll(Arrays.asList(volumes));
  }

  public void setPriority(int priority) throws IllegalArgumentException {
    if (priority < 0) {
      throw new IllegalArgumentException("Cannot set a negative priority");
    }
    this.priority = priority;
    this.save();

    // Push the priority of all intersecting zones up by one (perhaps more, recursively)
    domain.zones().intersecting(this, false)
        .stream()
        .filter(other -> this.priority() == other.priority())
        .forEach(zone -> zone.setPriority(this.priority + 1));
  }

  public List<Volume> volumes() {
    return Lists.newArrayList(volumes);
  }

  public Volume remove(int index) {
    Volume volume = volumes.remove(index);
    save();
    return volume;
  }

  public boolean remove(Volume volume) {
    boolean removed = volumes.remove(volume);
    save();
    return removed;
  }

  @Override
  public boolean contains(Location location) {
    return location.getDomain().equals(domain) && volumes.stream().allMatch(volume ->
        volume.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
  }

  @Override
  public void destroy() {
    Nope.instance().data().zones().destroy(this.name());
  }

  @Override
  public void save() {
    Nope.instance().data().zones().save(this);
  }

}
