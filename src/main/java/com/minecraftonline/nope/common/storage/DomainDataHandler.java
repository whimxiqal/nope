package com.minecraftonline.nope.common.storage;

import com.minecraftonline.nope.common.host.Domain;

public interface DomainDataHandler {

  void save(Domain domain);

  void load(Domain domain);
}
