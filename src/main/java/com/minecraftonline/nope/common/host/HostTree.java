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

package com.minecraftonline.nope.common.host;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.setting.Setting;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingLibrary;
import com.minecraftonline.nope.common.setting.SettingValue;
import com.minecraftonline.nope.common.struct.Location;
import com.minecraftonline.nope.common.struct.Vector3i;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.ResourceKey;

/**
 * Implementation of HostTree making distinctions between
 * a GlobalHost, a WorldHost, and a Zone (VolumeHost).
 *
 * @see Host
 * @see VolumeHost
 */
public abstract class HostTree implements HostTreeAdapter {

  protected final HashMap<UUID, WorldHost> worldHosts = Maps.newHashMap();
  private final Map<String, UUID> zoneToWorld = Maps.newHashMap();
  protected final Storage storage;
  protected final Function<String, String> worldNameConverter;
  private final String zoneNameRegex;
  protected GlobalHost globalHost;

  /**
   * Default constructor.
   *
   * @param storage            the type of storage to handle IO
   * @param globalHostName     the name given to the global host
   * @param worldNameConverter the converter with which to create the world host names
   * @param zoneNameRegex      the allowed regex for added zones, which should ensure
   *                           no conflicts with global or world hosts
   */
  public HostTree(@NotNull Storage storage,
                  @NotNull String globalHostName,
                  @NotNull Function<String, String> worldNameConverter,
                  @NotNull String zoneNameRegex) {
    this.storage = storage;
    this.worldNameConverter = worldNameConverter;
    this.zoneNameRegex = zoneNameRegex;
    this.globalHost = new GlobalHost(globalHostName);
  }

  @Nullable
  @Override
  public Host isRedundant(Host host, SettingKey<?> key) {
    // Check if this host even has a setting
    Optional<? extends SettingValue<?>> value = host.get(key);
    if (!value.isPresent()) {
      return null;
    }
    List<Host> containers = new LinkedList<>(getContainingHosts(host));
    containers.sort(Comparator.comparingInt(h -> -h.getPriority()));  // Sort maximum first
    for (Host container : containers) {
      // Continue if this container doesn't have priority over the desired host
      if (container.getPriority() > host.getPriority()) {
        continue;
      }

      Optional<? extends SettingValue<?>> containerValue = container.get(key);
      // Continue if the value is not found on this host
      if (!containerValue.isPresent()) {
        continue;
      }

      // If the value is the same, then its redundant. Otherwise, not redundant
      if (value.get().equals(containerValue.get())) {
        return container;
      } else {
        return null;
      }
    }

    // We're out of containers, so check if the default value is this one
    if (key.getDefaultData().equals(value.get().getData())) {
      // Return the original host to signify that the default value makes this redundant
      return host;
    } else {
      return null;
    }
  }

  @NotNull
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
  public Zone getZone(final String name) {
    UUID worldUuid = zoneToWorld.get(name.toLowerCase());
    if (worldUuid == null) {
      return null;
    }
    return worldHosts.get(worldUuid).getZoneTree().get(name.toLowerCase());
  }

  @NotNull
  @Override
  public Map<String, Host> getHosts() {
    Map<String, Host> hosts = Maps.newHashMap();
    hosts.put(this.globalHost.getName(), this.globalHost);
    this.worldHosts.values().forEach(worldHost -> {
      hosts.put(worldHost.getName(), worldHost);
      worldHost.getZoneTree().volumes().forEach(zone -> hosts.put(zone.getName(), zone));
    });
    return hosts;
  }

  @NotNull
  @Override
  public Collection<VolumeHost> getZones(final UUID worldUuid) throws IllegalArgumentException {
    return Optional.ofNullable(getWorldHost(worldUuid)).map(worldHost ->
        worldHost.getZoneTree().volumes()
            .stream()
            .map(volume -> (VolumeHost) volume)
            .collect(Collectors.toList()))
        .orElseThrow(() -> new IllegalArgumentException("Invalid world uuid: " + worldUuid));
  }

  @Nullable
  @Override
  public Zone addZone(@NotNull final String name,
                      @NotNull final UUID worldUuid,
                      @NotNull final Vector3i pos1,
                      @NotNull final Vector3i pos2,
                      int priority) {
    if (getHosts().size() >= Nope.MAX_HOST_COUNT) {
      return null;  // Too many
    }
    Zone zone = new Zone(worldUuid, name, pos1, pos2);
    zone.setPriority(priority);
    this.addZone(zone);
    return zone;
  }

