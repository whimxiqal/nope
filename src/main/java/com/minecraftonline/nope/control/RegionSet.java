/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
