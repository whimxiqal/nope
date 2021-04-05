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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingLibrary;
import com.minecraftonline.nope.setting.SettingValue;
import com.minecraftonline.nope.structures.HashQueueVolumeTree;
import com.minecraftonline.nope.structures.VolumeTree;

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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

/**
 * Implementation of HostTree making distinctions between
 * a GlobalHost, a WorldHost, and a Zone (VolumeHost).
 *
 * @see Host
 * @see VolumeHost
 */
public final class HostTreeImpl implements HostTree {

  private final HashMap<UUID, WorldHost> worldHosts = Maps.newHashMap();
  private final Map<String, UUID> zoneToWorld = Maps.newHashMap();
  private final Storage storage;
  private final String globalHostName;
  private final Function<String, String> worldNameConverter;
  private final String zoneNameRegex;
  private GlobalHost globalHost;

  /**
   * Default constructor.
   *
   * @param storage            the type of storage to handle IO
   * @param globalHostName     the name given to the global host
   * @param worldNameConverter the converter with which to create the world host names
   * @param zoneNameRegex      the allowed regex for added zones, which should ensure
   *                           no conflicts with global or world hosts
   */
  public HostTreeImpl(@Nonnull Storage storage,
                      @Nonnull String globalHostName,
                      @Nonnull Function<String, String> worldNameConverter,
                      @Nonnull String zoneNameRegex) {
    this.storage = storage;
    this.globalHostName = globalHostName;
    this.worldNameConverter = worldNameConverter;
    this.zoneNameRegex = zoneNameRegex;

    this.globalHost = new GlobalHost();
  }

  @Nullable
  @Override
  public Host isRedundant(Host host, SettingKey<?> key) {
    // Check if this host even has a setting
    Optional<? extends SettingValue<?>> value = host.get(key);
    if (!value.isPresent()) return null;
    List<Host> containers = new LinkedList<>(getContainingHosts(host));
    containers.sort(Comparator.comparingInt(h -> -h.getPriority()));  // Sort maximum first
    for (Host container : containers) {
      // Continue if this container doesn't have priority over the desired host
      if (container.getPriority() > host.getPriority()) continue;

      Optional<? extends SettingValue<?>> containerValue = container.get(key);
      // Continue if the value is not found on this host
      if (!containerValue.isPresent()) continue;

      // If the value is the same, then its redundant. Otherwise, not redundant
      if (value.get().equals(containerValue.get())) {
        return container;
      } else {
        return null;
      }
    }

    // We're out of containers, so check if the default value is this one
    if (key.getDefaultData().equals(value.get().getData())) {
      return host;  // Return the original host to signify that the default value makes this redundant
    } else {
      return null;
    }
  }

  @Nonnull
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
    assert zone.getWorldUuid() != null;
    containers.addAll(getContainingHosts(new Location<>(Sponge.getServer()
        .getWorld(zone.getWorldUuid())
        .orElseThrow(() -> new RuntimeException("Could not find world")),
        zone.getMinX(),
        zone.getMinY(),
        zone.getMinZ())));
    containers.remove(zone);  // Don't keep the one that we're looking with
    containers.retainAll(getContainingHosts(new Location<>(Sponge.getServer()
        .getWorld(zone.getWorldUuid())
        .orElseThrow(() -> new RuntimeException("Could not find world")),
        zone.getMaxX(),
        zone.getMaxY(),
        zone.getMaxZ())));

