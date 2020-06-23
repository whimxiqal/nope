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

package minecraftonline.nope.control;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import minecraftonline.nope.util.Validate;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@CatalogedBy(Parameters.class)
public class Parameter<T extends Serializable> implements CatalogType, Serializable {

  enum Applicability {
    REGION,
    WORLD,
    GLOBAL
  }

  private final String id;
  private String path;
  private T value;
  private final T defaultValue;
  private final Class<T> clazz;
  private String description;
  private String comment;
  private Set<Applicability> applicability = Sets.newEnumSet(Lists.newArrayList(), Applicability.class);

  protected Parameter(@Nonnull String id,
                      @Nonnull T defaultValue,
                      @Nonnull Class<T> clazz) {
    Preconditions.checkNotNull(id);
    Validate.checkKebabCase(
        id,
        "Invalid Parameter id: " + id + ". Valid ids only contain characters 'a-z' and '-'.");
    Preconditions.checkNotNull(defaultValue);
    Preconditions.checkNotNull(clazz);
    this.id = id;
    this.value = defaultValue;
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
   * @return The generated Parameter object
   */
  public static <S extends Serializable> Parameter<S> of(@Nonnull String id,
                                                         @Nonnull S defaultValue,
                                                         @Nonnull Class<S> clazz) {
    return new Parameter<>(id, defaultValue, clazz);
  }

  /**
   * Optional getter.
   *
   * @return An optional of the value
   */
  public Optional<T> getValue() {
    return Optional.ofNullable(this.value);
  }

  /**
   * Generic setter.
   *
   * @param value The value to store
   */
  public void setValue(@Nullable T value) {
    this.value = value;
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
   * @return The same Parameter, for chaining
   */
  public Parameter<T> withDescription(@Nullable String description) {
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
   * @return The same Parameter, for chaining
   */
  public Parameter<T> withComment(@Nullable String comment) {
    this.comment = comment;
    return this;
  }

  /**
   * Determines whether this Parameter is applicable somehow.
   *
   * @return true if this Parameter applies in the appropriate way
   */
  public boolean isApplicable(Applicability applicability) {
    return this.applicability.contains(applicability);
  }

  /**
   * A setter. By default, it's `GLOBAL` but this method also erases all other Applicabilities
   * prior to setting values.
   *
   * @param applicability `GLOBAL` if this Parameter applies to the entire server,
   *                      `WORLD` if this Parameter applies to individual worlds,
   *                      `REGION` if this Parameter applies to individual regions
   * @return The same Parameter, for chaining
   */
  public Parameter<T> withApplicability(Applicability... applicability) {
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

  public Parameter<T> withConfigurationPath(String path) {
    Validate.checkConfigFormat(path,
        "Invalid configuration path for Parameter: "
            + path
            + ". Valid ids only contain characters 'a-z', '-', and '.'.");
    this.path = path;
    return this;
  }

  /**
   * Generic getter.
   *
   * @return The class of the type stored in this Parameter.
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

}
