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

package com.minecraftonline.nope.host;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.listener.SettingListeners;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingLibrary;
import com.minecraftonline.nope.setting.SettingValue;
import com.minecraftonline.nope.structures.VolumeTree;
import lombok.Getter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HostTreeImpl implements HostTree {

  private GlobalHost globalHost = new GlobalHost();
  private final HashMap<UUID, WorldHost> worldHosts = Maps.newHashMap();

  private final Map<String, UUID> regionToWorld = Maps.newHashMap();

  private final Storage storage;

  private final String globalHostName;
  private final Function<String, String> nameConverter;
  private final String invalidHostNameRegex;

  public HostTreeImpl(Storage storage,
                      String globalHostName,
                      Function<String, String> nameConverter,
                      String invalidHostNameRegex) {
    this.storage = storage;
    this.globalHostName = globalHostName;
    this.nameConverter = nameConverter;
    this.invalidHostNameRegex = invalidHostNameRegex;
  }

  @Override
  public void load() {

    // Setup worlds
    Sponge.getServer()
            .getAllWorldProperties()
            .forEach(worldProperties ->
                    worldHosts.put(
                            worldProperties.getUniqueId(),
                            newWorldHost(worldProperties.getUniqueId())));

    // Read GlobalHost
    GlobalHost savedGlobalHost = storage.readGlobalHost(new GlobalHostSerializer());
    if (savedGlobalHost != null) {
      this.globalHost = savedGlobalHost;
    }


    // Read WorldHosts
    storage.readWorldHosts(new WorldHostSerializer()).forEach(worldHost ->
            worldHosts.put(worldHost.getWorldUuid(), worldHost));

    // Read Regions
    storage.readRegions(worldHosts.values(), new RegionSerializer()).forEach(this::addRegion);

  }

  @Override
  public void save() {
    storage.writeGlobalHost(globalHost, new GlobalHostSerializer());
    storage.writeWorldHosts(worldHosts.values(), new WorldHostSerializer());
    worldHosts.values().forEach(worldHost ->
            storage.writeRegions(worldHost.regionTree.volumes(), new RegionSerializer()));

    SettingListeners.register();
  }

  /**
   * Class for managing the single GlobalHost in this HostTree
   */
  class GlobalHost extends Host {
    private GlobalHost() {
      super(globalHostName, -2);
      setParent(null);
    }

    @Override
    public void setPriority(int priority) {
      throw new UnsupportedOperationException("You cannot set the priority of the global host!");
    }

    @Override
    public UUID getWorldUuid() {
      return null;
    }
  }

  public class GlobalHostSerializer implements Host.HostSerializer<GlobalHost> {

    @Override
    public JsonElement serialize(GlobalHost host) {
      Map<String, Object> serializedHost = Maps.newHashMap();
      serializedHost.put("settings", SettingLibrary.serializeSettingAssignments(host.getAll()));
      return new Gson().toJsonTree(serializedHost);
    }

    @Override
    public GlobalHost deserialize(JsonElement json) {
      GlobalHost host = new GlobalHost();
      host.putAll(SettingLibrary.deserializeSettingAssignments(json));
      return host;
    }
  }

  private WorldHost newWorldHost(UUID worldUuid) {
    return new WorldHost(Sponge.getServer()
            .getWorldProperties(worldUuid)
            .map(prop -> nameConverter.apply(prop.getWorldName()))
            .orElseThrow(() -> new RuntimeException(String.format(
                    "The worldUuid %s does not correspond to a Sponge world)",
                    worldUuid.toString()))),
            worldUuid);
  }

  /**
   * Class for managing the few WorldHosts in this HostTree
   */
  class WorldHost extends Host {

    @Getter
    private final UUID worldUuid;
    private final VolumeTree<String, Region> regionTree = new VolumeTree<>();

    WorldHost(String name, UUID worldUuid) {
      super(name, -1);
      this.worldUuid = worldUuid;
      setParent(globalHost);
    }

    @Override
    boolean encompasses(Location<World> spongeLocation) {
      return spongeLocation.getExtent().getUniqueId().equals(this.worldUuid);
    }

    @Override
    public void setPriority(int priority) {
      throw new UnsupportedOperationException("You cannot set the priority of a WorldHost!");
    }

  }

  public class WorldHostSerializer implements Host.HostSerializer<WorldHost> {

    @Override
    public JsonElement serialize(WorldHost host) {
      Map<String, Object> serializedHost = Maps.newHashMap();
      serializedHost.put("settings", SettingLibrary.serializeSettingAssignments(host.getAll()));
      serializedHost.put("world", Sponge.getServer()
              .getWorldProperties(host.worldUuid)
              .map(WorldProperties::getWorldName)
              .orElseThrow(() -> new RuntimeException(String.format(
                      "WorldHost has invalid world UUID: %s",
                      host.worldUuid.toString()))));
      return new Gson().toJsonTree(serializedHost);
    }

    @Override
    public WorldHost deserialize(JsonElement json) {
      WorldHost host = newWorldHost(Sponge.getServer()
              .getWorldProperties(json.getAsJsonObject().get("world").getAsString())
              .map(WorldProperties::getUniqueId)
              .orElseThrow(() -> new RuntimeException(String.format(
                      "This JSON element for a WorldHost is storing an invalid World name '%s': %s",
                      json.getAsJsonObject().get("world"),
                      json))));

      // Settings
      host.putAll(SettingLibrary.deserializeSettingAssignments(json));

      // No regions are put here because they must be added manually when deserializing the regions

      return host;
    }
  }

  /**
   * An object representing a three dimensional Nope Region in a Minecraft world.
   * The Region stores data about its location and extent in three dimensional
   * space and it stores com.minecraftonline.nope.setting.Setting data for handling and manipulating Sponge events
   * based in its specific configuration.
   */
  public class Region extends VolumeHost {

    @Getter
    private final UUID worldUuid;

    /**
     * Default constructor.
     *
     * @param name unique identifier
     * @param xmin start point of x range, inclusive
     * @param xmax end point of x range, inclusive
     * @param ymin start point of y range, inclusive
     * @param ymax end point of y range, inclusive
     * @param zmin start point of z range, inclusive
     * @param zmax end point of z range, inclusive
     */
    public Region(UUID worldUuid, String name, int xmin, int xmax, int ymin, int ymax, int zmin, int zmax) {
      super(name, xmin, xmax, ymin, ymax, zmin, zmax);
      this.worldUuid = worldUuid;
      if (!worldHosts.containsKey(worldUuid)) {
        throw new IllegalArgumentException("No world exists with UUID " + worldUuid.toString());
      }
      setParent(worldHosts.get(worldUuid));
    }

    /**
     * Convenient constructor.
     *
     * @param name unique identifier
     * @param pos1 a point to bound this region
     * @param pos2 another point to bound this region
     */
    public Region(UUID worldUuid, String name, Vector3i pos1, Vector3i pos2) {
      this(worldUuid, name,
              Math.min(pos1.getX(), pos2.getX()),
              Math.max(pos1.getX(), pos2.getX()),
              Math.min(pos1.getY(), pos2.getY()),
              Math.max(pos1.getY(), pos2.getY()),
              Math.min(pos1.getZ(), pos2.getZ()),
              Math.max(pos1.getZ(), pos2.getZ()));
    }


    @Override
    boolean encompasses(Location<World> spongeLocation) {
      return spongeLocation.getBlockX() >= getMinX()
              && spongeLocation.getBlockX() <= getMaxX()
              && spongeLocation.getBlockY() >= getMinY()
              && spongeLocation.getBlockY() <= getMaxY()
              && spongeLocation.getBlockZ() >= getMinZ()
              && spongeLocation.getBlockZ() <= getMaxZ();
    }

    @Override
    public void setPriority(int priority) {
      if (priority < 0) {
        throw new IllegalArgumentException("Cannot set a negative priority");
      }
      Optional<Region> intersection = findIntersectingRegionWithSamePriority(worldUuid, this);
      if (intersection.isPresent()) {
        throw new IllegalArgumentException(String.format(
                "Cannot set priority of %s to %d, "
                        + "because region %s which this intersects has that priority",
                getName(),
                priority,
                intersection.get().getName()
        ));
      }
    }
  }

  public class RegionSerializer implements Host.HostSerializer<Region> {

    @Override
    public JsonElement serialize(Region host) {
      Map<String, Object> serializedHost = Maps.newHashMap();
      serializedHost.put("name", host.getName());
      serializedHost.put("settings", SettingLibrary.serializeSettingAssignments(host.getAll()));
      serializedHost.put("parent", Sponge.getServer()
              .getWorldProperties(((WorldHost) host.getParent()).worldUuid)
              .map(WorldProperties::getWorldName)
              .orElseThrow(() -> new RuntimeException(String.format(
                      "Region's parent WorldHost has invalid parent world UUID: %s",
                      ((WorldHost) host.getParent()).worldUuid.toString()))));
      serializedHost.put("priority", host.getPriority());
      Map<String, Integer> volume = Maps.newHashMap();
      volume.put("xmin", host.getMinX());
      volume.put("xmax", host.getMaxX());
      volume.put("ymin", host.getMinY());
      volume.put("ymax", host.getMaxY());
      volume.put("zmin", host.getMinZ());
      volume.put("zmax", host.getMaxZ());
      serializedHost.put("volume", volume);

      return new Gson().toJsonTree(serializedHost);
    }

    @Override
    public Region deserialize(JsonElement json) {
      String name = json.getAsJsonObject().get("name").getAsString();
      UUID parent = Sponge.getServer()
              .getWorldProperties(json.getAsJsonObject().get("parent").getAsString())
              .map(WorldProperties::getUniqueId)
              .orElseThrow(() -> new RuntimeException(String.format(
                      "This JSON element for a WorldHost is storing an invalid World name '%s': %s",
                      json.getAsJsonObject().get("world"),
                      json)));
      JsonObject volume = json.getAsJsonObject().get("volume").getAsJsonObject();
      int xmin = volume.get("xmin").getAsInt();
      int xmax = volume.get("xmax").getAsInt();
      int ymin = volume.get("ymin").getAsInt();
      int ymax = volume.get("ymax").getAsInt();
      int zmin = volume.get("zmin").getAsInt();
      int zmax = volume.get("zmax").getAsInt();
      Region host = new Region(parent, name, xmin, xmax, ymin, ymax, zmin, zmax);

      host.setPriority(json.getAsJsonObject().get("priority").getAsInt());

      // Settings
      host.putAll(SettingLibrary.deserializeSettingAssignments(json));
      return host;
    }
  }

  /**
   * Storage for Nope Hosts.
   */
  public interface Storage {

    GlobalHost readGlobalHost(Host.HostSerializer<GlobalHost> serializer) throws HostParseException;

    Collection<WorldHost> readWorldHosts(Host.HostSerializer<WorldHost> serializer) throws HostParseException;

    Collection<Region> readRegions(Collection<WorldHost> parents, Host.HostSerializer<Region> serializer);

    void writeGlobalHost(GlobalHost globalHost, Host.HostSerializer<GlobalHost> serializer);

    void writeWorldHosts(Collection<WorldHost> worldHosts, Host.HostSerializer<WorldHost> serializer);

    void writeRegions(Collection<Region> regions, Host.HostSerializer<Region> serializer);

    class HostParseException extends RuntimeException {
      public HostParseException() {
        super();
      }

      public HostParseException(String message) {
        super(message);
      }

      public HostParseException(Throwable t) {
        super(t);
      }

      public HostParseException(String message, Throwable t) {
        super(message, t);
      }
    }

  }

  /* ======= */
  /* METHODS */
  /* ======= */

  @Nonnull
  @Override
  public GlobalHost getGlobalHost() {
    return globalHost;
  }

  @Nullable
  @Override
  public WorldHost getWorldHost(final UUID worldUuid) {
    return worldHosts.get(worldUuid);
  }

  @Nullable
  @Override
  public Region getRegion(final String name) {
    UUID worldUuid = regionToWorld.get(name);
    if (worldUuid == null) {
      return null;
    }
    return worldHosts.get(worldUuid).regionTree.get(name);
  }

  @Nonnull
  @Override
  public Map<String, Host> getHosts() {
    Map<String, Host> hosts = Maps.newHashMap();
    hosts.put(this.globalHost.getName(), this.globalHost);
    this.worldHosts.values().forEach(worldHost -> {
      hosts.put(worldHost.getName(), worldHost);
      worldHost.regionTree.volumes().forEach(region -> hosts.put(region.getName(), region));
    });
    return hosts;
  }

  @Nullable
  @Override
  public Collection<VolumeHost> getRegions(final UUID worldUuid) {
    return Optional.ofNullable(getWorldHost(worldUuid)).map(worldHost ->
            worldHost.regionTree.volumes()
                    .stream()
                    .map(volume -> (VolumeHost) volume)
                    .collect(Collectors.toList()))
            .orElse(null);
  }

  @Nonnull
  @Override
  public Region addRegion(final String name,
                          final UUID worldUuid,
                          final Vector3i pos1,
                          final Vector3i pos2,
                          int priority) {
    Region region = new Region(worldUuid, name, pos1, pos2);
    region.setPriority(priority);
    this.addRegion(region);
    return region;
  }

  private void addRegion(Region region) {
    if (Pattern.matches(invalidHostNameRegex, region.getName())) {
      throw new IllegalArgumentException(String.format(
              "Region insertion failed because the name %s has the name format of a WorldHost",
              region.getName()));
    }
    if (hasRegion(region.getName())) {
      throw new IllegalArgumentException(String.format(
              "Region insertion failed because name %s already exists (in world \"%s\")",
              region.getName(),
              Sponge.getServer()
                      .getAllWorldProperties()
                      .stream().filter(prop -> prop.getUniqueId().equals(region.getWorldUuid()))
                      .findFirst().map(WorldProperties::getWorldName)
                      .orElse("unknown")));
    }
    Optional<Region> intersection = findIntersectingRegionWithSamePriority(region.getWorldUuid(), region);
    if (intersection.isPresent()) {
      throw new IllegalArgumentException(String.format(
              "Region insertion failed because the new region %s and region %s have the same priority level: %d",
              region.getName(),
              intersection.get().getName(),
              region.getPriority()));
    }
    worldHosts.get(region.getWorldUuid()).regionTree.push(region.getName(), region);  // Should return null
    regionToWorld.put(region.getName(), region.getWorldUuid());
  }

  private Optional<Region> findIntersectingRegionWithSamePriority(final UUID worldUuid,
                                                                  final Region region) {
    return worldHosts.get(worldUuid).regionTree
            .volumes()
            .stream()
            .filter(other -> other != region && region.intersects(other) && region.getPriority() == other.getPriority())
            .findAny();
  }

  @Nonnull
  @Override
  public Region removeRegion(final String name) {
    if (!hasRegion(name)) {
      throw new IllegalArgumentException(String.format(
              "Region deletion failed because name %s does not exist",
              name));
    }
    WorldHost worldHost = worldHosts.get(regionToWorld.get(name));
    regionToWorld.remove(name);
    return worldHost.regionTree.remove(name);
  }

  @Override
  public boolean hasRegion(final String name) {
    return regionToWorld.containsKey(name);
  }

  @Override
  public boolean isAssigned(SettingKey<?> key) {
    return getHosts().values().stream().anyMatch(host -> host.get(key).isPresent());
  }

  @Override
  public <V> V lookup(final SettingKey<V> key,
                      final Subject subject,
                      final Location<World> location) {
    return lookupWithParentsAndTarget(key, subject, location, key.getDefaultData());
  }

  private <V> V lookupWithParentsAndTarget(final SettingKey<V> key,
                                           final Subject subject,
                                           final Location<World> location,
                                           final V defaultData) {
    // Maximum priority queue (swapped int compare)
    Queue<Host> maximumHeap = new PriorityQueue<>((h1, h2) ->
            Integer.compare(h2.getPriority(), h1.getPriority()));

    // add global
    if (globalHost.has(key)) {
      maximumHeap.add(globalHost);
    }

    // add world
    Optional<WorldHost> worldHost = worldHosts.values()
            .stream()
            .filter(host -> host.encompasses(location))
            .findAny();
    if (worldHost.isPresent()) {
      if (worldHost.get().has(key)) {
        maximumHeap.add(worldHost.get());
      }

      // add regions
      worldHost.get().regionTree
              .containingVolumes(location.getBlockX(),
                      location.getBlockY(),
                      location.getBlockZ())
              .stream().filter(host -> host.has(key))
              .forEach(maximumHeap::add);
    }

    Host dictator = maximumHeap.peek();
    Optional<SettingValue<V>> value;

    if (dictator != null) {
      value = dictator.get(key);
      if (!value.isPresent()) {
        // This shouldn't happen because we previously found that this host has this setting
        throw new RuntimeException("Error retrieving setting value");
      }
      if (value.get().getTarget().test(subject)) {
        return value.get().getData();
      }
    }

    if (key.getParent().isPresent()) {
      return lookupWithParentsAndTarget(key.getParent().get(), subject, location, defaultData);
    } else {
      return defaultData;
    }
  }

}
