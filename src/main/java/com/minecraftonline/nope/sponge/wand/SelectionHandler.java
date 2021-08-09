package com.minecraftonline.nope.sponge.wand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SelectionHandler {

  private final Map<UUID, Selection.Draft> selectionDrafts = new ConcurrentHashMap<>();

  public Selection.Draft draft(UUID playerUuid) {
    return selectionDrafts.computeIfAbsent(playerUuid, uuid -> new Selection.Draft());
  }

}
