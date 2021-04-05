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

import com.google.gson.JsonElement;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.config.configurate.serializer.JsonElementSerializer;
import com.minecraftonline.nope.host.HostTreeImpl.GlobalHost;
import com.minecraftonline.nope.host.HostTreeImpl.WorldHost;
import com.minecraftonline.nope.util.NopeTypeTokens;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;

public class HoconHostTreeImplStorage implements HostTreeImpl.Storage {

  private static final String WORLD_SUB_ZONES_KEY = "sub-zones";

  @SuppressWarnings("UnstableApiUsage")
  private HoconConfigurationLoader getLoader(String fileName) {
    final TypeSerializerCollection typeSerializerCollection = TypeSerializerCollection.create()
        .register(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN, new JsonElementSerializer());

    ConfigurationOptions options = ConfigurationOptions.defaults()
        .withSerializers(typeSerializerCollection);

    Path zoneConfig = Nope.getInstance().getConfigDir().resolve(fileName);
    try {
      if (zoneConfig.toFile().createNewFile()) {
        Nope.getInstance().getLogger().info("No config file found. New config file created.");
      }
    } catch (IOException e) {
      throw new RuntimeException("Zone config file was not found but could not be created.");
    }

    return HoconConfigurationLoader.builder()
        .setDefaultOptions(options)
        .setPath(zoneConfig)
        .build();
  }

  @Override
  @SuppressWarnings("UnstableApiUsage")
  public GlobalHost readGlobalHost(String fileName, Host.HostSerializer<GlobalHost> serializer)
      throws IOException, HostParseException {
    try (Connection connection = new Connection(getLoader(fileName))) {
      final JsonElement jsonElement = connection.node
          .getNode(Nope.GLOBAL_HOST_NAME)
          .getValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN);
      if (jsonElement == null) {
        return null;
      }
      return serializer.deserialize(jsonElement);
      // return GlobalHost
    } catch (ObjectMappingException e) {
      throw new HostParseException("ObjectMappingException when trying "
          + "to read Global Host node", e);
    }
  }

  @Override
  @SuppressWarnings("UnstableApiUsage")
  public Collection<WorldHost> readWorldHosts(String fileName, Host.HostSerializer<WorldHost> serializer)
      throws IOException, HostParseException {

    try (Connection connection = new Connection(getLoader(fileName))) {
      return connection.node.getChildrenMap().entrySet()
          .stream()
          .filter(entry ->
              !entry.getKey().toString().equals(Nope.GLOBAL_HOST_NAME))
          .map(entry -> {
            // Assume all other top level nodes are world zones.
            try {
              return serializer.deserialize(entry.getValue()
                  .getValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN));
            } catch (ObjectMappingException e) {
              throw new HostParseException("ObjectMappingException when trying "
                  + "to read World Host node", e);
            }
          }).collect(Collectors.toList());
    }
  }

  @Override
  @SuppressWarnings("UnstableApiUsage")
  public Collection<HostTreeImpl.Zone> readZones(String fileName, Collection<WorldHost> parents,
                                                 Host.HostSerializer<HostTreeImpl.Zone> serializer)
      throws IOException, HostParseException {

    List<HostTreeImpl.Zone> zones = new ArrayList<>();
    try (Connection connection = new Connection(getLoader(fileName))) {
      // return collection of zones
      for (WorldHost worldHost : parents) {
        final ConfigurationNode worldNode = connection.node.getNode(worldHost.getName(), WORLD_SUB_ZONES_KEY);

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : worldNode.getChildrenMap().entrySet()) {
          try {
            final HostTreeImpl.Zone zone = serializer.deserialize(entry.getValue()
                .getValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN));
            zones.add(zone);
          } catch (IllegalArgumentException e) {
            Nope.getInstance().getLogger().error("Could not add zone", e);
          }
        }
      }
    } catch (ObjectMappingException e) {
      throw new HostParseException("ObjectMappingException when trying "
          + "to read Zone Host node", e);
    }
    return zones;
  }

  @Override
  @SuppressWarnings("UnstableApiUsage")
  public void writeGlobalHost(String fileName, GlobalHost globalHost,
                              Host.HostSerializer<GlobalHost> serializer)
      throws IOException, HostParseException {
    try (Connection connection = new Connection(getLoader(fileName))) {
      // write GlobalHost
      final ConfigurationNode node = connection.node.getNode(Nope.GLOBAL_HOST_NAME);
      node.setValue(null); // Blank it.
      final JsonElement element = serializer.serialize(globalHost);
      node.setValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN, element);
    } catch (ObjectMappingException e) {
      throw new HostParseException("Error writing globalhost", e);
    }
  }

  @Override
  @SuppressWarnings("UnstableApiUsage")
  public void writeWorldHosts(String fileName, Collection<WorldHost> worldHosts,
                              Host.HostSerializer<WorldHost> serializer)
      throws IOException, HostParseException {

    try (Connection connection = new Connection(getLoader(fileName))) {
      // write collection of WorldHosts
      for (WorldHost worldHost : worldHosts) {
        final ConfigurationNode node = connection.node.getNode(worldHost.getName());
        node.setValue(null); // Blank it.
        final JsonElement element = serializer.serialize(worldHost);
        node.setValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN, element);
      }
    } catch (ObjectMappingException e) {
      throw new HostParseException("Error writing world hosts", e);
    }
  }

  @Override
  @SuppressWarnings("UnstableApiUsage")
  public void writeZones(String fileName, Collection<HostTreeImpl.Zone> zones,
                         Host.HostSerializer<HostTreeImpl.Zone> serializer)
      throws IOException, HostParseException {

    try (Connection connection = new Connection(getLoader(fileName))) {
      // write collection of zones
      Set<String> worlds = zones.stream().map(zone ->
          zone.getParent().getName()).collect(Collectors.toSet());
      for (String world : worlds) {
        final ConfigurationNode node = connection.node.getNode(world, WORLD_SUB_ZONES_KEY);
        node.setValue(null); // Blank it to stop deleted zones/settings from reappearing
      }
      for (HostTreeImpl.Zone zone : zones) {
        final String worldName = zone.getParent().getName();
        worlds.add(worldName);
        final ConfigurationNode node = connection.node.getNode(worldName,
            WORLD_SUB_ZONES_KEY,
            zone.getName());

        node.setValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN, serializer.serialize(zone));
      }
    } catch (ObjectMappingException e) {
      throw new HostParseException("Error saving config after writing zones", e);
    }
  }

  /**
   * Connection class for local Hocon implementation of HostTree storage.
   */
  private class Connection implements Closeable {
    private final ConfigurationNode node;
    private final HoconConfigurationLoader loader;

    public Connection(HoconConfigurationLoader loader) {
      try {
        this.loader = loader;
        this.node = loader.load();
      } catch (IOException e) {
        throw new HostParseException(e);
      }
    }

    @Override
    public void close() throws IOException {
      loader.save(node);
    }
  }
}
