package com.minecraftonline.nope.common.host;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.setting.Setting;
import com.minecraftonline.nope.common.struct.Location;
import java.util.stream.Collectors;

/**
 * Class for managing the single GlobalHost in this HostTree.
 */
public class Universe extends Host<Universe> {

  public Universe(String name) {
    super(name, null, -2);
  }

  @Override
  public boolean contains(Location location) {
    return true;
  }

  @Override
  public void save() {
    // Save config
    Nope.instance().config().save(getAll()
        .stream()
        .filter(setting -> setting.key().isGlobal())
        .filter(setting -> setting.data() != null)
        .collect(Collectors.toMap(Setting::key, Setting::requireData)));

    // Save zone
    Nope.instance().data().universe().save(this);
  }
}
