package com.minecraftonline.nope.common.host;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.struct.Location;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Class for managing the few WorldHosts in this HostTree.
 */
public class Domain extends Host implements Domained {

  @Getter
  @Accessors(fluent = true)
  private final String id;

  @Getter()
  @Accessors(fluent = true)
  private final VolumeTree volumes;

  public Domain(String name, String id, int cacheSize) {
    super(name, -1);
    if (cacheSize < 0) {
      throw new RuntimeException("The cache size must be greater than 0");
    } else if (cacheSize == 0) {
      this.volumes = new VolumeTree();
    } else {
      FlexibleHashQueueVolumeTree flexVolumeTree = new FlexibleHashQueueVolumeTree(cacheSize);
      Nope.instance().scheduleAsyncIntervalTask(flexVolumeTree::trim, 1, TimeUnit.SECONDS);
      this.volumes = flexVolumeTree;
    }

    this.id = id;
  }

  @Override
  public boolean contains(Location location) {
    return this.equals(location.domain());
  }

  @Override
  public void save() {
    Nope.instance().data().domains().save(this);
  }

  @Override
  public Domain domain() {
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    Domain domain = (Domain) o;
    return id.equals(domain.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id);
  }
}
