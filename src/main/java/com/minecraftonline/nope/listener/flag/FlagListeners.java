package com.minecraftonline.nope.listener.flag;

import com.google.common.collect.Lists;
import com.minecraftonline.nope.Nope;
import org.spongepowered.api.Sponge;

import java.util.List;

public final class FlagListeners {
  private static final List<FlagListener> FLAG_LISTENERS = Lists.newArrayList(
      new DamageListener()
  );

  public static void registerAll() {
    for (Object obj : FLAG_LISTENERS) {
      Sponge.getEventManager().registerListeners(Nope.getInstance(), obj);
    }
  }
}