  protected void addZone(Zone zone) {
    if (getHosts().size() >= Nope.MAX_HOST_COUNT) {
      return;  // Too many
    }
    if (!Pattern.matches(zoneNameRegex, zone.getName())) {
      throw new IllegalArgumentException(String.format(
          "Zone insertion failed because the format of name %s is not allowed",
          zone.getName()));
    }
    Zone other = getZone(zone.getName());
    if (other != null) {
      throw new IllegalArgumentException(String.format(
          "Zone insertion failed because name %s already exists in the given world",
          zone.getName()));
    }
    worldHosts.get(zone.getWorldKey())
        .getZoneTree()
        .add(zone.getName(), zone);  // Should return null
    zoneToWorld.put(zone.getName(), zone.getWorldKey());
  }

  /* ======= */
  /* METHODS */
  /* ======= */

  private Optional<Zone> findIntersectingZoneWithSamePriority(final UUID worldUuid,
                                                              final Zone zone) {
    return worldHosts.get(worldUuid).getZoneTree()
        .volumes()
        .stream()
        .filter(other -> other != zone
            && zone.intersects(other)
            && zone.getPriority() == other.getPriority())
        .findAny();
  }

  @NotNull
  @Override
  public Zone removeZone(final String name) {
    if (!hasZone(name.toLowerCase())) {
      throw new IllegalArgumentException(String.format(
          "Zone deletion failed because name %s does not exist",
          name.toLowerCase()));
    }
    WorldHost worldHost = worldHosts.get(zoneToWorld.get(name.toLowerCase()));
    zoneToWorld.remove(name.toLowerCase());
    return Objects.requireNonNull(worldHost.getZoneTree().remove(name.toLowerCase()));
  }

  @Override
  public boolean hasZone(final String name) {
    return zoneToWorld.containsKey(name.toLowerCase());
  }

  @NotNull
  @Override
  public Collection<Host> getContainingHosts(@NotNull Location location) {
    List<Host> list = Lists.newLinkedList();
    if (this.globalHost.encompasses(location)) {
      list.add(this.globalHost);
    }
    this.worldHosts.values().forEach(worldHost -> {
      if (worldHost.encompasses(location)) {
        list.add(worldHost);
        list.addAll(worldHost.getZoneTree().containersOf(location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ()));
      }
    });
    return list;
  }

  /**
   * Get all hosts which completely contain the given host.
   *
   * @param host the contained host
   * @return all containing hosts
   */
  @NotNull
  public Collection<Host> getContainingHosts(Host host) {
    Set<Host> containers = new HashSet<>();
    if (host instanceof GlobalHost) {
      return containers;  // Not contained by anything
    }
    if (host instanceof WorldHost) {
      containers.add(globalHost);  // Only contained by global
      return containers;
    }
    if (!(host instanceof Zone)) {
      throw new IllegalArgumentException("The given host must be a GlobalHost, WorldHost, or Zone");
    }
    Zone zone = (Zone) host;

    // Calculate all zones that encapsulate this one
    assert zone.getWorldKey() != null;
    containers.addAll(getContainingHosts(new Location(zone.getMinX(),
        zone.getMinY(),
        zone.getMinZ(),
        zone.getWorldKey())));
    containers.remove(zone);  // Don't keep the one that we're looking with
    containers.retainAll(getContainingHosts(new Location(zone.getMaxX(),
        zone.getMaxY(),
        zone.getMaxZ(),
        zone.getWorldKey())));

    // Add global and world hosts
    containers.add(globalHost);
    containers.add(zone.getParent());
    return containers;
  }

  @Override
  public boolean isAssigned(SettingKey<?> key) {
    return getHosts().values().stream().anyMatch(host -> host.get(key).isPresent());
  }

  @Override
  public <V> V lookup(@NotNull final SettingKey<V> key,
                      @Nullable final UUID userUuid,
                      @NotNull final Location location) {
    Host dictator = lookupDictator(key, userUuid, location);
    if (dictator == null) {
      return key.getDefaultData();
    } else {
      return dictator.get(key).orElseThrow(() ->
          new RuntimeException("There was an error looking up a Nope setting key")).getData();
    }
  }

  @Override
  public <V> V lookupAnonymous(@NotNull SettingKey<V> key,
                               @NotNull Location location) {
    return lookup(key, null, location);
  }

