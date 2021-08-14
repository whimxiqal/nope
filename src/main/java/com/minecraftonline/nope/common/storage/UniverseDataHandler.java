package com.minecraftonline.nope.common.storage;

import com.minecraftonline.nope.common.host.Universe;

public interface UniverseDataHandler {

  void save(Universe universe);

  Universe load();

}
