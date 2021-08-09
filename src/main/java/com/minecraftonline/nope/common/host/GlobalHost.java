package com.minecraftonline.nope.common.host;

import com.minecraftonline.nope.common.struct.Location;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.ResourceKey;

/**
 * Class for managing the single GlobalHost in this HostTree.
 */
public class GlobalHost extends Host {

  public GlobalHost(String name) {
    super(name, -2);
    setParent(null);
  }

  @Override
  public void setPriority(int priority) {
    throw new UnsupportedOperationException("You cannot set the priority of the global host!");
  }

  @Override
  public boolean encompasses(Location location) {
    return true;
  }

  @Override
  public @Nullable ResourceKey getWorldKey() {
    return null;
  }
}