  @Nullable
  @Override
  public Host lookupDictator(@NotNull SettingKey<?> key,
                             @Nullable UUID userUuid,
                             @NotNull Location location) {
    /* Collect all hosts */
    LinkedList<Host> hosts = new LinkedList<>();

    /* Add global */
    if (globalHost.has(key)) {
      hosts.addFirst(globalHost);
    }

    /* Add world */
    Optional<WorldHost> worldHost = worldHosts.values()
        .stream()
        .filter(host -> host.encompasses(location))
        .findAny();
    if (worldHost.isPresent()) {
      if (worldHost.get().has(key)) {
        hosts.addFirst(worldHost.get());
      }

      /* Add zones */
      worldHost.get().getZoneTree()
          .containersOf(location.getBlockX(),
              location.getBlockY(),
              location.getBlockZ())
          .stream().filter(host -> host.has(key))
          .forEach(hosts::addFirst);
    }

    /* Choose a data structure that will optimize searching for highest priority matching */
    Queue<Host> hostQueue;
    Comparator<Host> descending = (h1, h2) -> Integer.compare(h2.getPriority(), h1.getPriority());
    /*  */
    if (hosts.size() > 10) {
      hostQueue = new PriorityQueue<>(hosts.size(), descending);
      hostQueue.addAll(hosts);
    } else {
      hostQueue = new LinkedList<>(hosts);
      ((LinkedList<Host>) hostQueue).sort(descending);
    }

    Host dictator;

    while (hostQueue.peek() != null) {
      dictator = hostQueue.remove();
      if (!dictator.get(key).isPresent()) {
        // This shouldn't happen because we previously found that this host has this setting
        throw new RuntimeException("Error retrieving setting value");
      }
      if (userUuid == null || dictator.get(key).get().getTarget().test(key, userUuid)) {
        return dictator;
      }
    }

    return null;
  }

  @Nullable
  @Override
  public Host lookupDictatorAnonymous(@NotNull SettingKey<?> key, @NotNull Location location) {
    return lookupDictator(key, null, location);
  }

  /**
   * Storage for Nope Hosts.
   */
  public interface Storage {

    /**
     * Read a GlobalHost from storage, if it exists.
     *
     * @param location   the location of the data
     * @param serializer the serializer which holds the logic for serialization
     * @return the GlobalHost, or null if it does not exist in storage
     * @throws IOException        if there is an error connecting to the storage
     * @throws HostParseException if there was an error parsing an existing stored GlobalHost
     */
    @Nullable
    GlobalHost readGlobalHost(String location, Host.HostSerializer<GlobalHost> serializer)
        throws IOException, HostParseException;

    /**
     * Read all WorldHosts in storage.
     *
     * @param location   the location of the data
     * @param serializer the serializer which holds the logic for serialization
     * @return any WorldHosts in storage
     * @throws IOException        if there is an error connecting to the storage
     * @throws HostParseException if there is an error parsing an existing stored WorldHost
     */
    Collection<WorldHost> readWorldHosts(String location, Host.HostSerializer<WorldHost> serializer)
        throws IOException, HostParseException;

    /**
     * Reads all Zones in storage.
     *
     * @param location   the location of the data
     * @param parents    all parents of these Zones so that the Zones
     *                   are given the correct parents
     * @param serializer the serializer which holds the logic for serialization
     * @return any Zones in storage
     * @throws IOException        if there is an error connecting to the storage
     * @throws HostParseException if there is any error parsing an existing stored Zone
     */
    Collection<Zone> readZones(String location, Collection<WorldHost> parents,
                               Host.HostSerializer<Zone> serializer)
        throws IOException, HostParseException;

    /**
     * Writes a GlobalHost to storage.
     *
     * @param location   the location of the data
     * @param globalHost the host to store
     * @param serializer the serializer which holds the logic for serialization
     * @throws IOException        if there is an error connecting to the storage
     * @throws HostParseException if there is any error parsing an existing stored GlobalHost
     */
    void writeGlobalHost(String location, GlobalHost globalHost,
                         Host.HostSerializer<GlobalHost> serializer)
        throws IOException, HostParseException;

    /**
     * Writes WorldHosts to storage.
     *
     * @param location   the location of the data
     * @param worldHosts the host to store
     * @param serializer the serializer which holds the logic for serialization
     * @throws IOException        if there is an error connecting to the storage
     * @throws HostParseException if there is any error parsing an existing stored WorldHost
     */
    void writeWorldHosts(String location, Collection<WorldHost> worldHosts,
                         Host.HostSerializer<WorldHost> serializer)
        throws IOException, HostParseException;

    /**
     * Writes Zones to storage.
     *
     * @param location   the location of the data
     * @param zones      the host to store
     * @param serializer the serializer which holds the logic for serialization
     * @throws IOException        if there is an error connecting to the storage
     * @throws HostParseException if there is any error parsing an existing stored Zone
     */
    void writeZones(String location, Collection<Zone> zones,
                    Host.HostSerializer<Zone> serializer)
        throws IOException, HostParseException;

