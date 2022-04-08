/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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
 */

package me.pietelite.nope.sponge.storage.yaml;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.sponge.config.SettingValueConfigSerializerRegistrar;
import me.pietelite.nope.sponge.storage.configurate.ConfigurateDataHandler;
import me.pietelite.nope.sponge.storage.configurate.DomainConfigurateDataHandler;
import me.pietelite.nope.sponge.storage.configurate.ZoneConfigurateDataHandler;
import me.pietelite.nope.sponge.storage.configurate.serializer.VolumeTypeSerializer;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

/**
 * The YAML implementation of the {@link ConfigurateDataHandler}.
 */
public class YamlDataHandler extends ConfigurateDataHandler {

  public YamlDataHandler(Path path, SettingValueConfigSerializerRegistrar serializerRegistrar) {
    super(new YamlConfig(path, serializerRegistrar),
        new DomainConfigurateDataHandler((name) -> yamlLoader(path.resolve(name + ".yml")),
            serializerRegistrar),
        new ZoneConfigurateDataHandler((name) -> yamlLoader(path.resolve("zones")
            .resolve(name + ".yml")),
            (name) -> path.resolve("zones")
                .resolve(name + ".yml"),
            () -> {
              File[] files = path.resolve("zones")
                  .toFile()
                  .listFiles();
              if (files == null) {
                return Collections.emptyList();
              } else {
                return Arrays.stream(files)
                    .filter(file -> {
                      String[] tokens = file.getName().split("\\.");
                      String type = tokens[tokens.length - 1].toLowerCase();
                      if (type.equals("yml") || type.equals("yaml")) {
                        return true;
                      } else {
                        Nope.instance().logger().error("File " + file.getName() + " is unknown");
                        return false;
                      }
                    })
                    .map(file -> yamlLoader(file.toPath()))
                    .collect(Collectors.toList());
              }
            },
            serializerRegistrar));
  }

  /**
   * Get a {@link YamlConfigurationLoader} at the given {@link Path}.
   *
   * @param path the path
   * @return the loader
   */
  public static YamlConfigurationLoader yamlLoader(Path path) {
    return YamlConfigurationLoader.builder()
        .defaultOptions(ConfigurationOptions.defaults()
            .serializers(builder -> builder.register(Volume.class, new VolumeTypeSerializer())))
        .indent(2)
        .nodeStyle(NodeStyle.BLOCK)
        .path(path)
        .build();
  }

}
