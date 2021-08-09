package com.minecraftonline.nope.sponge.host;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.minecraftonline.nope.common.host.GlobalHost;
import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.host.HostTree;
import com.minecraftonline.nope.common.host.WorldHost;
import com.minecraftonline.nope.common.setting.SettingLibrary;
import com.minecraftonline.nope.sponge.SpongeNope;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.server.ServerWorld;

public class SpongeHostTree extends HostTree {

  /**
   * Default constructor.
   *
   * @param storage            the type of storage to handle IO
   * @param globalHostName     the name given to the global host
   * @param worldNameConverter the converter with which to create the world host names
   * @param zoneNameRegex      the allowed regex for added zones, which should ensure
   */
  public SpongeHostTree(@NotNull Storage storage,
                        @NotNull String globalHostName,
                        @NotNull Function<String, String> worldNameConverter,
                        @NotNull String zoneNameRegex) {
    super(storage, globalHostName, worldNameConverter, zoneNameRegex);
  }

  @Override
  public void load(String location) throws IOException {

    // Setup worlds
    Sponge.server()
        .worldManager()
        .worlds()
        .forEach(serverWorld ->
            worldHosts.put(
                serverWorld.uniqueId(),
                newWorldHost(serverWorld)));

    // Read GlobalHost
    try {
      GlobalHost savedGlobalHost = storage.readGlobalHost(location, new GlobalHostSerializer());
      if (savedGlobalHost == null) {
        this.globalHost = new GlobalHost(SpongeNope.GLOBAL_HOST_NAME);
      } else {
        this.globalHost = savedGlobalHost;
      }
    } catch (IOException e) {
      throw new IOException("Nope's GlobalHost could not be read.", e);
    }

    // Read WorldHosts
    try {
      storage.readWorldHosts(location, new WorldHostSerializer()).forEach(worldHost ->
          worldHosts.put(worldHost.getWorldKey(), worldHost));
    } catch (IOException e) {
      throw new IOException("Nope's WorldHosts could not be read.", e);
    }

    // Read Zones
    try {
      storage.readZones(location, worldHosts.values(), new ZoneSerializer()).forEach(this::addZone);
    } catch (IOException e) {
      throw new IOException("Nope's Zones could not be read.", e);
    }

  }

  @Override
  public void save(String location) throws IOException {
    try {
      storage.writeGlobalHost(location, globalHost, new GlobalHostSerializer());
    } catch (IOException e) {
      throw new IOException("Nope's GlobalHost could not be written.", e);
    }

    try {
      storage.writeWorldHosts(location, worldHosts.values(), new WorldHostSerializer());
    } catch (IOException e) {
      throw new IOException("Nope's WorldHosts could not be written.", e);
    }

    for (WorldHost worldHost : worldHosts.values()) {
      try {
        storage.writeZones(location, worldHost.getZoneTree().volumes(), new ZoneSerializer());
      } catch (IOException e) {
        throw new IOException("Nope's Zones could not be written.", e);
      }
    }
  }

  private WorldHost newWorldHost(ServerWorld world) {
    return new WorldHost(world.properties().key().value(),
        world.uniqueId(),
        globalHost.getData(SettingLibrary.CACHE_SIZE),
        globalHost);
  }

  /**
   * A serializer for {@link WorldHost}s.
   */
  public class WorldHostSerializer implements Host.HostSerializer<WorldHost> {

    @Override
    public JsonElement serialize(WorldHost host) {
      Map<String, Object> serializedHost = Maps.newHashMap();
      serializedHost.put("settings", SettingLibrary.serializeSettingAssignments(host.getAll()));

      ResourceKey key = Sponge.server()
          .worldManager().worldKey(host.getWorldKey())
          .orElseThrow(() -> new RuntimeException(String.format(
              "WorldHost has invalid world UUID: %s",
              host.getWorldKey())));
      Map<String, String> worldMap = Maps.newHashMap();
      worldMap.put("namespace", key.namespace());
      worldMap.put("value", key.value());
      serializedHost.put("world", worldMap);

      return new Gson().toJsonTree(serializedHost);
    }

    @Override
    public WorldHost deserialize(JsonElement json) {
      ResourceKey key = ResourceKey.of(json.getAsJsonObject()
              .get("world").getAsJsonObject()
              .get("namespace").getAsString(),
          json.getAsJsonObject()
              .get("world").getAsJsonObject()
              .get("value").getAsString());
      WorldHost host = newWorldHost(Sponge.server()
          .worldManager().world(key)
          .orElseThrow(() -> new RuntimeException(String.format(
              "This JSON element for a WorldHost is storing an invalid World name '%s'",
              json.getAsJsonObject().get("world")))));

      // Settings
      host.putAll(SettingLibrary.deserializeSettingAssignments(json, host.getName()));

      // No zones are put here because they must be added manually when deserializing the zones

      return host;
    }
  }

}
