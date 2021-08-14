package com.minecraftonline.nope.common.host;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.setting.SettingLibrary;
import com.minecraftonline.nope.common.struct.FlexibleHashQueueZoneTree;
import com.minecraftonline.nope.common.struct.Location;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Class for managing the few WorldHosts in this HostTree.
 */
public class Domain extends Domained<Universe> {

  @Getter
  @Accessors(fluent = true)
  private final String id;

  @Getter()
  @Accessors(fluent = true)
  private final ZoneTree zones;

  public Domain(String name, String id, Universe parent) {
    super(name, parent, -1);
    int cacheSize = parent.getDataOrDefault(SettingLibrary.CACHE_SIZE);
    if (cacheSize < 0) {
      throw new RuntimeException("The cache size must be greater than 0");
    } else if (cacheSize == 0) {
      this.zones = new ZoneTree();
    } else {
      FlexibleHashQueueZoneTree flexVolumeTree =
          new FlexibleHashQueueZoneTree(cacheSize);
      Nope.instance().scheduleAsyncIntervalTask(flexVolumeTree::trim, 1, TimeUnit.SECONDS);
      this.zones = flexVolumeTree;
    }

    this.id = id;
  }

  @Override
  public boolean contains(Location location) {
    return this.equals(location.getDomain());
  }

  @Override
  public void save() {
    Nope.instance().data().domains().save(this);
  }
}
