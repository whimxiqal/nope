package com.minecraftonline.nope.common.storage;

public interface DataHandler {

  UniverseDataHandler universe();

  DomainDataHandler domains();

  ZoneDataHandler zones();

  TemplateDataHandler templates();

}
