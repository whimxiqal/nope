package com.minecraftonline.nope.control;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RegionSet {
  // Sorted with highest priority region last.
  private List<Region> regions;

  public RegionSet(List<Region> regions) {
    this.regions = regions;
    this.regions.sort(Comparator.comparingInt(r -> r.getSettingValueOrDefault(Settings.REGION_PRIORITY)));
  }

  /**
   * Gets the settings from the regions
   * @param setting Setting to look for
   * @param <T> Type of value
   * @return List of values, ordered least to highest priority
   */
  public <T extends Serializable> List<Map.Entry<T, Region>> getSettingValue(Setting<T> setting) {
    List<Map.Entry<T, Region>> values = new ArrayList<>();
    for (Region region : this.regions) {
      region.getSettingValue(setting)
          .ifPresent(val -> values.add(new AbstractMap.SimpleEntry<>(val, region)));
    }
    return values;
  }
}
