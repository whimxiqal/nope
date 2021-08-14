package com.minecraftonline.nope.sponge.storage.yaml;

import com.minecraftonline.nope.sponge.storage.configurate.ConfigurateDataHandler;
import com.minecraftonline.nope.sponge.storage.configurate.DomainConfigurateDataHandler;
import com.minecraftonline.nope.sponge.storage.configurate.TemplateConfigurateDataHandler;
import com.minecraftonline.nope.sponge.storage.configurate.UniverseConfigurateDataHandler;
import com.minecraftonline.nope.sponge.storage.configurate.ZoneConfigurateDataHandler;
import java.nio.file.Path;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class YamlDataHandler extends ConfigurateDataHandler {

  public YamlDataHandler(Path path) {
    super(new UniverseConfigurateDataHandler(YamlConfigurationLoader.builder()
            .path(path.resolve("server.yml"))
            .indent(2)
            .nodeStyle(NodeStyle.BLOCK)
            .build()),
        new DomainConfigurateDataHandler((key) -> YamlConfigurationLoader.builder()
            .path(path.resolve(key.namespace()).resolve(key.value()).resolve(key.value() + ".yaml"))
            .indent(2)
            .nodeStyle(NodeStyle.BLOCK)
            .build()),
        new ZoneConfigurateDataHandler((domainKey, name) -> YamlConfigurationLoader.builder()
            .path(path.resolve(domainKey.namespace()).resolve(domainKey.value()).resolve("zones").resolve(name + ".yaml"))
            .indent(2)
            .nodeStyle(NodeStyle.BLOCK)
            .build(),
            (domainKey, name) -> path.resolve(domainKey.namespace()).resolve(domainKey.value()).resolve("zones").resolve(name + ".yaml"), acquirer),
        new TemplateConfigurateDataHandler(YamlConfigurationLoader.builder()
            .path(path.resolve("templates.yml"))
            .indent(2)
            .nodeStyle(NodeStyle.BLOCK)
            .build()));
  }

}
