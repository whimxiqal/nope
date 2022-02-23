/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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

package me.pietelite.nope.common.setting;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import me.pietelite.nope.common.host.Evaluation;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.struct.AltSet;
import me.pietelite.nope.common.struct.HashAltSet;
import me.pietelite.nope.common.struct.Location;
import me.pietelite.nope.common.struct.Named;
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
    M extends SettingKey.Manager<T, V>> implements Named, Comparable<SettingKey<?, ?, ?>> {
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

  public abstract Evaluation<T> extractValue(@NotNull Collection<Host> hosts,
                                          @Nullable final UUID userUuid,
                                          @NotNull final Location location);

  public abstract String type();

  @Override
  public String name() {
    return id;
  }

  @Override
  public int compareTo(@NotNull SettingKey<?, ?, ?> o) {
    return this.id().compareTo(o.id());
  }

  public enum Category {
    BLOCKS,
    DAMAGE,
    ENTITIES,
    MISC,
    MOVEMENT,
  }

  public static class Unary<T> extends SettingKey<T, SettingValue.Unary<T>, Manager.Unary<T>> {

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
    public Evaluation<T> extractValue(@NotNull Collection<Host> hosts,
                          @Nullable final UUID userUuid,
                          @NotNull final Location location) {

      /* Choose a data structure that will optimize searching for highest priority matching */
      Queue<Host> hostQueue;
      Comparator<Host> descending = (h1, h2) -> Integer.compare(h2.priority(), h1.priority());
      Evaluation<T> evaluation = new Evaluation<>(this);
      if (hosts.size() >= 1) {
        hostQueue = new PriorityQueue<>(hosts.size(), descending);
        hostQueue.addAll(hosts);

        Host currentHost;
        Optional<Setting<T, SettingValue.Unary<T>>> currentSetting;
        Target activeTarget = null;
        Target currentTarget;
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
          if (currentTarget != null && activeTarget == null) {
            // Ignore if target has already been specified: the last one takes precedence.
            activeTarget = currentTarget;
          }
          data = currentSetting.get().value().get();
          if (data != null) {
            // the active target acts here
            if ((activeTarget == null ? Target.all() : activeTarget)
                .test(userUuid, playerRestrictive())) {
              evaluation.add(currentHost, data);
              return evaluation;
            } else {
              // We have found the data which was specifically not targeted.
              // So, pass through this data value and continue on anew.
              activeTarget = null;
            }
          }
        }
      }

      // No more left, so just do default
      return evaluation;
    }

    @Override
    public String type() {
      return "Single Value";
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
      Manager.Poly<T, S>> {

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
    public Evaluation<S> extractValue(@NotNull Collection<Host> hosts,
                          @Nullable UUID userUuid,
                          @NotNull Location location) {
      /* Choose a data structure that will optimize searching for highest priority matching */
      Queue<Host> hostQueue;
      Comparator<Host> descending = (h1, h2) -> Integer.compare(h2.priority(), h1.priority());
      Stack<SettingValue.Poly<T, S>> values = new Stack<>();
      Stack<Host> valuedHosts = new Stack<>();
      Evaluation<S> evaluation = new Evaluation<>(this);
      if (hosts.size() >= 1) {
        hostQueue = new PriorityQueue<>(hosts.size(), descending);
        hostQueue.addAll(hosts);

        Host currentHost;
        Optional<Setting<S, SettingValue.Poly<T, S>>> currentSetting;
        Target activeTarget = null;
        Target currentTarget;
        SettingValue.Poly<T, S> value;

        while (!hostQueue.isEmpty()) {
          currentHost = hostQueue.remove();
          currentSetting = currentHost.get(this);
          if (!currentSetting.isPresent()) {
            // This shouldn't happen because we previously found that this host has this setting
            throw new RuntimeException("Error retrieving setting value");
          }
          currentTarget = currentSetting.get().target();
          if (currentTarget != null && activeTarget == null) {
            // Ignore if target has already been specified: the last one takes precedence.
            activeTarget = currentTarget;
          }
          value = currentSetting.get().value();
          if (value != null) {
            if ((activeTarget == null ? Target.all() : activeTarget)
                .test(userUuid, playerRestrictive())) {
              values.add(value);
              valuedHosts.add(currentHost);
              // TODO if we ever add a declarative type, it will ignore anything else.
              //  so, just stop here and continue on to the backwards traversal down below.
              //  (need to add a check method in SettingValue.Poly for declarative type)
            }
            // We have found data (whether targeting us or not)
            // So continue on anew.
            activeTarget = null;
          }
        }
      }

      // now apply our values in the appropriate order
      S result = manager().copySet(defaultData());
      while (!values.isEmpty()) {
        result = manager().copySet(values.pop().applyTo(result));
        evaluation.add(valuedHosts.pop(), result);
      }
      return evaluation;
    }

    @Override
    public String type() {
      return "Multiple Value";
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

    public @NotNull Map<String, Object> elementSuggestions() {
      return elementOptions();
    }

    public abstract V parseDeclarativeValue(String settingValue);

    public abstract V wrapData(T data);

    public abstract static class Unary<T> extends Manager<T, SettingValue.Unary<T>> {

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
      public SettingValue.Unary<T> wrapData(T data) {
        return SettingValue.Unary.of(data);
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
      private final Map<String, Set<T>> groups = new HashMap<>();
      private final Map<String, String> groupDescriptions = new HashMap<>();

      @Override
      public SettingValue.Poly<T, S> parseDeclarativeValue(String settingValue) {
        // I'm not sure if we'll ever have to implement this
        throw new UnsupportedOperationException();
      }

      @Override
      public SettingValue.Poly<T, S> wrapData(S data) {
        return SettingValue.Poly.declarative(data);
      }

      public final Set<T> parseSet(String data) throws ParseSettingException {
        Set<T> set = new HashSet<>();
        for (String token : data.split(SET_SPLIT_REGEX)) {
          if (groups.containsKey(token)) {
            set.addAll(groups.get(token));
          } else {
            try {
              set.add(parseElement(token));
            } catch (IllegalArgumentException ex) {
              StringBuilder errorMessage = new StringBuilder(token + " is not a valid element. ");
              Set<String> options = elementOptions().keySet();
              if (options.size() > 0) {
                if (options.size() <= 6) {
                  errorMessage.append("Allowed types: ")
                      .append(options.stream()
                          .map(String::toLowerCase)
                          .collect(Collectors.joining(", ")));
                } else {
                  errorMessage.append("Allowed types: ")
                      .append(options.stream()
                          .limit(8)
                          .map(String::toLowerCase)
                          .collect(Collectors.joining(", ")))
                      .append(" ...");
                }
              }
              throw new SettingKey.ParseSettingException(errorMessage.toString());
            }
          }
        }
        return set;
      }

      @Override
      public @NotNull String printValue(SettingValue.@NotNull Poly<T, S> value) {
        if (value.declarative()) {
          return printSetGrouped(value.additive());
        } else {
          return "add ["
              + printSetGrouped(value.additive()) + "], subtract ["
              + printSetGrouped(value.subtractive()) + "]";
        }
      }

      @NotNull
      public String printElement(T element) {
        return element.toString();
      }

      @Override
      public @NotNull String printData(@NotNull S value) {
        return printSetGrouped(value);
      }

      public final void addGroup(String id, String description, Set<T> group) {
        this.groups.put(id, group);
        this.groupDescriptions.put(id, description);
      }

      /**
       * Print the set, like {@link AltSet#printAll()}. However,
       * combine any values in a group into one single instance of
       * only their group name.
       *
       * @param set the set to print
       * @return the printed string
       * @see #addGroup(String, String, Set)
       */
      public final String printSetGrouped(S set) {
        Set<T> originalSet = new HashSet<>(set.set());
        Set<String> helper = new HashSet<>();
        for (Map.Entry<String, Set<T>> group : groups.entrySet()) {
          Set<T> groupValues = group.getValue();
          if (set.set().containsAll(groupValues)) {
            helper.add(group.getKey());
            originalSet.removeAll(groupValues);
          }
        }
        // add whatever is left (not yet grouped) into the helper set
        originalSet.stream().map(Object::toString).forEach(helper::add);

        String setString = String.join(", ", helper);
        if (set.isEmpty()) {
          return "none";
        } else if (set.isFull()) {
          return "all";
        } else {
          if (set.inverted()) {
            return "(all except) " + setString;
          } else {
            return setString;
          }
        }
      }

      @Override
      public final @NotNull Map<String, Object> elementOptions() {
        Map<String, Object> map = new HashMap<>();
        map.putAll(groupDescriptions);
        map.putAll(elementOptionsWithoutGroups());
        return map;
      }

      @NotNull
      public abstract Map<String, Object> elementOptionsWithoutGroups();

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
