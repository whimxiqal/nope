package minecraftonline.nope.config.supplier;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;

import java.nio.file.Path;

/**
 * Supplies a config loader. This should be extended for formats such as HOCON and YAML if needed.
 */
public abstract class ConfigLoaderSupplier {
    protected TypeSerializerCollection typeSerializerCollection;

    protected ConfigLoaderSupplier(TypeSerializerCollection typeSerializerCollection) {
        this.typeSerializerCollection = typeSerializerCollection;
    }

    public abstract ConfigurationLoader<CommentedConfigurationNode> createConfigLoader(Path path);

    public abstract ConfigurationLoader<CommentedConfigurationNode> createConfigLoader(Path path, String header);
}
