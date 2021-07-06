package com.minecraftonline.nope.common;

import com.minecraftonline.nope.common.permission.Permission;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

public abstract class Nope {

  public static final String GLOBAL_HOST_NAME = "_global";
  public static final String ZONE_CONFIG_FILENAME = "zones.conf";
  public static final String ZONE_CONFIG_BACKUP_FILENAME = "zones-backup.conf";

  public static final int WORLD_DEPTH = 512;
  public static final int WORLD_RADIUS = 100000;
  public static final int MAX_HOST_COUNT = 100000;
  public static final String REPO_URL = "https://github.com/pietelite/nope/";

  @Getter
  public static Nope instance;

  public final boolean hasPermission(UUID playerUuid, Permission permission) {
    return hasPermission(playerUuid, permission.get());
  }

  public abstract boolean hasPermission(UUID playerUuid, String permission);

  public abstract void scheduleAsyncIntervalTask(Runnable runnable, int interval, TimeUnit intervalUnit);

  public abstract void logError(String message);

  public abstract void logInfo(String message);

}
