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

import com.minecraftonline.nope.control.flags.Flag;
import com.minecraftonline.nope.control.flags.FlagUtil;
import com.minecraftonline.nope.control.flags.Membership;
import org.apache.commons.lang3.tuple.Pair;

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
    this.regions.sort(Comparator.comparingInt(r -> ((Region)r).getSettingValueOrDefault(Settings.REGION_PRIORITY)).reversed());
  }

  /**
   * Check if the given region is contained in this region
   * set. Useful to figure out if a player is changing regions.
   * @param region Region to check
   * @return If the region is part of this region set.
   */
  public boolean containsRegion(Region region) {
    return this.regions.contains(region);
  }

  /**
   * Gets the settings from the regions
   * @param setting com.minecraftonline.nope.setting.Setting to look for
   * @param <T> Type of value
   * @return List of values, ordered highest to least priority
   */
  public <T extends Serializable> List<Map.Entry<T, Region>> getSettingValue(Setting<T> setting) {
    List<Map.Entry<T, Region>> values = new ArrayList<>();
    for (Region region : this.regions) {
      region.getSettingValue(setting)
          .ifPresent(val -> values.add(new AbstractMap.SimpleEntry<>(val, region)));
    }
    return values;
  }

  public <T extends Serializable> Optional<Map.Entry<T, Region>> getHighestPrioritySettingValue(Setting<T> setting) {
    if (regions.size() == 0) return Optional.empty();
    Region region = this.regions.get(this.regions.size() - 1);
    T val = region.getSettingValue(setting).orElse(null);
    return val == null ? Optional.empty() : Optional.of(new AbstractMap.SimpleEntry<>(val, region));
  }

  public <T extends Flag<?>> Optional<Pair<T, Region>> findFirstFlagSettingWithRegion(Setting<T> setting, Membership membership) {
    for (Region region : regions) {
      Optional<T> val = region.getSettingValue(setting);
      if (val.isPresent() && FlagUtil.appliesTo(val.get(), region, membership)) {
        return Optional.of(Pair.of(val.get(), region));
      }
      else if (setting.getParent().isPresent()) {
        return findFirstFlagSettingWithRegion(setting.getParent().get(), membership);
      }
    }
    return Optional.empty();
  }

  /**
   * This is the preferred method, as it is the easiest to use.
   * However, for some special cases it might not be suitable.
   * @param setting com.minecraftonline.nope.setting.Setting to check
   * @param membership Membership level
   * @param <T> Type of flag.
   * @return Flag set in this region or its default.
   */
  public <T extends Flag<?>> T findFirstFlagSettingOrDefault(Setting<T> setting, Membership membership) {
    return findFirstFlagSetting(setting, membership).orElse(setting.getDefaultValue());
  }

  /**
   * Your second best option.
   * Sometimes, you may need to know if there was a flag set, so you can check
   * for other flags instead, such as for build, interact and block-break
   * @param setting com.minecraftonline.nope.setting.Setting to check
   * @param membership Membership level
   * @param <T> Type of flag.
   * @return Flag set in this region or its default.
   */
  public <T extends Flag<?>> Optional<T> findFirstFlagSetting(Setting<T> setting, Membership membership) {
    Optional<T> optFlag = findFirstFlagSettingNoParent(setting, membership);
    if (optFlag.isPresent()) {
      return optFlag;
    }
    // No flag found, look for parent.
    if (setting.getParent().isPresent()) {
      return findFirstFlagSetting(setting.getParent().get(), membership);
    }
    return Optional.empty();
  }

  public <T extends Flag<?>> Optional<T> findFirstFlagSettingNoParent(Setting<T> setting, Membership membership) {
    for (Region region : regions) {
      Optional<T> val = region.getSettingValue(setting);
      if (val.isPresent() && FlagUtil.appliesTo(val.get(), region, membership)) {
        return val;
      }
    }
    return Optional.empty();
  }

  public Optional<Region> getHighestPriorityRegion() {
    if (regions.size() == 0) return Optional.empty();
    return Optional.of(regions.get(0));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RegionSet regionSet = (RegionSet) o;
    // Regions must be equal with == operator, so slight optimisation vs
    // list's .equals can be done
    if (this.regions.size() != regionSet.regions.size()) {
      return false;
    }
    for (int i = 0; i < regions.size(); i++) {
      if (this.regions.get(i) != regionSet.regions.get(i)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return regions.hashCode();
  }
}
