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

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

public abstract class Host {

    /**
     * Final and private holder for all settings.
     * This should only be changed when adding and removing elements
     * through control methods here in Host.
     */
    private final Map<Setting<?>, Object> settings = Maps.newHashMap();

    public <T extends Serializable> void set(Setting<T> setting, T value) {
        settings.put(setting, value);
    }

    public <T extends Serializable> Optional<T> unset(Setting<T> setting) {
        Optional<T> out = getSettingValue(setting);
        out.ifPresent(unused -> settings.remove(setting));
        return out;
    }

    public Map<Setting<?>, ?> getSettingMap() {
        Map<Setting<?>, Object> copy = Maps.newHashMap();
        copy.putAll(settings);
        return copy;
    }

    public <T extends Serializable> Optional<T> getSettingValue(Setting<T> setting) {
        // TODO: make cast less sketchy
        return Optional.ofNullable((T) settings.get(setting));
    }

    /**
     * Get the Applicability that a setting needs to
     * be applicable to this Host.
     *
     * @return
     */
    @Nonnull
    public abstract Setting.Applicability getApplicability();
}
