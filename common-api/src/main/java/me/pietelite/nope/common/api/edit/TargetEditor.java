package me.pietelite.nope.common.api.edit;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface TargetEditor {

  Alteration targetAll();

  Alteration targetNone();

  Alteration targetPermission(String permission, boolean value);

  Alteration untargetPermission(String permission);

  Alteration targetPlayer(UUID player);

  Alteration untargetPlayer(UUID player);

  Alteration remove();

  Type playerTargetType();

  Set<UUID> playerSet();

  Map<String, Boolean> permissions();

  enum Type {
    WHITELIST,
    BLACKLIST
  }

}
