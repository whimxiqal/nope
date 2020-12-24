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

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.config.configurate.serializer.JsonElementSerializer;
import com.minecraftonline.nope.util.NopeTypeTokens;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;

public class HoconHostTreeStorage implements HostTreeImpl.Storage {

  private static final String REGION_CONFIG_FILENAME = "regions.conf";
  private /* final */ HoconConfigurationLoader loader;

  public HoconHostTreeStorage() {
    // These method calls threw errors, including the loader?
//    final TypeSerializerCollection typeSerializerCollection = TypeSerializerCollection.defaults().newChild()
//            .register(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN, new JsonElementSerializer());
//
//    ConfigurationOptions options = ConfigurationOptions.defaults().withSerializers(typeSerializerCollection);
//
//    this.loader = HoconConfigurationLoader.builder()
//            .setDefaultOptions(options)
//            .setPath(Nope.getInstance().getConfigDir().resolve(REGION_CONFIG_FILENAME))
//            .build();
  }

  public Connection open() {
    try {
      return new Connection(loader.load());
    } catch (IOException e) {
      throw new HostParseException("Failed to save config file!", e);
    }
  }

  @Override
  public HostTreeImpl.GlobalHost readGlobalHost(Host.HostSerializer<HostTreeImpl.GlobalHost> serializer) throws HostParseException {
    // TODO implement
    ConfigurationNode globalHostConfigNode = null; // TODO get globalHostConfigNode?
    try (Connection connection = new Connection(globalHostConfigNode)) {
      // return GlobalHost
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Collection<HostTreeImpl.WorldHost> readWorldHosts(Host.HostSerializer<HostTreeImpl.WorldHost> serializer) throws HostParseException {
    // TODO implement
    ConfigurationNode worldHostConfigNode = null; // TODO get worldHostConfigNode?
    try (Connection connection = new Connection(worldHostConfigNode)) {
      // return collection of WorldHosts
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Collection<HostTreeImpl.Region> readRegions(Collection<HostTreeImpl.WorldHost> parents, Host.HostSerializer<HostTreeImpl.Region> serializer) throws HostParseException {
    // TODO implement
    ConfigurationNode regionConfigNode = null; // TODO get regionConfigNode?
    try (Connection connection = new Connection(regionConfigNode)) {
      // return collection of regions
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void writeGlobalHost(HostTreeImpl.GlobalHost globalHost, Host.HostSerializer<HostTreeImpl.GlobalHost> serializer) {
    // TODO implement
    ConfigurationNode globalHostConfigNode = null; // TODO get globalHostConfigNode?
    try (Connection connection = new Connection(globalHostConfigNode)) {
      // write GlobalHost
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void writeWorldHosts(Collection<HostTreeImpl.WorldHost> worldHosts, Host.HostSerializer<HostTreeImpl.WorldHost> serializer) {
    // TODO implement
    ConfigurationNode worldHostConfigNode = null; // TODO get worldHostConfigNode?
    try (Connection connection = new Connection(worldHostConfigNode)) {
      // write collection of WorldHosts
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void writeRegions(Collection<HostTreeImpl.Region> region, Host.HostSerializer<HostTreeImpl.Region> serializer) {
    // TODO implement
    ConfigurationNode regionConfigNode = null; // TODO get regionConfigNode?
    try (Connection connection = new Connection(regionConfigNode)) {
      // write collection of regions
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Connection class for local Hocon implementation of HostTree storage
   * TODO finish implementing
   */
  public class Connection implements Closeable {
    private final ConfigurationNode node;

    public Connection(ConfigurationNode node) {
      this.node = node;
    }

    @Override
    public void close() throws IOException {
      // TODO implement
    }
  }
}
