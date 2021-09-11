package com.minecraftonline.nope.common.host;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.struct.Location;

/**
 * Class for managing the single GlobalHost in this HostTree.
 */
public class Universe extends Host {

  public Universe(String name) {
    super(name, -2);
  }

  @Override
  public boolean contains(Location location) {
    return true;
  }

  @Override
  public void save() {
    Nope.instance().data().universe().save(this);
  }
}
