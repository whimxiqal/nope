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

package com.minecraftonline.nope.common.setting;

import com.google.common.collect.Streams;
import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.struct.Location;
import com.minecraftonline.nope.sponge.SpongeNope;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
  @Setter
  @NonNull
  @Accessors(fluent = true)
  private Manager<T, V> manager;
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


  SettingKey(String id, Manager<T, V> manager,
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

  public abstract T extractValue(@NotNull Collection<Host> hosts,
                                 @Nullable final UUID userUuid,
                                 @NotNull final Location location);

  public enum Category {
    BLOCKS,
    DAMAGE,
    ENTITIES,
    MISC,
    MOVEMENT,
    GLOBAL,
  }

  public static class Unary<T> extends SettingKey<T, SettingValue.Unary<T>> {

    Unary(String id, Manager<T, SettingValue.Unary<T>> manager,
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

    public static <X> Builder<X> builder(String id,
                                         X defaultValue,
                                         Manager<X, SettingValue.Unary<X>> manager) {
      return new Builder<>(id, defaultValue, manager);
    }

    @Override
    public T extractValue(@NotNull Collection<Host> hosts,
                          @Nullable final UUID userUuid,
                          @NotNull final Location location) {

      /* Choose a data structure that will optimize searching for highest priority matching */
      Queue<Host> hostQueue;
      Comparator<Host> descending = (h1, h2) -> Integer.compare(h2.priority(), h1.priority());
      if (hosts.size() >= 1) {
        hostQueue = new PriorityQueue<>(hosts.size(), descending);
        hostQueue.addAll(hosts);

        Host currentHost;
        Optional<Setting<T, SettingValue.Unary<T>>> currentSetting;
        Target currentTarget;
        boolean targeted = true;
        boolean targetSpecified = false;
        T data;

        // Assume targeted until target is set and specifically does not target us
        while (!hostQueue.isEmpty()) {
          currentHost = hostQueue.remove();
          currentSetting = currentHost.get(this);
          if (!currentSetting.isPresent()) {
            // This shouldn't happen because we previously found that this host has this setting
            throw new RuntimeException("Error retrieving setting value");
          }
          currentTarget = currentSetting.get().target();
          data = currentSetting.get().value().get();
          if (currentTarget != null) {
            // Ignore if target has already been specified: the last one takes precedence.
            if (!targetSpecified) {
              targeted = currentTarget.test(userUuid, playerRestrictive());
              targetSpecified = true;
            }
          }
          if (data != null) {
            if (targeted) {
              return data;
            } else {
              // We have found the data which was specifically not targeted.
              // So, pass through this data value and continue on anew.
              targeted = true;
              targetSpecified = false;
            }
          }
        }
      }

      // No more left, so just do default
      return defaultValue().get();
    }

    public static class Builder<T> {
      private final String id;
      private final Manager<T, SettingValue.Unary<T>> manager;
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

      private Builder(String id, T defaultValue, Manager<T, SettingValue.Unary<T>> manager) {
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

    Poly(String id, Manager<Set<T>, SettingValue.Poly<T>> manager,
         Set<T> defaultData, Set<T> naturalValue,
         String description, String blurb,
         Category category,
         boolean implemented,
         boolean global, boolean playerRestrictive) {
      super(id, manager,
          SettingValue.Poly.declarative(defaultData),
          SettingValue.Poly.declarative(naturalValue),
          description, blurb,
          category,
          implemented,
          global, playerRestrictive);
    }

    public static <X> Builder<X> builder(String id, Set<X> defaultValue, Manager<Set<X>, SettingValue.Poly<X>> manager) {
      return new Builder(id, defaultValue, manager);
    }

    public static <X> Builder<X> builderEmptyDefault(String id, Manager<Set<X>, SettingValue.Poly<X>> manager) {
      return new Builder<>(id, Collections.emptySet(), manager);
    }

    @Override
    public Set<T> extractValue(@NotNull Collection<Host> hosts, @Nullable UUID userUuid, @NotNull Location location) {
      /* Choose a data structure that will optimize searching for highest priority matching */
      Queue<Host> hostQueue;
      Comparator<Host> descending = (h1, h2) -> Integer.compare(h2.priority(), h1.priority());
      Stack<SettingValue.Poly<T>> values = new Stack<>();
      if (hosts.size() >= 1) {
        hostQueue = new PriorityQueue<>(hosts.size(), descending);
        hostQueue.addAll(hosts);

        Host currentHost;
        Optional<Setting<Set<T>, SettingValue.Poly<T>>> currentSetting;
        Target currentTarget;
        boolean targeted = true;
        boolean targetSpecified = false;
        SettingValue.Poly<T> value;

        // Assume targeted until target is set and specifically does not target us
        while (!hostQueue.isEmpty()) {
          currentHost = hostQueue.remove();
          currentSetting = currentHost.get(this);
          if (!currentSetting.isPresent()) {
            // This shouldn't happen because we previously found that this host has this setting
            throw new RuntimeException("Error retrieving setting value");
          }
          currentTarget = currentSetting.get().target();
          if (currentTarget != null) {
            // Ignore if target has already been specified: the last one takes precedence.
            if (!targetSpecified) {
              targeted = currentTarget.test(userUuid, playerRestrictive());
              targetSpecified = true;
            }
          }
          value = currentSetting.get().value();
          if (value != null) {
            if (targeted) {
              values.add(value);
              // TODO if we ever add a declarative type, it will ignore anything else.
              //  so, just stop here and continue on to the backwards traversal down below.
              //  (need to add a check method in SettingValue.Poly for declarative type)
            }
            // We have found data (whether targeting us or not)
            // So continue on anew.
            targeted = true;
            targetSpecified = false;
          }
        }
      }
      // lastly, put on the default value
      values.add(defaultValue());


      // now apply our values in the appropriate order
      Set<T> result = new HashSet<>();
      while (!values.isEmpty()) {
        values.pop().applyTo(result);
      }
      return result;
    }

    public static class Builder<T> {
      private final String id;
      private final Manager<Set<T>, SettingValue.Poly<T>> manager;
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

      private Builder(String id, Set<T> defaultValue, Manager<Set<T>, SettingValue.Poly<T>> manager) {
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

  public abstract static class Manager<T, V extends SettingValue<T>> {

    public abstract Class<T> dataType() throws ParseSettingException;

    public abstract Class<V> valueType() throws ParseSettingException;

    /**
     * Parse some data in some custom format.
     * Used for dealing with data from in-game usages of
     * declaring data.
     *
     * @param data string representation of data
     * @return the data object
     * @throws ParseSettingException if data cannot be parsed
     */
    public abstract T parseData(String data) throws ParseSettingException;

//    public final Object serializeValue(Object value) {
//      return serializeValueGenerified(castValue(value));
//    }
//
//    public abstract Object serializeValueGenerified(V value);
//
//    public final Object deserializeValue(Object serialized) throws ParseSettingException {
//      return deserializeValueGenerified(serialized);
//    }
//
//    public abstract V deserializeValueGenerified(Object serialized) throws ParseSettingException;

    @NotNull
    public abstract String printValue(@NotNull V value);

//    @NotNull
//    public String printData(@NotNull T data) {
//      return data.toString();
//    }

    public @NotNull Map<String, Object> elementOptions() {
      return Collections.emptyMap();
    }

    /**
     * Cast the object to this object's generic type.
     *
     * @param object the object to convert
     * @return the cast value
     */
    public final T castData(Object object) {
      if (!dataType().isInstance(object)) {
        throw new IllegalArgumentException(String.format(
            "input %s must be of type %s",
            object.getClass().getName(),
            dataType().getName()));
      }
      return dataType().cast(object);
    }

    public final V castValue(Object object) {
      if (!valueType().isInstance(object)) {
        throw new IllegalArgumentException(String.format(
            "input %s must be of type %s",
            object.getClass().getName(),
            valueType().getName()));
      }
      return valueType().cast(object);
    }

    public abstract V parseDeclarativeValue(String settingValue);

    public abstract static class Unary<T> extends Manager<T, SettingValue.Unary<T>> {

      @SuppressWarnings("unchecked")
      @Override
      public Class<SettingValue.Unary<T>> valueType() throws ParseSettingException {
        return (Class<SettingValue.Unary<T>>) (Class<?>) SettingValue.Unary.class;
      }

      public final SettingValue.Unary<T> parseValue(String data) throws ParseSettingException {
        return SettingValue.Unary.of(parseData(data));
      }

      @Override
      public final SettingValue.Unary<T> parseDeclarativeValue(String settingValue) {
        return parseValue(settingValue);
      }

      @Override
      public @NotNull String printValue(SettingValue.@NotNull Unary<T> value) {
        return value.get().toString();
      }
    }

    public abstract static class Poly<T> extends Manager<Set<T>, SettingValue.Poly<T>> {
      public static final String SET_SPLIT_REGEX = "(?<![ ,])(( )+|( *, *))(?![ ,])";

      @SuppressWarnings("unchecked")
      @Override
      public Class<SettingValue.Poly<T>> valueType() throws ParseSettingException {
        return (Class<SettingValue.Poly<T>>) (Class<?>) SettingValue.Poly.class;
      }

      @SuppressWarnings("unchecked")
      @Override
      public final Class<Set<T>> dataType() throws ParseSettingException {
        return (Class<Set<T>>) (Class<?>) Set.class;
      }

      @Override
      public SettingValue.Poly<T> parseDeclarativeValue(String settingValue) {
        throw new UnsupportedOperationException();
      }

      @Override
      public final Set<T> parseData(String data) throws ParseSettingException {
        Set<T> set = new HashSet<>();
        SpongeNope.instance().logger().info("parseData... : " + data);
        for (String token : data.split(SET_SPLIT_REGEX)) {
          SpongeNope.instance().logger().info("Parsing data: token: " + token);
          try {
            set.add(parseElement(token));
          } catch (IllegalArgumentException ex) {
            StringBuilder errorMessage = new StringBuilder(token + " is not a valid element. ");
            Set<String> options = elementOptions().keySet();
            if (options.size() > 0) {
              if (options.size() <= 8) {
                errorMessage.append("Allowed types: ")
                    .append(options.stream()
                        .map(e -> e.toString().toLowerCase())
                        .collect(Collectors.joining(", ")));
              } else {
                errorMessage.append("Allowed types: ")
                    .append(options.stream()
                        .limit(8)
                        .map(e -> e.toString().toLowerCase())
                        .collect(Collectors.joining(", ")))
                    .append(" ...");
              }
            }
            throw new SettingKey.ParseSettingException(errorMessage.toString());
          }
        }
        return set;
      }

      @Override
      public @NotNull String printValue(SettingValue.@NotNull Poly<T> value) {
        if (value.declarative()) {
          if (value.additive().size() == 0) {
            return "(None)";
          } else {
            return value.additive()
                .stream()
                .map(this::printElement)
                .collect(Collectors.joining(", "));
          }
        } else {
          return Streams.concat(value.additive()
              .stream()
              .map(this::printElement)
                  .map(element -> "+" + element),
              value.subtractive()
              .stream()
              .map(this::printElement)
                  .map(element -> "!" + element))
              .collect(Collectors.joining(", "));
        }
      }

      public abstract String printElement(T element);

      public abstract T parseElement(String element) throws ParseSettingException;

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
