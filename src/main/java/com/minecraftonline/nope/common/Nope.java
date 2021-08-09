package com.minecraftonline.nope.common;

import com.minecraftonline.nope.common.permission.Permission;
import com.minecraftonline.nope.common.util.Formatter;
import com.minecraftonline.nope.common.util.Logger;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public abstract class Nope {

  /* CONSTANTS */
  public static final String GLOBAL_HOST_NAME = "_global";
  public static final String ZONE_CONFIG_FILENAME = "zones.conf";
  public static final String ZONE_CONFIG_BACKUP_FILENAME = "zones-backup.conf";

  public static final int WORLD_DEPTH = 512;
  public static final int WORLD_RADIUS = 100000;
  public static final int MAX_HOST_COUNT = 100000;
  public static final String REPO_URL = "https://github.com/pietelite/nope/";

  @Setter
  @Getter
  @Accessors(fluent = true)
  private static Nope instance;

  @Getter
  @Accessors(fluent = true)
  private final Logger logger;

  public final boolean hasPermission(UUID playerUuid, Permission permission) {
    return hasPermission(playerUuid, permission.get());
  }

  public abstract boolean hasPermission(UUID playerUuid, String permission);

  public abstract void scheduleAsyncIntervalTask(Runnable runnable, int interval, TimeUnit intervalUnit);

}
