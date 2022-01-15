/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Pieter Svenson
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

package com.minecraftonline.nope.common.settingnew;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

/**
 * An identifier for a setting. These can be set on a {@link com.minecraftonline.nope.common.host.Host}
 * in pairing with a {@link SettingValue}.
 *
 * @param <T> the type of data that can be retrieved under this setting at any location
 * @param <V> the type of value that wraps around the data stored at a specific host
 */
public abstract class SettingKey<T, V extends SettingValue<T>> {
  @Getter
  @Accessors(fluent = true)
  private final String id;
  @Getter
  @Accessors(fluent = true)
  private final Manager<T> manager;
  @Getter
  @Accessors(fluent = true)
  private final V defaultValue;
  @Getter
  @Accessors(fluent = true)
  private final V naturalValue;
  @Getter
  @Accessors(fluent = true)
  private final String description;
  @Getter
  @Accessors(fluent = true)
  private final String blurb;
  @Getter
  @Accessors(fluent = true)
  private final Category category;
  @Getter
  @Accessors(fluent = true)
  private final boolean implemented;
  @Getter
  @Accessors(fluent = true)
  private final boolean global;
  @Getter
  @Accessors(fluent = true)
  private final boolean playerRestrictive;


  SettingKey(String id, Manager<T> manager,
             V defaultValue, V naturalValue,
             String description, String blurb,
             Category category,
             boolean implemented,
             boolean global,
             boolean playerRestrictive) {
    this.id = id;
    this.manager = manager;
    this.defaultValue = defaultValue;
    this.naturalValue = naturalValue;
    this.description = description;
    this.blurb = blurb;
    this.category = category;
    this.implemented = implemented;
    this.global = global;
    this.playerRestrictive = playerRestrictive;
  }

  enum Category {
    BLOCKS,
    DAMAGE,
    ENTITIES,
    MISC,
    MOVEMENT,
    GLOBAL,
  }

  public static class Unary<T> extends SettingKey<T, SettingValue.Unary<T>> {

    Unary(String id, Manager<T> manager,
          T defaultData, T naturalValue,
          String description, String blurb,
          Category category,
          boolean implemented,
          boolean global, boolean playerRestrictive) {
      super(id, manager,
          SettingValue.Unary.of(defaultData), SettingValue.Unary.of(naturalValue),
          description, blurb,
          category,
          implemented,
          global, playerRestrictive);
    }

    public static class Builder<T> {
      private final String id;
      private final Manager<T> manager;
      private final T defaultValue;
      @Setter
      @Accessors(fluent = true)
      private T naturalValue;
      @Setter
      @Accessors(fluent = true)
      private String description = "";
      @Setter
      @Accessors(fluent = true)
      private String blurb = "";
      @Setter
      @Accessors(fluent = true)
      private Category category = Category.MISC;

      private boolean implemented = true;
      private boolean global = false;
      private boolean playerRestrictive = false;

      public Builder(String id, T defaultValue, Manager<T> manager) {
        this.id = id;
        this.manager = manager;
        this.defaultValue = defaultValue;
        this.naturalValue = defaultValue;
      }

      public Builder<T> notImplemented() {
        this.implemented = false;
        return this;
      }

      public Builder<T> global() {
        this.global = true;
        return this;
      }

      public Builder<T> playerRestrictive() {
        this.playerRestrictive = true;
        return this;
      }

      public SettingKey.Unary<T> build() {
        return new SettingKey.Unary<>(
            id, manager,
            defaultValue, naturalValue,
            description, blurb,
            category,
            implemented,
            global, playerRestrictive
        );
      }
    }
  }

  public static class Poly<T> extends SettingKey<Set<T>, SettingValue.Poly<T>> {

    Poly(String id, Manager<Set<T>> manager,
         Set<T> defaultData, Set<T> naturalValue,
         String description, String blurb,
         Category category,
         boolean implemented,
         boolean global, boolean playerRestrictive) {
      super(id, manager,
          SettingValue.Poly.additive(defaultData),
          SettingValue.Poly.additive(naturalValue),
          description, blurb,
          category,
          implemented,
          global, playerRestrictive);
    }

    public static class Builder<T> {
      private final String id;
      private final Manager<Set<T>> manager;
      private final Set<T> defaultValue;
      @Setter
      @Accessors(fluent = true)
      private Set<T> naturalValue;
      @Setter
      @Accessors(fluent = true)
      private String description = "";
      @Setter
      @Accessors(fluent = true)
      private String blurb = "";
      @Setter
      @Accessors(fluent = true)
      private Category category = Category.MISC;

      private boolean implemented = true;
      private boolean global = false;
      private boolean playerRestrictive = false;

      public Builder(String id, Set<T> defaultValue, Manager<Set<T>> manager) {
        this.id = id;
        this.manager = manager;
        this.defaultValue = defaultValue;
        this.naturalValue = defaultValue;
      }

      public void notImplemented() {
        this.implemented = false;
      }

      public void global() {
        this.global = true;
      }

      public void playerRestrictive() {
        this.playerRestrictive = true;
      }

      public SettingKey.Poly<T> build() {
        return new SettingKey.Poly<>(
            id, manager,
            defaultValue, naturalValue,
            description, blurb,
            category,
            implemented,
            global, playerRestrictive
        );
      }
    }
  }

  public static abstract class Manager<T> {

    public abstract Class<T> type() throws ParseSettingException;

    /**
     * Parse some data in some custom format.
     * Used for dealing with data from in-game usages of
     * declaring data.
     *
     * @param data string representation of data
     * @return the data object
     * @throws ParseSettingException if data cannot be parsed
     */
    public abstract T parse(String data) throws ParseSettingException;

    public final Object serializeData(Object data) {
      return serializeDataGenerified(cast(data));
    }

    protected Object serializeDataGenerified(T data) {
      return data;
    }

    public final Object deserializeData(Object serialized) throws ParseSettingException {
      return deserializeDataGenerified(serialized);
    }

    public T deserializeDataGenerified(Object serialized) throws ParseSettingException {
      return cast(serialized);
    }

    /**
     * Create a readable String version of the data.
     *
     * @param data the data to print
     * @return data in a readable form
     */
    @NotNull
    public String print(@NotNull T data) {
      return serializeDataGenerified(data).toString();
    }

    @NotNull
    public List<String> options() {
      return Collections.emptyList();
    }

    /**
     * Cast the object to this object's generic type.
     *
     * @param object the object to convert
     * @return the cast value
     */
    public final T cast(Object object) {
      if (!type().isInstance(object)) {
        throw new IllegalArgumentException(String.format(
            "input %s must be of type %s",
            object.getClass().getName(),
            type().getName()));
      }
      return type().cast(object);
    }

  }

  /**
   * An exception to throw when a setting could not be parsed.
   */
  public static class ParseSettingException extends IllegalArgumentException {
    public ParseSettingException() {
      super();
    }

    public ParseSettingException(String s) {
      super(s);
    }
  }

}