    /**
     * An exception class to throw when an error in parsing from storage occurs.
     */
    class HostParseException extends RuntimeException {
      public HostParseException(Throwable t) {
        super(t);
      }

      public HostParseException(String message, Throwable t) {
        super(message, t);
      }
    }

  }

  /**
   * A serializer for {@link GlobalHost}s.
   */
  public class GlobalHostSerializer implements Host.HostSerializer<GlobalHost> {

    @Override
    public JsonElement serialize(GlobalHost host) {
      Map<String, Object> serializedHost = Maps.newHashMap();
      serializedHost.put("settings", SettingLibrary.serializeSettingAssignments(host.getAll()));
      return new Gson().toJsonTree(serializedHost);
    }

    @Override
    public GlobalHost deserialize(JsonElement json) {
      String name = json.getAsJsonObject().get("name").getAsString();
      GlobalHost host = new GlobalHost(name);
      host.putAll(SettingLibrary.deserializeSettingAssignments(json, host.getName()));
      return host;
    }
  }

  /**
   * An object representing a three dimensional Nope Zone in a Minecraft world.
   * The Zone stores data about its location and extent in three dimensional
   * space and it stores {@link Setting} data for handling
   * and manipulating events based in its specific configuration.
   */
  public class Zone extends VolumeHost {

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
    public Zone(UUID worldUuid,
                String name,
                int xmin,
                int xmax,
                int ymin,
                int ymax,
                int zmin,
                int zmax) {
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
     * @param pos1 a point to bound this zone
     * @param pos2 another point to bound this zone
     */
    public Zone(UUID worldUuid, String name, Vector3i pos1, Vector3i pos2) {
      this(worldUuid, name,
          Math.min(pos1.getX(), pos2.getX()),
          Math.max(pos1.getX(), pos2.getX()),
          Math.min(pos1.getY(), pos2.getY()),
          Math.max(pos1.getY(), pos2.getY()),
          Math.min(pos1.getZ(), pos2.getZ()),
          Math.max(pos1.getZ(), pos2.getZ()));
    }


    @Override
    public boolean encompasses(Location location) {
      return location.getBlockX() >= getMinX()
          && location.getBlockX() <= getMaxX()
          && location.getBlockY() >= getMinY()
          && location.getBlockY() <= getMaxY()
          && location.getBlockZ() >= getMinZ()
          && location.getBlockZ() <= getMaxZ()
          && location.getWorldUuid().equals(getWorldKey());
    }

    @Override
    public @Nullable ResourceKey getWorldKey() {
      return this.worldUuid;
    }

    @Override
    public void setPriority(int priority) throws IllegalArgumentException {
      if (priority < 0) {
        throw new IllegalArgumentException("Cannot set a negative priority");
      }
      super.setPriority(priority);
      Optional<Zone> intersection = findIntersectingZoneWithSamePriority(worldUuid, this);
      // Bump the priority level of the intersecting zone to make way
      intersection.ifPresent(zone -> zone.setPriority(priority + 1));
    }
  }

  /**
   * A serializing class for {@link Zone}.
   */
  public class ZoneSerializer implements Host.HostSerializer<Zone> {

    @Override
    public JsonElement serialize(Zone host) {
      Map<String, Object> serializedHost = Maps.newHashMap();
      serializedHost.put("name", host.getName());
      serializedHost.put("settings", SettingLibrary.serializeSettingAssignments(host.getAll()));
      serializedHost.put("parent", host.getWorldKey().toString());
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
    public Zone deserialize(JsonElement json) {
      String name = json.getAsJsonObject().get("name").getAsString();
      UUID parent = UUID.fromString(json.getAsJsonObject().get("parent").getAsString());
      JsonObject volume = json.getAsJsonObject().get("volume").getAsJsonObject();
      int xmin = volume.get("xmin").getAsInt();
      int xmax = volume.get("xmax").getAsInt();
      int ymin = volume.get("ymin").getAsInt();
      int ymax = volume.get("ymax").getAsInt();
      int zmin = volume.get("zmin").getAsInt();
      int zmax = volume.get("zmax").getAsInt();
      Zone host = new Zone(parent, name, xmin, xmax, ymin, ymax, zmin, zmax);

      host.setPriority(json.getAsJsonObject().get("priority").getAsInt());

      // Settings
      host.putAll(SettingLibrary.deserializeSettingAssignments(json, host.getName()));
      return host;
    }
  }
}
