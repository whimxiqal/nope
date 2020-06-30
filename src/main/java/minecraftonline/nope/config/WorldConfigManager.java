package minecraftonline.nope.config;

import minecraftonline.nope.config.supplier.ConfigLoaderSupplier;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.world.World;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WorldConfigManager extends ConfigManager {
    private World world;
    private Map<String, ConfigContainer<CommentedConfigurationNode>> regionConfig = new HashMap<>();

    public WorldConfigManager(Path configDir, World world, ConfigLoaderSupplier configLoaderSupplier) {
        super(configDir.resolve(world.getName()), world.getName(), configLoaderSupplier);
        this.world = world;
    }

    @Override
    public void loadExtra() {
        File[] regions = this.configDir.toFile().listFiles((f, name) -> !name.equals(this.configFileName));
        if (regions == null) {
            return;
        }

        for (File file : regions) {
            String regionId = file.getName().replace(ConfigManager.CONFIG_FILE_EXTENSION, "");
            loadRegion(regionId, file.toPath());
        }
    }

    @Override
    public void saveExtra() {
        regionConfig.values().forEach(ConfigContainer::save);
    }

    public Collection<ConfigContainer<CommentedConfigurationNode>> getAllRegions() {
        return this.regionConfig.values();
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
