package com.minecraftonline.nope.sponge.storage.yaml;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.math.Volume;
import com.minecraftonline.nope.sponge.storage.configurate.ConfigurateDataHandler;
import com.minecraftonline.nope.sponge.storage.configurate.DomainConfigurateDataHandler;
import com.minecraftonline.nope.sponge.storage.configurate.TemplateConfigurateDataHandler;
import com.minecraftonline.nope.sponge.storage.configurate.ZoneConfigurateDataHandler;
import com.minecraftonline.nope.sponge.storage.configurate.serializer.VolumeTypeSerializer;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class YamlDataHandler extends ConfigurateDataHandler {

  public YamlDataHandler(Path path) {
    super(new YamlConfig(path),
        new DomainConfigurateDataHandler((key) -> yamlLoader(path.resolve(key.namespace())
            .resolve(key.value() + ".yml"))),
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
            }),
        new TemplateConfigurateDataHandler(yamlLoader(path.resolve("templates.yml"))));
  }

  public static YamlConfigurationLoader yamlLoader(Path path) {
    return YamlConfigurationLoader.builder()
        .defaultOptions(ConfigurationOptions.defaults().serializers(builder ->
            builder.register(Volume.class, new VolumeTypeSerializer())))
        .indent(2)
        .nodeStyle(NodeStyle.BLOCK).path(path).build();
  }

}
