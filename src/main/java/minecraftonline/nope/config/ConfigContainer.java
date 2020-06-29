package minecraftonline.nope.config;

import jdk.internal.jline.internal.Nullable;
import minecraftonline.nope.Nope;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Contains a ConfigurationLoader and provides access to easily load and save
 * @param <T>
 */
public class ConfigContainer<T extends ConfigurationNode> {
    private ConfigurationLoader<T> configurationLoader;
    private T configNode = null;

    public ConfigContainer(ConfigurationLoader<T> configurationLoader) {
        this.configurationLoader = configurationLoader;
    }

    /**
     * Loads a config
     * @throws IllegalStateException if there is an IOException
     * @return T for ease, although also stored, or null if loading failed
     */
    @Nullable
    public T load() {
        try {
            this.configNode = configurationLoader.load();
            return this.configNode;
        } catch (IOException e) {
            Nope.getInstance().getLogger().error("Error loading config", e);
        }
        return null;
    }

    /**
     * Saves a config
     * @throws IllegalStateException if there is an IOException
     */
    public void save() {
        if (configNode == null) {
            return; // Nothing loaded
        }
        try {
            configurationLoader.save(configNode);
        } catch (IOException e) {
            Nope.getInstance().getLogger().error("Error saving config", e);
        }
    }

    public ConfigurationLoader<T> getConfigurationLoader() {
        return configurationLoader;
    }

    /**
     * Gets the loaded config node
     * @throws IllegalStateException if load() has not been called, or did not succeed
     * @return T
     */
    public T getConfigNode() {
        return checkNotNull(configNode, "Attempt to get config node before it was loaded!");
    }
}
