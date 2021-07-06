package com.minecraftonline.nope.sponge.host;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.host.HostTree;
import com.minecraftonline.nope.common.setting.SettingLibrary;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.spongepowered.api.Sponge;

public class SpongeHostTree extends HostTree {

  @Override
  public void load(String location) throws IOException {

    // Setup worlds
    Sponge.server()
        .worldManager()
        .worlds()
        .forEach(serverWorld ->
            worldHosts.put(
                serverWorld.uniqueId(),
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
      throw new IOException("Nope's GlobalHost could not be read.", e);
    }

    // Read WorldHosts
    try {
      storage.readWorldHosts(location, new WorldHostSerializer()).forEach(worldHost ->
          worldHosts.put(worldHost.getWorldUuid(), worldHost));
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

  private WorldHost newWorldHost(UUID worldUuid) {
    return new WorldHost(Sponge.server()
        .worldManager().worlworldUuid)
        .map(prop -> worldNameConverter.apply(prop.getWorldName()))
        .orElseThrow(() -> new RuntimeException(String.format(
            "The worldUuid %s does not correspond to a Sponge world)",
            worldUuid))),
        worldUuid);
  }

  /**
   * A serializer for {@link WorldHost}s.
   */
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
              host.worldUuid))));
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

}
