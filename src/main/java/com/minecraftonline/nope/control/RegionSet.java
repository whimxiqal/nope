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

  public <T extends Serializable> Optional<Map.Entry<T, Region>> getHighestPrioritySettingValue(Setting<T> setting) {
    if (regions.size() == 0) return Optional.empty();
    Region region = this.regions.get(this.regions.size() - 1);
    T val = region.getSettingValue(setting).orElse(null);
    return val == null ? Optional.empty() : Optional.of(new AbstractMap.SimpleEntry<>(val, region));
  }

  /**
   * Finds the first flag that is applicable
   * @param setting Flag Setting to find value for
   * @param isMember whether the caller is a member
   * @param isOwner whether the caller is an owner
   * @param <T> The type of setting
   * @return Optional of Map.Entry of T and Region
   * @deprecated use {@link #findFirstFlagSetting(Setting, Membership)}
   */
  @Deprecated
  public <T extends Flag<?>> Optional<Map.Entry<T, Region>> findFirstApplicableFlagSetting(Setting<T> setting, boolean isMember, boolean isOwner) {
    for (int i = regions.size() - 1; i >= 0; i--) {
      Region region = regions.get(i);
      Optional<T> val = region.getSettingValue(setting);
      if (val.isPresent() && FlagUtil.appliesTo(val.get(), isOwner, isMember)) {
        return Optional.of(new AbstractMap.SimpleEntry<>(val.get(), region));
      }
    }
    return Optional.empty();
  }

  public <T extends Flag<?>> Optional<Pair<T, Region>> findFirstFlagSettingWithRegion(Setting<T> setting, Membership membership) {
    for (int i = regions.size() - 1; i >= 0; i--) {
      Region region = regions.get(i);
      Optional<T> val = region.getSettingValue(setting);
      if (val.isPresent() && FlagUtil.appliesTo(val.get(), region, membership)) {
        return Optional.of(Pair.of(val.get(), region));
      }
    }
    return Optional.empty();
  }

  public <T extends Flag<?>> Optional<T> findFirstFlagSetting(Setting<T> setting, Membership membership) {
    return findFirstFlagSettingWithRegion(setting, membership).map(Pair::getKey);
  }

  public <T extends Flag<?>> T findFirstFlagSettingOrDefault(Setting<T> setting, Membership membership) {
    return findFirstFlagSetting(setting, membership).orElse(setting.getDefaultValue());
  }

  @Deprecated
  public <T extends Flag<?>> T findFirstApplicableFlagSettingOrDefault(Setting<T> setting, boolean isMember, boolean isOwner) {
    return findFirstApplicableFlagSetting(setting, isMember, isOwner).map(Map.Entry::getKey).orElse(setting.getDefaultValue());
  }

  public Optional<Region> getHighestPriorityRegion() {
    if (regions.size() == 0) return Optional.empty();
    return Optional.of(regions.get(regions.size() - 1));
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
