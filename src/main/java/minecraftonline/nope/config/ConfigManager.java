package minecraftonline.nope.config;

import minecraftonline.nope.config.supplier.ConfigLoaderSupplier;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public abstract class ConfigManager {
    private Map<World, WorldConfigManager> worldConfigs;
    protected Path configDir;
    private ConfigContainer<CommentedConfigurationNode> globalConfig;

    protected ConfigLoaderSupplier configLoaderSupplier;
    private ConfigurationOptions configurationOptions;

    public ConfigManager(Path configDir, ConfigLoaderSupplier configLoaderSupplier) {
        this.configDir = configDir;
        this.configLoaderSupplier = configLoaderSupplier;
    }

    public void loadAll() {
        this.globalConfig
                = new ConfigContainer<>(configLoaderSupplier.createConfigLoader(configDir.resolve("global.conf")));

        this.globalConfig.load();

        for (World world : Sponge.getServer().getWorlds()) {
            WorldConfigManager worldConfigManager = new WorldConfigManager(configDir, world, configLoaderSupplier);
            worldConfigManager.loadAll();
            worldConfigs.put(world, worldConfigManager);
        }
    }

    public void saveAll() {
        this.globalConfig.save();
        worldConfigs.values().forEach(WorldConfigManager::saveAll);
    }

    public WorldConfigManager getWorldManager(World world) {
        return this.worldConfigs.get(world);
    }

    public ConfigContainer<CommentedConfigurationNode> getWorldConfig(World world) {
        return this.worldConfigs.get(world).getWorldConfig();
    }

    public Optional<ConfigContainer<CommentedConfigurationNode>> getRegionConfig(World world, String id) {
        return getWorldManager(world).getRegion(id);
    }
}
