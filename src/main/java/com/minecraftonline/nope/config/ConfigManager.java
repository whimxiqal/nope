/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.nope.config;

import com.minecraftonline.nope.control.GlobalHost;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.WorldHost;
import jdk.internal.jline.internal.Nullable;
import org.spongepowered.api.world.World;

/**
 * A config manager with various methods to handle config loading, saving and updating.
 */
public interface ConfigManager {
  /**
   * Load everything
   */
  void loadAll();

  /**
   * Save everything
   */
  void saveAll();

  /**
   * From the data that was loaded, put it into the global host
   * @param host GlobalHost to fill data into
   */
  void fillSettings(GlobalHost host);

  /**
   * Loads a world if not already loaded
   * @param world World to load
   * @return Filled WorldHost or null if world already loaded
   */
  @Nullable
  WorldHost loadWorld(World world);

  /**
   * Lets the config know that a region has been created.
   * This should only be called when the user sends input to create a region, not somewhere
   * generic like add a region to a host, that could be loading config
   * Doesn't have to do anything, can just save everything on {@link #saveAll()}
   * @param worldHost WorldHost that the region was created in
   * @param name Name of the new region
   * @param region the new Region
   */
  default void onRegionCreate(WorldHost worldHost, String name, Region region) {}

  /**
   * Lets the config know that a region has changed
   * This should only be called when the user sends input to create a region, not somewhere
   * generic like set host setting, that could be loading config
   * @param worldHost WorldHost the region is in
   * @param name Name of the region
   * @param region The region
   * @param setting The setting whos value has changed
   */
  default void onRegionModify(WorldHost worldHost, String name, Region region, Setting<?> setting) {}

  /**
   * Lets to config know that a region has been removed
   * This should only be called when the user sends input to create a region, not somewhere
   * generic like removing a region from a host, that could be loading config/similar
   * @param worldHost WorldHost the region was in
   * @param name Name of the region
   */
  default void onRegionRemove(WorldHost worldHost, String name) {}
}
