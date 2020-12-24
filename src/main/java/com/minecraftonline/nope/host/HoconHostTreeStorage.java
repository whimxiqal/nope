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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HoconHostTreeStorage implements HostTreeImpl.Storage {

  private static final String REGION_CONFIG_FILENAME = "regions.conf";
  private static final String WORLD_SUB_REGIONS_KEY = "sub-regions";
  private final HoconConfigurationLoader loader;

  public HoconHostTreeStorage() {
    // These method calls threw errors, including the loader?
    final TypeSerializerCollection typeSerializerCollection = TypeSerializerCollection.create()
            .register(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN, new JsonElementSerializer());

    ConfigurationOptions options = ConfigurationOptions.defaults().withSerializers(typeSerializerCollection);

    this.loader = HoconConfigurationLoader.builder()
            .setDefaultOptions(options)
            .setPath(Nope.getInstance().getConfigDir().resolve(REGION_CONFIG_FILENAME))
            .build();
  }

  @Override
  public HostTreeImpl.GlobalHost readGlobalHost(Host.HostSerializer<HostTreeImpl.GlobalHost> serializer) throws HostParseException {

    try (Connection connection = new Connection(loader)) {
      final JsonElement jsonElement = connection.node.getNode(Nope.GLOBAL_HOST_NAME).getValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN);
      if (jsonElement == null) {
        return null;
      }
      return serializer.deserialize(jsonElement);
      // return GlobalHost
    } catch (IOException | ObjectMappingException e) {
      throw new HostParseException("Error reading global host config", e);
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
    } catch (IOException | ObjectMappingException e) {
      throw new HostParseException("Error reading world hosts", e);
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

          final HostTreeImpl.Region region = serializer.deserialize(entry.getValue().getValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN));
          regions.add(region);
        }
      }
    } catch (IOException | ObjectMappingException e) {
      throw new HostParseException("Error saving config after reading regions", e);
    }
    return regions;
  }

  @Override
  public void writeGlobalHost(HostTreeImpl.GlobalHost globalHost, Host.HostSerializer<HostTreeImpl.GlobalHost> serializer) {
    try (Connection connection = new Connection(loader)) {
      // write GlobalHost
      final JsonElement element = serializer.serialize(globalHost);
      connection.node.getNode(Nope.GLOBAL_HOST_NAME).setValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN, element);
    } catch (IOException | ObjectMappingException e) {
      throw new HostParseException("Error writing globalhost", e);
    }
  }

  @Override
  public void writeWorldHosts(Collection<HostTreeImpl.WorldHost> worldHosts, Host.HostSerializer<HostTreeImpl.WorldHost> serializer) {

    try (Connection connection = new Connection(loader)) {
      // write collection of WorldHosts
      for (HostTreeImpl.WorldHost worldHost : worldHosts) {
        final JsonElement element = serializer.serialize(worldHost);
        connection.node.getNode(worldHost.getName()).setValue(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN, element);
      }
    } catch (IOException | ObjectMappingException e) {
      throw new HostParseException("Error writing world hosts", e);
    }
  }

  @Override
  public void writeRegions(Collection<HostTreeImpl.Region> regions, Host.HostSerializer<HostTreeImpl.Region> serializer) {

    try (Connection connection = new Connection(loader)) {
      // write collection of regions
      for (HostTreeImpl.Region region : regions) {
        final ConfigurationNode node = connection.node.getNode(region.getParent().getName(), WORLD_SUB_REGIONS_KEY, region.getName());

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
