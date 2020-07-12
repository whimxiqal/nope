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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minecraftonline.nope.util.Validate;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@CatalogedBy(Settings.class)
public class Setting<T extends Serializable> implements CatalogType, Serializable {

    enum Applicability {
        REGION,
        WORLD,
        GLOBAL
    }

    private final String id;
    private String path;
    private final T defaultValue;
    private final Class<T> clazz;
    @Nullable
    private String description;
    @Nullable
    private String comment;
    private Set<Applicability> applicability = Sets.newEnumSet(Lists.newArrayList(), Applicability.class);

    protected Setting(@Nonnull String id,
                      @Nonnull T defaultValue,
                      @Nonnull Class<T> clazz) {
        Preconditions.checkNotNull(id);
        Validate.checkKebabCase(
                id,
                "Invalid Setting id: " + id + ". Valid ids only contain characters 'a-z' and '-'.");
        Preconditions.checkNotNull(defaultValue);
        Preconditions.checkNotNull(clazz);
        this.id = id;
        this.defaultValue = defaultValue;
        this.clazz = clazz;
    }

    /**
     * Factory generator instead of a class.
     *
     * @param id           The readable String id
     * @param defaultValue The default value
     * @param clazz        The class object representing the type of value this setting stores.
     *                     This is used so {@link com.google.common.reflect.TypeToken}s can be made
     * @param <S>          The type of value stored
     * @return The generated Setting object
     */
    public static <S extends Serializable> Setting<S> of(@Nonnull String id,
                                                         @Nonnull S defaultValue,
                                                         @Nonnull Class<S> clazz) {
        return new Setting<>(id, defaultValue, clazz);
    }

    /**
     * Generic getter.
     *
     * @return The default value
     */
    public T getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Optional getter.
     *
     * @return An optional of the description
     */
    public Optional<String> getDescription() {
        return Optional.ofNullable(this.description);
    }

    /**
     * A setter.
     *
     * @param description The description of the setting.
     *                    If no comment has been set, it will be set as this description.
     * @return The same Setting, for chaining
     */
    public Setting<T> withDescription(@Nullable String description) {
        this.description = description;
        if (!getComment().isPresent()) {
            this.comment = description;
        }
        return this;
    }

    /**
     * Optional getter.
     *
     * @return An optional of the comment
     */
    public Optional<String> getComment() {
        return Optional.ofNullable(this.comment);
    }

    /**
     * A setter.
     *
     * @param comment The comment to be shown on configuration pages
     * @return The same Setting, for chaining
     */
    public Setting<T> withComment(@Nullable String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * Determines whether this Setting is applicable somehow.
     *
     * @return true if this Setting applies in the appropriate way
     */
    public boolean isApplicable(Applicability applicability) {
        return this.applicability.contains(applicability);
    }

    /**
     * A setter. By default, it's `GLOBAL` but this method also erases all other Applicabilities
     * prior to setting values.
     *
     * @param applicability `GLOBAL` if this Setting applies to the entire server,
     *                      `WORLD` if this Setting applies to individual worlds,
     *                      `REGION` if this Setting applies to individual regions
     * @return The same Setting, for chaining
     */
    public Setting<T> withApplicability(Applicability... applicability) {
        this.applicability.clear();
        this.applicability.addAll(Arrays.asList(applicability));
        return this;
    }

    public boolean isConfigurable() {
        return getConfigurationPath().isPresent();
    }

    public Optional<String> getConfigurationPath() {
        return Optional.ofNullable(this.path);
    }

    public Setting<T> withConfigurationPath(String path) {
        Validate.checkConfigFormat(path,
                "Invalid configuration path for Setting: "
                        + path
                        + ". Valid ids only contain characters 'a-z', '-', and '.'.");
        this.path = path;
        return this;
    }

    /**
     * Generic getter.
     *
     * @return The class of the type stored in this Setting.
     * Generally this is used for deserialization purposes.
     */
    public Class<T> getTypeClass() {
        return this.clazz;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Setting setting = (Setting) obj;
        return this.id.equals(setting.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
