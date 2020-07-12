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
 *
 */

package com.minecraftonline.nope.control;

import com.minecraftonline.nope.Nope;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class GlobalHost extends Host {
    private final Map<World, WorldHost> worlds = new HashMap<>();

    /**
     * Adds a world if it is not present.
     * Does nothing if world is already added
     *
     * @param world World to add
     *              =
     */
    public void addWorldIfNotPresent(World world) {
        worlds.computeIfAbsent(world, k -> {
            WorldHost host = new WorldHost(world.getUniqueId());
            Nope.getInstance().getGlobalConfigManager().loadAdditionalWorld(world);
            return host;
        });
    }

    public WorldHost getWorld(World world) {
        return this.worlds.get(world);
    }

    public Map<World, WorldHost> getWorlds() {
        return this.worlds;
    }

    @Nonnull
    @Override
    public Setting.Applicability getApplicability() {
        return Setting.Applicability.GLOBAL;
    }
}
