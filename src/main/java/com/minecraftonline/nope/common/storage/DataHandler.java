package com.minecraftonline.nope.common.storage;

import com.minecraftonline.nope.common.host.HostSystem;

public interface DataHandler {

  UniverseDataHandler universe();

  DomainDataHandler domains();

  ZoneDataHandler zones();

  TemplateDataHandler templates();

  HostSystem loadSystem();

}
