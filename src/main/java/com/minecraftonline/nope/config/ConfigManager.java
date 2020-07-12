package com.minecraftonline.nope.config;

import com.minecraftonline.nope.config.supplier.ConfigLoaderSupplier;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.nio.file.Path;

public abstract class ConfigManager {

	public static final String CONFIG_FILE_EXTENSION = ".conf";

	protected Path configDir;
	protected String configFileName;
	protected ConfigLoaderSupplier configLoaderSupplier;
	private ConfigContainer<CommentedConfigurationNode> config = null;

	/**
	 * Create a ConfigManager
	 *
	 * @param configDir            Path Directory to create config in
	 * @param configName           Filename excluding extension
	 * @param configLoaderSupplier ConfigLoaderSupplier A supplier for ConfigLoaders
	 */
	public ConfigManager(Path configDir, String configName, ConfigLoaderSupplier configLoaderSupplier) {
		this.configDir = configDir;
		this.configFileName = configName + CONFIG_FILE_EXTENSION;
		this.configLoaderSupplier = configLoaderSupplier;
	}

	public void loadAll() {
		this.config
				= new ConfigContainer<>(configLoaderSupplier.createConfigLoader(configDir.resolve(configFileName)));

		this.config.load();

		loadExtra();
	}

	public void saveAll() {
		this.config.save();

		saveExtra();
	}

	/**
	 * Load extra config or other things wanted to be loaded
	 * in {@link ConfigManager#loadAll()}
	 */
	protected void loadExtra() {
	}

	/**
	 * Save extra config or other things wanted to be saved
	 * in {@link ConfigManager#saveAll()}
	 */
	protected void saveExtra() {
	}

	public ConfigContainer<CommentedConfigurationNode> getConfig() {
		return this.config;
	}
}
