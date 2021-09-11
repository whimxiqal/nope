package com.minecraftonline.nope.common.storage;

import com.minecraftonline.nope.common.host.Domain;
import org.jetbrains.annotations.NotNull;

public interface DomainDataHandler {

  void save(@NotNull Domain domain);

  void load(@NotNull Domain domain);
}
