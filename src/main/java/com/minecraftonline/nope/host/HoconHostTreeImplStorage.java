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
import com.minecraftonline.nope.util.NopeTypeTokens;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HoconHostTreeImplStorage implements HostTreeImpl.Storage {

  private static final String REGION_CONFIG_FILENAME = "regions.conf";
  private static final String WORLD_SUB_REGIONS_KEY = "sub-regions";
  private final HoconConfigurationLoader loader;

  public HoconHostTreeImplStorage() {
    // These method calls threw errors, including the loader?
    final TypeSerializerCollection typeSerializerCollection = TypeSerializerCollection.create()
            .register(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN, new JsonElementSerializer());

    ConfigurationOptions options = ConfigurationOptions.defaults().withSerializers(typeSerializerCollection);

    Path regionConfig = Nope.getInstance().getConfigDir().resolve(REGION_CONFIG_FILENAME);
    try {
      if (regionConfig.toFile().createNewFile()) {
        Nope.getInstance().getLogger().info("No config file found. New config file created.");
      }
    } catch (IOException e) {
      throw new RuntimeException("Region config file was not found but could not be created.");
    }

    this.loader = HoconConfigurationLoader.builder()
            .setDefaultOptions(options)
            .setPath(regionConfig)
            .build();
  }

  @Override
  public HostTreeImpl.GlobalHost readGlobalHost(Host.HostSerializer<HostTreeImpl.GlobalHost> serializer) throws HostParseException {

    try (Connection connection = new Connection(loader)) {
      final JsonElement jsonElement = connection.node
              .getNode(Nope.GLOBAL_HOST_NAME)
              .getValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN);
      if (jsonElement == null) {
        return null;
      }
      return serializer.deserialize(jsonElement);
      // return GlobalHost
    } catch (IOException e) {
      throw new HostParseException("IOException when reading global host config", e);
    } catch (ObjectMappingException e) {
      throw new HostParseException("ObjectMappingException when trying read Global Host node", e);
    }
  }

  @Override
  public Collection<HostTreeImpl.WorldHost> readWorldHosts(Host.HostSerializer<HostTreeImpl.WorldHost> serializer) throws HostParseException {

    List<HostTreeImpl.WorldHost> worldHostList = new ArrayList<>();

    try (Connection connection = new Connection(loader)) {

      for (Map.Entry<Object, ? extends ConfigurationNode> entry : connection.node.getChildrenMap().entrySet()) {
        final String key = entry.getKey().toString();
        if (key.equals(Nope.GLOBAL_HOST_NAME)) {
          continue;
        }
        // Assume all other top level nodes are world regions.
        final JsonElement jsonElement = entry.getValue().getValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN);

        worldHostList.add(serializer.deserialize(jsonElement));
      }
      return worldHostList;
    } catch (IOException e) {
      throw new HostParseException("IOException when reading global host config", e);
    } catch (ObjectMappingException e) {
      throw new HostParseException("ObjectMappingException when trying read World Host node", e);
    }
  }

  @Override
  public Collection<HostTreeImpl.Region> readRegions(Collection<HostTreeImpl.WorldHost> parents, Host.HostSerializer<HostTreeImpl.Region> serializer) throws HostParseException {

    List<HostTreeImpl.Region> regions = new ArrayList<>();

    try (Connection connection = new Connection(loader)) {
      // return collection of regions
      for (HostTreeImpl.WorldHost worldHost : parents) {
        final ConfigurationNode worldNode = connection.node.getNode(worldHost.getName(), WORLD_SUB_REGIONS_KEY);

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : worldNode.getChildrenMap().entrySet()) {

          final HostTreeImpl.Region region = serializer.deserialize(entry.getValue()
                  .getValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN));
          regions.add(region);
        }
      }
    } catch (IOException e) {
      throw new HostParseException("IOException when reading global host config", e);
    } catch (ObjectMappingException e) {
      throw new HostParseException("ObjectMappingException when trying read Region Host node", e);
    }
    return regions;
  }

  @Override
  public void writeGlobalHost(HostTreeImpl.GlobalHost globalHost, Host.HostSerializer<HostTreeImpl.GlobalHost> serializer) {
    try (Connection connection = new Connection(loader)) {
      // write GlobalHost
      final ConfigurationNode node = connection.node.getNode(Nope.GLOBAL_HOST_NAME);
      node.setValue(null); // Blank it.
      final JsonElement element = serializer.serialize(globalHost);
      node.setValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN, element);
    } catch (IOException | ObjectMappingException e) {
      throw new HostParseException("Error writing globalhost", e);
    }
  }

  @Override
  public void writeWorldHosts(Collection<HostTreeImpl.WorldHost> worldHosts, Host.HostSerializer<HostTreeImpl.WorldHost> serializer) {

    try (Connection connection = new Connection(loader)) {
      // write collection of WorldHosts
      for (HostTreeImpl.WorldHost worldHost : worldHosts) {
        final ConfigurationNode node = connection.node.getNode(worldHost.getName());
        node.setValue(null); // Blank it.
        final JsonElement element = serializer.serialize(worldHost);
        node.setValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN, element);
      }
    } catch (IOException | ObjectMappingException e) {
      throw new HostParseException("Error writing world hosts", e);
    }
  }

  @Override
  public void writeRegions(Collection<HostTreeImpl.Region> regions, Host.HostSerializer<HostTreeImpl.Region> serializer) {

    try (Connection connection = new Connection(loader)) {
      // write collection of regions
      Set<String> worlds = regions.stream().map(region -> region.getParent().getName()).collect(Collectors.toSet());
      for (String world : worlds) {
        final ConfigurationNode node = connection.node.getNode(world, WORLD_SUB_REGIONS_KEY);
        node.setValue(null); // Blank it to stop deleted regions/settings from reappearing
      }
      for (HostTreeImpl.Region region : regions) {
        final String worldName = region.getParent().getName();
        worlds.add(worldName);
        final ConfigurationNode node = connection.node.getNode(worldName, WORLD_SUB_REGIONS_KEY, region.getName());

        node.setValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN, serializer.serialize(region));
      }
    } catch (IOException | ObjectMappingException e) {
      throw new HostParseException("Error saving config after writing regions", e);
    }
  }

  /**
   * Connection class for local Hocon implementation of HostTree storage
   * TODO finish implementing
   */
  public class Connection implements Closeable {
    private final ConfigurationNode node;

    public Connection(HoconConfigurationLoader loader) {
      try {
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
