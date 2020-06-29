package minecraftonline.nope.config.supplier;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.loader.HeaderMode;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;

import java.nio.file.Path;

public class HoconLoaderSupplier extends ConfigLoaderSupplier {
    public HoconLoaderSupplier(TypeSerializerCollection typeSerializerCollection) {
        super(typeSerializerCollection);
    }

    private HoconConfigurationLoader.Builder createConfigBuilder(Path path) {
        return HoconConfigurationLoader.builder()
                .setPath(path);
    }

    @Override
    public ConfigurationLoader<CommentedConfigurationNode> createConfigLoader(Path path) {
        return createConfigBuilder(path)
                .setDefaultOptions(ConfigurationOptions.defaults().setSerializers(typeSerializerCollection))
                .build();
    }

    @Override
    public ConfigurationLoader<CommentedConfigurationNode> createConfigLoader(Path path, String header) {
        ConfigurationOptions options = ConfigurationOptions.defaults()
                .setSerializers(typeSerializerCollection)
                .setHeader(header);
        return createConfigBuilder(path)
                .setDefaultOptions(options)
                .setHeaderMode(HeaderMode.PRESET)
                .build();
    }
}
