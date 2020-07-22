package com.minecraftonline.nope.arguments;

import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.WorldHost;

public class RegionWrapper {
  private final WorldHost worldHost;
  private final String regionName;
  private final Region region;

  public RegionWrapper(WorldHost worldHost, Region region, String regionName) {
    this.worldHost = worldHost;
    this.region = region;
    this.regionName = regionName;
  }

  public WorldHost getWorldHost() {
    return worldHost;
  }

  public Region getRegion() {
    return region;
  }

  public String getRegionName() {
    return regionName;
  }
}