    // Add global and world hosts
    containers.add(globalHost);
    containers.add(zone.getParent());
    return containers;
  }

  @Override
  public void load(String location) {

    // Setup worlds
    Sponge.getServer()
        .getAllWorldProperties()
        .forEach(worldProperties ->
            worldHosts.put(
                worldProperties.getUniqueId(),
                newWorldHost(worldProperties.getUniqueId())));

    // Read GlobalHost
    try {
      GlobalHost savedGlobalHost = storage.readGlobalHost(location, new GlobalHostSerializer());
      if (savedGlobalHost == null) {
        this.globalHost = new GlobalHost();
      } else {
        this.globalHost = savedGlobalHost;
      }
    } catch (IOException e) {
      Nope.getInstance().getLogger().error("Nope's GlobalHost could not be read.", e);
    }

    // Read WorldHosts
    try {
      storage.readWorldHosts(location, new WorldHostSerializer()).forEach(worldHost ->
          worldHosts.put(worldHost.getWorldUuid(), worldHost));
    } catch (IOException e) {
      Nope.getInstance().getLogger().error("Nope's WorldHosts could not be read.", e);
    }

    // Read Zones
    try {
      storage.readZones(location, worldHosts.values(), new ZoneSerializer()).forEach(this::addZone);
    } catch (IOException e) {
      Nope.getInstance().getLogger().error("Nope's Zones could not be read.", e);
    }

  }

  @Override
  public void save(String location) {
    try {
      storage.writeGlobalHost(location, globalHost, new GlobalHostSerializer());
    } catch (IOException e) {
      Nope.getInstance().getLogger().error("Nope's GlobalHost could not be written.", e);
    }

    try {
      storage.writeWorldHosts(location, worldHosts.values(), new WorldHostSerializer());
    } catch (IOException e) {
      Nope.getInstance().getLogger().error("Nope's WorldHosts could not be written.", e);
    }

    worldHosts.values().forEach(worldHost -> {
      try {
        storage.writeZones(location, worldHost.getZoneTree().volumes(), new ZoneSerializer());
      } catch (IOException e) {
        Nope.getInstance().getLogger().error("Nope's Zones could not be written.", e);
      }
    });
  }

  private WorldHost newWorldHost(UUID worldUuid) {
    return new WorldHost(Sponge.getServer()
        .getWorldProperties(worldUuid)
        .map(prop -> worldNameConverter.apply(prop.getWorldName()))
        .orElseThrow(() -> new RuntimeException(String.format(
            "The worldUuid %s does not correspond to a Sponge world)",
            worldUuid.toString()))),
        worldUuid);
  }

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
  public Zone getZone(final String name) {
    UUID worldUuid = zoneToWorld.get(name.toLowerCase());
    if (worldUuid == null) {
      return null;
    }
    return worldHosts.get(worldUuid).getZoneTree().get(name.toLowerCase());
  }

  @Nonnull
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

  @Nonnull
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
  public Zone addZone(final String name,
                      final UUID worldUuid,
                      final Vector3i pos1,
                      final Vector3i pos2,
                      int priority) {
    if (getHosts().size() >= Nope.MAX_HOST_COUNT) {
      return null;  // Too many
    }
    Zone zone = new Zone(worldUuid, name, pos1, pos2);
    zone.setPriority(priority);
    this.addZone(zone);
    return zone;
  }

  private void addZone(Zone zone) {
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
          "Zone insertion failed because name %s already exists (in world \"%s\")",
          zone.getName(),
          Sponge.getServer()
              .getAllWorldProperties()
              .stream().filter(prop -> prop.getUniqueId().equals(other.getWorldUuid()))
              .findFirst().map(WorldProperties::getWorldName)
              .orElse("unknown")));
    }
    worldHosts.get(zone.getWorldUuid())
        .getZoneTree()
        .add(zone.getName(), zone);  // Should return null
    zoneToWorld.put(zone.getName(), zone.getWorldUuid());
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

  @Nonnull
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

  @Nonnull
  @Override
  public Collection<Host> getContainingHosts(@Nonnull Location<World> location) {
    List<Host> list = Lists.newLinkedList();
    if (this.globalHost.encompasses(location)) {
      list.add(this.globalHost);
    }
    this.worldHosts.values().forEach(worldHost -> {
      if (worldHost.encompasses(location)) {
        list.add(worldHost);
        list.addAll(worldHost.getZoneTree()
            .containersOf(location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()));
      }
    });
    return list;
  }

  @Override
  public boolean isAssigned(SettingKey<?> key) {
    return getHosts().values().stream().anyMatch(host -> host.get(key).isPresent());
  }

  @Override
  public <V> V lookup(@Nonnull final SettingKey<V> key,
                      @Nullable final User user,
                      @Nonnull final Location<World> location) {
    Host dictator = lookupDictator(key, user, location);
    if (dictator == null) {
      return key.getDefaultData();
    } else {
      return dictator.get(key).orElseThrow(() ->
          new RuntimeException("There was an error looking up a Nope setting key")).getData();
    }
  }

  @Override
  public <V> V lookupAnonymous(@Nonnull SettingKey<V> key,
                               @Nonnull Location<World> location) {
    return lookup(key, null, location);
  }

  @Nullable
  @Override
  public Host lookupDictator(@Nonnull SettingKey<?> key, @Nullable User user, @Nonnull Location<World> location) {
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
      if (user == null || dictator.get(key).get().getTarget().test(key, user)) {
        return dictator;
      }
    }

    return null;
  }

  @Nullable
  @Override
  public Host lookupDictatorAnonymous(@Nonnull SettingKey<?> key, @Nullable User user, @Nonnull Location<World> location) {
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
     * @return the GLobalHost, or null if it does not exist in storage
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
   * Class for managing the single GlobalHost in this HostTree.
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
      host.putAll(SettingLibrary.deserializeSettingAssignments(json, host.getName()));
      return host;
    }
  }

  /**
   * Class for managing the few WorldHosts in this HostTree.
   */
  public class WorldHost extends Host {

    @Getter
    private final UUID worldUuid;
    @Getter(AccessLevel.PUBLIC)
    private final VolumeTree<String, Zone> zoneTree;

    WorldHost(String name, UUID worldUuid) {
      super(name, -1);
      int cacheSize = globalHost.getData(SettingLibrary.CACHE_SIZE);
      if (cacheSize < 0) {
        throw new RuntimeException("The cache size must be greater than 0");
      } else if (cacheSize == 0) {
        this.zoneTree = new VolumeTree<>();
      } else {
        this.zoneTree = new HashQueueVolumeTree<>(cacheSize);
      }

      this.worldUuid = worldUuid;
      setParent(globalHost);
    }

    @Override
    public boolean encompasses(Location<World> spongeLocation) {
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
              "This JSON element for a WorldHost is storing an invalid World name '%s'",
              json.getAsJsonObject().get("world")))));

      // Settings
      host.putAll(SettingLibrary.deserializeSettingAssignments(json, host.getName()));

      // No zones are put here because they must be added manually when deserializing the zones

      return host;
    }
  }

  /**
   * An object representing a three dimensional Nope Zone in a Minecraft world.
   * The Zone stores data about its location and extent in three dimensional
   * space and it stores com.minecraftonline.nope.setting.Setting data for handling
   * and manipulating Sponge events based in its specific configuration.
   */
  public class Zone extends VolumeHost {

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
    public boolean encompasses(Location<World> spongeLocation) {
      return spongeLocation.getBlockX() >= getMinX()
          && spongeLocation.getBlockX() <= getMaxX()
          && spongeLocation.getBlockY() >= getMinY()
          && spongeLocation.getBlockY() <= getMaxY()
          && spongeLocation.getBlockZ() >= getMinZ()
          && spongeLocation.getBlockZ() <= getMaxZ()
          && spongeLocation.getExtent().getUniqueId().equals(getWorldUuid());
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

  public class ZoneSerializer implements Host.HostSerializer<Zone> {

    @Override
    public JsonElement serialize(Zone host) {
      Map<String, Object> serializedHost = Maps.newHashMap();
      serializedHost.put("name", host.getName());
      serializedHost.put("settings", SettingLibrary.serializeSettingAssignments(host.getAll()));
      serializedHost.put("parent", Sponge.getServer()
          .getWorldProperties(((WorldHost) host.getParent()).worldUuid)
          .map(WorldProperties::getWorldName)
          .orElseThrow(() -> new RuntimeException(String.format(
              "Zone's parent WorldHost has invalid parent world UUID: %s",
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
    public Zone deserialize(JsonElement json) {
      String name = json.getAsJsonObject().get("name").getAsString();
      UUID parent = Sponge.getServer()
          .getWorldProperties(json.getAsJsonObject().get("parent").getAsString())
          .map(WorldProperties::getUniqueId)
          .orElseThrow(() -> new IllegalArgumentException(String.format(
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
      Zone host = new Zone(parent, name, xmin, xmax, ymin, ymax, zmin, zmax);

      host.setPriority(json.getAsJsonObject().get("priority").getAsInt());

      // Settings
      host.putAll(SettingLibrary.deserializeSettingAssignments(json, host.getName()));
      return host;
    }
  }
}
