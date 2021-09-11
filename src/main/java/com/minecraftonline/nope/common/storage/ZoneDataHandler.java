package com.minecraftonline.nope.common.storage;

import com.minecraftonline.nope.common.host.Domain;
import com.minecraftonline.nope.common.host.Zone;
import java.util.Collection;

public interface ZoneDataHandler {

  void destroy(Zone zone);

  void save(Zone zone);

  Collection<Zone> load();

}
