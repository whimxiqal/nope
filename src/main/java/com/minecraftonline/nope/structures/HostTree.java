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
 *
 */

package com.minecraftonline.nope.structures;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minecraftonline.nope.SettingLibrary;
import lombok.Data;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HostTree {

  private final GlobalHost globalHost = new GlobalHost();
  private final HashMap<UUID, WorldHost> worldHosts = Maps.newHashMap();
  // TODO K-D Tree for regions here

  public static HostTree up() {

    // Load all hosts
    HostTree hostTree = new HostTree();
    Sponge.getServer()
        .getAllWorldProperties()
        .forEach(worldProperties ->
            hostTree
                .worldHosts
                .put(worldProperties.getUniqueId(), new WorldHost(worldProperties)));

    // ...

    return hostTree;
  }

  private abstract static class Host {
    private final HashMap<String, Object> settings = Maps.newHashMap();

    @Nullable
    @SuppressWarnings("unchecked")
    <A> A putSetting(SettingLibrary.Setting<A> setting, A value) {
      return (A) settings.put(setting.getInfo().getId(), value);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    <A> A getSetting(SettingLibrary.Setting<A> setting) {
      return (A) settings.get(setting.getInfo().getId());
    }

    boolean hasSetting(SettingLibrary.Setting<?> setting) {
      return settings.containsKey(setting.getInfo().getId());
    }

    boolean contains(Locatable spongeLocatable) {
      return true;
    }

    abstract String getName();
  }

  public static class GlobalHost extends Host {

    @Override
    String getName() {
      return "__global__";
    }
  }

  public static class WorldHost extends Host {
    private final WorldProperties spongeWorldProperties;

    public WorldHost(WorldProperties spongeWorldProperties) {
      this.spongeWorldProperties = spongeWorldProperties;
    }

    @Override
    boolean contains(Locatable spongeLocatable) {
      return super.contains(spongeLocatable)
          && spongeLocatable.getWorld().getUniqueId().equals(this.spongeWorldProperties.getUniqueId());
    }

    @Override
    String getName() {
      return spongeWorldProperties.getWorldName();
    }
  }

  @Data
  public static class Region extends Host {
    private final WorldHost worldHost;

    /** Unique identifier. */
    private final String name;

    private final int xmin;
    private final int xmax;

    private final int ymin;
    private final int ymax;

    private final int zmin;
    private final int zmax;

    @Override
    boolean contains(Locatable spongeLocatable) {
      return worldHost.contains(spongeLocatable)
          && spongeLocatable.getLocation().getBlockX() >= xmin
          && spongeLocatable.getLocation().getBlockX() <= xmax
          && spongeLocatable.getLocation().getBlockY() >= ymin
          && spongeLocatable.getLocation().getBlockY() <= ymax
          && spongeLocatable.getLocation().getBlockZ() >= zmin
          && spongeLocatable.getLocation().getBlockZ() <= zmax;

    }

    @Override
    String getName() {
      return name;
    }
  }


  /* ======= */
  /* METHODS */
  /* ======= */

  void addRegion(UUID worldUuid, String name, Vector3i pos1, Vector3i pos2) {
    Region region = new Region(worldHosts.get(worldUuid), name,
        Math.min(pos1.getX(), pos2.getX()),
        Math.max(pos1.getX(), pos2.getX()),
        Math.min(pos1.getY(), pos2.getY()),
        Math.max(pos1.getY(), pos2.getY()),
        Math.min(pos1.getZ(), pos2.getZ()),
        Math.max(pos1.getZ(), pos2.getZ()));
    // TODO add region to data structure
  }

  List<Region> getRegions(Location<World> location) {
    WorldHost worldHost = worldHosts.get(location.getExtent().getUniqueId());
    // TODO run through data structure to find regions
    return Lists.newArrayList();
  }

}
