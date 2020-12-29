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

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Preconditions;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.host.Worlded;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldHost extends Host implements Worlded {

  private final UUID worldUuid;
  private final Map<String, Region> regions = new HashMap<>();

  public WorldHost(@Nonnull UUID worldUuid) {
    Preconditions.checkNotNull(worldUuid);
    this.worldUuid = worldUuid;
  }

  @Override
  public UUID getWorldUuid() {
    return worldUuid;
  }

  public Map<String, Region> getRegions() {
    return regions;
  }

  public void addRegion(String regionId, Region region) {
    this.regions.put(regionId, region);
  }

  public void removeRegion(String regionId) {
    this.regions.remove(regionId);
    Nope.getInstance().getGlobalConfigManager().removeRegion(this.worldUuid, regionId);
  }

  public RegionSet getRegions(Vector3d position) {
    List<Region> validRegions = new ArrayList<>();
    for (Region region : this.regions.values()) {
      if (region.isLocationInRegion(position)) {
        validRegions.add(region);
      }
    }
    return new RegionSet(validRegions);
  }

  @Nonnull
  @Override
  public Setting.Applicability getApplicability() {
    return Setting.Applicability.WORLD;
  }
}
