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
 */

package com.minecraftonline.nope.common.setting;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.struct.AltSet;
import com.minecraftonline.nope.common.struct.HashAltSet;
import com.minecraftonline.nope.common.struct.Location;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An identifier for a setting. These can be set on a {@link Host}
 * in pairing with a {@link SettingValue}.
 *
 * @param <T> the type of data that can be retrieved under this setting at any location
 * @param <V> the type of value that wraps around the data stored at a specific host
 */
@Getter
@Accessors(fluent = true)
public abstract class SettingKey<T,
    V extends SettingValue<T>,
    M extends SettingKey.Manager<T, V>> {
  private final String id;
  private final M manager;
  private final T defaultData;
  private final T naturalData;
  private final String description;
  private final String blurb;
  private final Category category;
  private final boolean global;
  private final boolean playerRestrictive;
  @Setter
  private boolean functional;

  SettingKey(String id, @NotNull M manager,
             T defaultData, T naturalData,
             String description, String blurb,
             Category category,
             boolean functional,
             boolean global,
             boolean playerRestrictive) {
    this.id = id;
    this.manager = manager;
    this.defaultData = defaultData;
    this.naturalData = naturalData;
    this.description = description;
    this.blurb = blurb;
    this.category = category;
    this.functional = functional;
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

  public static class Unary<T> extends SettingKey<T, SettingValue.Unary<T>, SettingKey.Manager.Unary<T>> {

    Unary(String id, Manager.Unary<T> manager,
          T defaultData, T naturalValue,
          String description, String blurb,
          Category category,
          boolean functional,
          boolean global, boolean playerRestrictive) {
      super(id, manager,
          defaultData, naturalValue,
          description, blurb,
          category,
          functional,
          global, playerRestrictive);
    }

    public static <X> Builder<X> builder(String id,
                                         X defaultValue,
                                         Manager.Unary<X> manager) {
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
      return defaultData();
    }

    public T getDataOrDefault(Host host) {
      return host.getValue(this).map(SettingValue.Unary::get).orElse(this.defaultData());
    }

    public static class Builder<T> {
      private final String id;
      private final Manager.Unary<T> manager;
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

      private boolean functional = false;
      private boolean global = false;
      private boolean playerRestrictive = false;

      private Builder(String id, T defaultValue, Manager.Unary<T> manager) {
        this.id = id;
        this.manager = manager;
        this.defaultValue = defaultValue;
        this.naturalValue = defaultValue;
      }

      public Builder<T> functional() {
        this.functional = true;
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
            functional,
            global, playerRestrictive
        );
      }
    }
  }

  public static class Poly<T, S extends AltSet<T>> extends SettingKey<S,
      SettingValue.Poly<T, S>,
      SettingKey.Manager.Poly<T, S>> {

    Poly(String id, Manager.Poly<T, S> manager,
         S defaultData, S naturalValue,
         String description, String blurb,
         Category category,
         boolean functional,
         boolean global, boolean playerRestrictive) {
      super(id, manager,
          defaultData,
          naturalValue,
          description, blurb,
          category,
          functional,
          global, playerRestrictive);
    }

    public static <X, Y extends HashAltSet<X>> Builder<X, Y> builder(String id,
                                                                     Y defaultData,
                                                                     Manager.Poly<X, Y> manager) {
      return new Builder<>(id, defaultData, manager);
    }

    @Override
    public S extractValue(@NotNull Collection<Host> hosts,
                          @Nullable UUID userUuid,
                          @NotNull Location location) {
      /* Choose a data structure that will optimize searching for highest priority matching */
      Queue<Host> hostQueue;
      Comparator<Host> descending = (h1, h2) -> Integer.compare(h2.priority(), h1.priority());
      Stack<SettingValue.Poly<T, S>> values = new Stack<>();
      if (hosts.size() >= 1) {
        hostQueue = new PriorityQueue<>(hosts.size(), descending);
        hostQueue.addAll(hosts);

        Host currentHost;
        Optional<Setting<S, SettingValue.Poly<T, S>>> currentSetting;
        Target currentTarget;
        boolean targeted = true;
        boolean targetSpecified = false;
        SettingValue.Poly<T, S> value;

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


      // now apply our values in the appropriate order
      S result = manager().copySet(defaultData());
      while (!values.isEmpty()) {
        result = manager().copySet(values.pop().applyTo(result));
      }
      return result;
    }

    public static class Builder<T, S extends HashAltSet<T>> {
      private final String id;
      private final Manager.Poly<T, S> manager;
      private final S defaultData;
      @Setter
      @Accessors(fluent = true)
      private S naturalData;
      @Setter
      @Accessors(fluent = true)
      private String description = "";
      @Setter
      @Accessors(fluent = true)
      private String blurb = "";
      @Setter
      @Accessors(fluent = true)
      private Category category = Category.MISC;

      private boolean functional = false;
      private boolean global = false;
      private boolean playerRestrictive = false;

      private Builder(String id, S defaultData, Manager.Poly<T, S> manager) {
        this.id = id;
        this.manager = manager;
        this.defaultData = defaultData;
        this.naturalData = defaultData;
      }

      public Builder<T, S> functional() {
        this.functional = true;
        return this;
      }

      public Builder<T, S> global() {
        this.global = true;
        return this;
      }

      public Builder<T, S> playerRestrictive() {
        this.playerRestrictive = true;
        return this;
      }

      public SettingKey.Poly<T, S> build() {
        return new SettingKey.Poly<>(
            id, manager,
            defaultData, naturalData,
            description, blurb,
            category,
            functional,
            global, playerRestrictive
        );
      }
    }
  }

  public abstract static class Manager<T, V extends SettingValue<T>> {

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
    public abstract String printData(@NotNull T value);

    @NotNull
    public abstract String printValue(@NotNull V value);

    public @NotNull Map<String, Object> elementOptions() {
      return Collections.emptyMap();
    }

    public abstract V parseDeclarativeValue(String settingValue);

    public abstract static class Unary<T> extends Manager<T, SettingValue.Unary<T>> {

      @SuppressWarnings("unchecked")
      public abstract Class<T> dataType() throws ParseSettingException;

      public final SettingValue.Unary<T> parseValue(String data) throws ParseSettingException {
        return SettingValue.Unary.of(parseData(data));
      }

      public abstract T parseData(String data) throws ParseSettingException;

      @Override
      public final SettingValue.Unary<T> parseDeclarativeValue(String settingValue) {
        return parseValue(settingValue);
      }

      @Override
      @NotNull
      public final String printValue(SettingValue.@NotNull Unary<T> value) {
        return printData(value.get());
      }

      @Override
      public @NotNull String printData(@NotNull T value) {
        return value.toString();
      }
    }

    public abstract static class Poly<T, S extends AltSet<T>> extends Manager<S, SettingValue.Poly<T, S>> {
      public static final String SET_SPLIT_REGEX = "(?<![ ,])(( )+|( *, *))(?![ ,])";

      @SuppressWarnings("unchecked")
      @Override
      public SettingValue.Poly<T, S> parseDeclarativeValue(String settingValue) {
        throw new UnsupportedOperationException();
      }

      public final Set<T> parseSet(String data) throws ParseSettingException {
        Set<T> set = new HashSet<>();
        for (String token : data.split(SET_SPLIT_REGEX)) {
          try {
            set.add(parseElement(token));
          } catch (IllegalArgumentException ex) {
            StringBuilder errorMessage = new StringBuilder(token + " is not a valid element. ");
            Set<String> options = elementOptions().keySet();
            if (options.size() > 0) {
              if (options.size() <= 8) {
                errorMessage.append("Allowed types: ")
                    .append(options.stream()
                        .map(e -> e.toLowerCase())
                        .collect(Collectors.joining(", ")));
              } else {
                errorMessage.append("Allowed types: ")
                    .append(options.stream()
                        .limit(8)
                        .map(e -> e.toLowerCase())
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
      public @NotNull String printValue(SettingValue.@NotNull Poly<T, S> value) {
        if (value.declarative()) {
          return value.additive().printAll();
        } else {
          return "add ["
              + value.additive().printAll() + "], subtract ["
              + value.subtractive().printAll() + "]";
        }
      }

      @NotNull
      public String printElement(T element) {
        return element.toString();
      }

      @Override
      public @NotNull String printData(@NotNull S value) {
        return value.printAll();
      }

      public abstract T parseElement(String element) throws ParseSettingException;

      public abstract S createSet();

      public final S copySet(S set) {
        S newSet = createSet();
        newSet.addAll(set);
        return newSet;
      }
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
