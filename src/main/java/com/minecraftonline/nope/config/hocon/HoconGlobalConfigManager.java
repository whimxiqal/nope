package com.minecraftonline.nope.config.hocon;

import com.minecraftonline.nope.config.GlobalConfigManager;
import com.minecraftonline.nope.config.supplier.HoconLoaderSupplier;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;

import java.nio.file.Path;

public class HoconGlobalConfigManager extends GlobalConfigManager {

<<<<<<< HEAD
  public HoconGlobalConfigManager(Path configDir) {
    super(configDir, new HoconLoaderSupplier(getTypeSerializers()));
  }

  private static TypeSerializerCollection getTypeSerializers() {
    return ConfigurationOptions.defaults().getSerializers();
  }
=======
  public HoconGlobalConfigManager(Path configDir) {
    super(configDir, new HoconLoaderSupplier(GlobalConfigManager.getTypeSerializers()));
  }
>>>>>>> 9e0f8e2... Reworked region config
}
