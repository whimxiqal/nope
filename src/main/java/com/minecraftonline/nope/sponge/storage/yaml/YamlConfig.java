package com.minecraftonline.nope.sponge.storage.yaml;

import com.minecraftonline.nope.sponge.api.config.SettingValueConfigSerializerRegistrar;
import com.minecraftonline.nope.sponge.storage.configurate.UniverseConfigurateDataHandler;
import java.nio.file.Path;

public class YamlConfig extends UniverseConfigurateDataHandler {

  public YamlConfig(Path path, SettingValueConfigSerializerRegistrar serializerRegistrar) {
    super(YamlDataHandler.yamlLoader(path.resolve("server.yml")),
        serializerRegistrar);
  }

}
