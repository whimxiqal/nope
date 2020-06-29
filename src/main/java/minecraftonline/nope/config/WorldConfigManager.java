package minecraftonline.nope.config;

import minecraftonline.nope.config.supplier.ConfigLoaderSupplier;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.world.World;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class WorldConfigManager {
    private World world;
    private Path configDir;
    private ConfigContainer<CommentedConfigurationNode> worldConfig;
    private Map<String, ConfigContainer<CommentedConfigurationNode>> regionConfig;
    private ConfigLoaderSupplier configLoaderSupplier;

    public WorldConfigManager(Path configDir, World world, ConfigLoaderSupplier configLoaderSupplier) {
        this.configDir = configDir;
        this.world = world;
        this.configLoaderSupplier = configLoaderSupplier;
    }

    public void loadAll() {
        File worldDir = this.configDir.resolve(world.getName()).toFile();
        String worldConfName = this.world.getName() + ".conf";
        Path worldConf = worldDir.toPath().resolve(worldConfName);
        this.worldConfig = new ConfigContainer<>(configLoaderSupplier.createConfigLoader(worldConf));

        this.worldConfig.load();

        File[] regions = worldDir.listFiles((f, name) -> !name.equals(worldConfName));
        if (regions == null) {
            return;
        }

        for (File file : regions) {
            String regionId = file.getName().replace(".conf", "");
            loadRegion(regionId, file.toPath());
        }
    }

    public void saveAll() {
        worldConfig.save();
        regionConfig.values().forEach(ConfigContainer::save);
    }

    public Collection<ConfigContainer<CommentedConfigurationNode>> getAllRegions() {
        return this.regionConfig.values();
    }

    public ConfigContainer<CommentedConfigurationNode> getWorldConfig() {
        return this.worldConfig;
    }

    public Optional<ConfigContainer<CommentedConfigurationNode>> getRegion(String id) {
        return Optional.ofNullable(this.regionConfig.get(id));
    }

    private void loadRegion(String string, Path path) {
        ConfigContainer<CommentedConfigurationNode> regionConfig
                = new ConfigContainer<>(configLoaderSupplier.createConfigLoader(path));
        regionConfig.load();
        this.regionConfig.put(string, regionConfig);
    }
}
