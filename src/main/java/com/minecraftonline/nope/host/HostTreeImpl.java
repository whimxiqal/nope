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
import com.minecraftonline.nope.SettingLibrary;
import com.minecraftonline.nope.structures.VolumeTree;
import lombok.Getter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public class HostTreeImpl implements HostTree {

  private GlobalHost globalHost = new GlobalHost();
  private final HashMap<UUID, WorldHost> worldHosts = Maps.newHashMap();

  private final Map<String, UUID> regionToWorld = Maps.newHashMap();

  private final Storage storage;
  private final String globalHostName;
  private final Function<String, String> nameConverter;
  private final String worldHostNameRegex;

  public HostTreeImpl(Storage storage,
                      String globalHostName,
                      Function<String, String> nameConverter,
                      String worldHostNameRegex) {
    this.storage = storage;
    this.globalHostName = globalHostName;
    this.nameConverter = nameConverter;
    this.worldHostNameRegex = worldHostNameRegex;
    load();
  }

  public void load() {

    // Setup worlds
    Sponge.getServer()
            .getAllWorldProperties()
            .forEach(worldProperties ->
                    worldHosts.put(
                            worldProperties.getUniqueId(),
                            new WorldHost(worldProperties.getUniqueId())));

    // Read GlobalHost
    this.globalHost = storage.readGlobalHost(new GlobalHostSerializer());

    // Read WorldHosts
    storage.readWorldHosts(new WorldHostSerializer()).forEach(worldHost ->
            worldHosts.put(worldHost.getWorldUuid(), worldHost));

    // Read Regions
    storage.readRegions(worldHosts.values(), new RegionSerializer()).forEach(region ->
            addRegion(((WorldHost) region.getParent()).getWorldUuid(),
                    region.getName(),
                    new Vector3i(region.xMin(), region.yMin(), region.zMin()),
                    new Vector3i(region.xMax(), region.yMax(), region.zMax()),
                    region.getPriority()));

  }

  public void save() {
    storage.writeGlobalHost(globalHost, new GlobalHostSerializer());
    storage.writeWorldHosts(worldHosts.values(), new WorldHostSerializer());
    worldHosts.values().forEach(worldHost ->
            storage.writeRegions(worldHost.regionTree.volumes(), new RegionSerializer()));
  }

  /**
   * Class for managing the single GlobalHost in this HostTree
   */
  class GlobalHost extends Host {
    private GlobalHost() {
      super(globalHostName);
      setParent(null);
      setPriority(0);
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

  /**
   * Class for managing the few WorldHosts in this HostTree
   */
  class WorldHost extends Host {
    @Getter
    private final UUID worldUuid;
    private final VolumeTree<String, Region> regionTree = new VolumeTree<>();

    public WorldHost(UUID worldUuid) {
      super(Sponge.getServer().getWorldProperties(worldUuid)
              .map(prop -> nameConverter.apply(prop.getWorldName()))
              .orElseThrow(() -> new RuntimeException(String.format(
                      "The worldUuid %s does not correspond to a Sponge world)",
                      worldUuid.toString()))));
      this.worldUuid = worldUuid;
      setParent(globalHost);
      setPriority(1);
    }

    @Override
    boolean encompasses(Location<World> spongeLocation) {
      return spongeLocation.getExtent().getUniqueId().equals(this.worldUuid);
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
      WorldHost host = new WorldHost(Sponge.getServer()
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
   * space and it stores Setting data for handling and manipulating Sponge events
   * based in its specific configuration.
   */
  public class Region extends VolumeHost {


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
      if (!worldHosts.containsKey(worldUuid)) {
        throw new IllegalArgumentException("No world exists with UUID " + worldUuid.toString());
      }
      setParent(worldHosts.get(worldUuid));
      setPriority(2);
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
      return spongeLocation.getBlockX() >= xMin()
              && spongeLocation.getBlockX() <= xMax()
              && spongeLocation.getBlockY() >= yMin()
              && spongeLocation.getBlockY() <= yMax()
              && spongeLocation.getBlockZ() >= zMin()
              && spongeLocation.getBlockZ() <= zMax();
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
      volume.put("xmin", host.xMin());
      volume.put("xmax", host.xMax());
      volume.put("ymin", host.yMin());
      volume.put("ymax", host.yMax());
      volume.put("zmin", host.zMin());
      volume.put("zmax", host.zMax());
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
      int xmin = json.getAsJsonObject().get("xmin").getAsInt();
      int xmax = json.getAsJsonObject().get("xmax").getAsInt();
      int ymin = json.getAsJsonObject().get("ymin").getAsInt();
      int ymax = json.getAsJsonObject().get("ymax").getAsInt();
      int zmin = json.getAsJsonObject().get("zmin").getAsInt();
      int zmax = json.getAsJsonObject().get("zmax").getAsInt();
      Region host = new Region(parent, name, xmin, xmax, ymin, ymax, zmin, zmax);

      host.setPriority(json.getAsJsonObject().get("priority").getAsInt());

      // Settings
      host.putAll(SettingLibrary.deserializeSettingAssignments(json));

      return host;
    }
  }

  /**
   * Storage for Nope.
   * All read/write methods quickly open then close
   * a {@link StorageConnection}. If you use more
   * than one method at once, use {@link #open()}
   * and then call the same methods on that object.
   */
  public interface Storage {

    /**
     * Opens a connection for this Storage,
     * which stays open until it is closed.
     *
     * @return StorageConnection
     */
    StorageConnection open();

    default GlobalHost readGlobalHost(Host.HostSerializer<GlobalHost> serializer) throws HostParseException {
      return quickRead(connection -> connection.readGlobalHost(serializer));
    }

    default Collection<WorldHost> readWorldHosts(Host.HostSerializer<WorldHost> serializer) throws HostParseException {
      return quickRead(connection -> connection.readWorldHosts(serializer));
    }

    default Collection<Region> readRegions(Collection<WorldHost> parents,
                     Host.HostSerializer<Region> serializer)
        throws HostParseException {
      return quickRead(connection -> connection.readRegions(parents, serializer));
    }

    default void writeGlobalHost(GlobalHost globalHost, Host.HostSerializer<GlobalHost> serializer) {
      quickWrite(connection -> connection.writeGlobalHost(globalHost, serializer));
    }

    default void writeWorldHosts(Collection<WorldHost> worldHosts, Host.HostSerializer<WorldHost> serializer) {
      quickWrite(connection -> connection.writeWorldHosts(worldHosts, serializer));
    }

    default void writeRegions(Collection<Region> region, Host.HostSerializer<Region> serializer) {
      quickWrite(connection -> connection.writeRegions(region, serializer));
    }

    interface StorageConnection extends Storage, Closeable {

    }

    default <T> T quickRead(Function<StorageConnection, T> connectionConsumer) {
      if (this instanceof StorageConnection) {
        throw new AssertionError("You must override read/write methods in StorageConnection");
      }
      try (StorageConnection connection = this.open()) {
        return connectionConsumer.apply(connection);
      } catch (IOException e) {
        throw new HostParseException(e);
      }
    }

    default void quickWrite(Consumer<StorageConnection> connectionConsumer) {
      if (this instanceof StorageConnection) {
        throw new AssertionError("You must override read/write methods in StorageConnection");
      }
      try (StorageConnection connection = this.open()) {
        connectionConsumer.accept(connection);
      } catch (IOException e) {
        throw new HostParseException(e);
      }
    }

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
  public GlobalHost getGlobalHost() {
    return globalHost;
  }

  @Nullable
  public WorldHost getWorldHost(UUID worldUuid) {
    return worldHosts.get(worldUuid);
  }

  @Nullable
  public Region getRegion(String name) {
    for (WorldHost worldHost : worldHosts.values()) {
      Region region = worldHost.regionTree.get(name);
      if (region != null) return region;
    }
    return null;
  }

  @Nonnull
  @Override
  public Region addRegion(UUID worldUuid, String name, Vector3i pos1, Vector3i pos2, int priority) {
    if (Pattern.matches(worldHostNameRegex, name)) {
      throw new IllegalArgumentException(String.format(
              "Region insertion failed because the name %s has the name format of a WorldHost",
              name));
    }
    if (hasRegion(name)) {
      throw new IllegalArgumentException(String.format(
              "Region insertion failed because name %s already exists (in world %s)",
              name,
              Sponge.getServer()
                      .getAllWorldProperties()
                      .stream().filter(prop -> prop.getUniqueId().equals(worldUuid))
                      .findFirst().map(WorldProperties::getWorldName)
                      .orElse("unknown")));
    }
    Region region = new Region(worldUuid, name, pos1, pos2);
    region.setPriority(priority);
    Optional<Region> intersection = worldHosts.get(worldUuid).regionTree
            .volumes()
            .stream()
            .filter(other -> region.intersects(other) && region.getPriority() == other.getPriority())
            .findAny();
    if (intersection.isPresent()) {
      throw new IllegalArgumentException(String.format(
              "Region insertion failed because the new region %s and region %s have the same priority level: %d",
              region.getName(),
              intersection.get().getName(),
              region.getPriority()));
    }
    worldHosts.get(worldUuid).regionTree.push(name, region);  // Should return null
    regionToWorld.put(name, worldUuid);
    return region;
  }

  @Nonnull
  @Override
  public Region removeRegion(String name) {
    if (!hasRegion(name)) {
      throw new IllegalArgumentException(String.format(
              "Region deletion failed because name %s does not exist",
              name));
    }
    return worldHosts.get(regionToWorld.get(name)).regionTree.remove(name);
  }

  @Override
  public boolean hasRegion(String name) {
    return regionToWorld.containsKey(name);
  }

  public <V> V lookup(SettingLibrary.Setting<V> setting, Location<World> location) {
    // Maximum priority queue (swapped int compare)
    Queue<Host> maximumHeap = new PriorityQueue<>((h1, h2) ->
            Integer.compare(h2.getPriority(), h1.getPriority()));

    // add global
    if (globalHost.has(setting)) maximumHeap.add(globalHost);

    // add world
    Optional<WorldHost> worldHost = worldHosts.values()
            .stream()
            .filter(host -> host.encompasses(location))
            .findAny();
    if (worldHost.isPresent()) {
      if (worldHost.get().has(setting)) maximumHeap.add(worldHost.get());

      // add regions
      worldHost.get().regionTree
              .containingVolumes(location.getBlockX(), location.getBlockY(), location.getBlockZ())
              .stream().filter(host -> host.has(setting))
              .forEach(maximumHeap::add);
    }

    Host dictator = maximumHeap.peek();

    if (dictator == null) {
      return setting.getDefaultValue();
    }

    Optional<V> value = dictator.get(setting);
    if (!value.isPresent()) {
      // This shouldn't happen because we previously found that this host has this setting
      throw new RuntimeException("Error retrieving setting value");
    }

    return value.get();
  }

}
