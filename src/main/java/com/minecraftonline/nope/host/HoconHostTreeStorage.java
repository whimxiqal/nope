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

import java.io.IOException;
import java.util.Collection;

public class HoconHostTreeStorage implements HostTreeImpl.Storage {

  private static final String CONFIG_FILENAME = "regions.conf";
  private final HoconConfigurationLoader loader;

  public HoconHostTreeStorage() {
    final TypeSerializerCollection typeSerializerCollection = TypeSerializerCollection.defaults().newChild()
        .register(NopeTypeTokens.JSON_ELEMENT_TYPE_TOKEN, new JsonElementSerializer());

    ConfigurationOptions options = ConfigurationOptions.defaults().withSerializers(typeSerializerCollection);

    this.loader = HoconConfigurationLoader.builder()
        .setDefaultOptions(options)
        .setPath(Nope.getInstance().getConfigDir().resolve(CONFIG_FILENAME))
        .build();
  }

  public StorageConnection open() {
    try {
      return new HoconStorageConnection(loader.load());
    } catch (IOException e) {
      throw new HostParseException("Failed to save config file!", e);
    }
  }

  public static class HoconStorageConnection implements StorageConnection {

    private final ConfigurationNode node;

    public HoconStorageConnection(ConfigurationNode node) {
      this.node = node;
    }

    @Override
    public StorageConnection open() {
      return this; // TODO see if we can get rid of this. Maybe split up more somehow?
    }

    @Override
    public HostTreeImpl.GlobalHost readGlobalHost(Host.HostSerializer<HostTreeImpl.GlobalHost> serializer) throws HostParseException {
      return null; // TODO implement
    }

    @Override
    public Collection<HostTreeImpl.WorldHost> readWorldHosts(Host.HostSerializer<HostTreeImpl.WorldHost> serializer) throws HostParseException {
      return null; // TODO implement
    }

    @Override
    public Collection<HostTreeImpl.Region> readRegions(Collection<HostTreeImpl.WorldHost> parents, Host.HostSerializer<HostTreeImpl.Region> serializer) throws HostParseException {
      return null; // TODO implement
    }

    @Override
    public void writeGlobalHost(HostTreeImpl.GlobalHost globalHost, Host.HostSerializer<HostTreeImpl.GlobalHost> serializer) {
      // TODO implement
    }

    @Override
    public void writeWorldHosts(Collection<HostTreeImpl.WorldHost> worldHosts, Host.HostSerializer<HostTreeImpl.WorldHost> serializer) {
      // TODO implement
    }

    @Override
    public void writeRegions(Collection<HostTreeImpl.Region> region, Host.HostSerializer<HostTreeImpl.Region> serializer) {
      // TODO implement
    }

    @Override
    public void close() throws IOException {
      // TODO implement
    }
  }
}
