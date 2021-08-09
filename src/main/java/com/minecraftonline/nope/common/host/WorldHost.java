package com.minecraftonline.nope.common.host;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.struct.FlexibleHashQueueVolumeTree;
import com.minecraftonline.nope.common.struct.Location;
import com.minecraftonline.nope.common.struct.VolumeTree;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Class for managing the few WorldHosts in this HostTree.
 */
public class WorldHost extends Host {

  @Getter
  private final UUID worldUuid;
  @Getter(AccessLevel.PUBLIC)
  private final VolumeTree<String, HostTree.Zone> zoneTree;

  public WorldHost(String name, UUID worldUuid, int cacheSize, GlobalHost parent) {
    super(name, -1);
    if (cacheSize < 0) {
      throw new RuntimeException("The cache size must be greater than 0");
    } else if (cacheSize == 0) {
      this.zoneTree = new VolumeTree<>();
    } else {
      FlexibleHashQueueVolumeTree<String, HostTree.Zone> flexVolumeTree =
          new FlexibleHashQueueVolumeTree<>(cacheSize);
      Nope.instance().scheduleAsyncIntervalTask(flexVolumeTree::trim, 1, TimeUnit.SECONDS);
      this.zoneTree = flexVolumeTree;
    }

    this.worldUuid = worldUuid;
    setParent(parent);
  }

  @Override
  public boolean encompasses(Location location) {
    return location.getWorldUuid().equals(this.worldUuid);
  }

  @Override
  public void setPriority(int priority) {
    throw new UnsupportedOperationException("You cannot set the priority of a WorldHost!");
  }

}
