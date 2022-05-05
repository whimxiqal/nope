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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.pietelite.nope.common.api.register.SettingCategory;
import me.pietelite.nope.common.api.register.SettingKeyBuilder;
import me.pietelite.nope.common.api.struct.AltSet;
import me.pietelite.nope.common.host.Evaluation;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.host.HostedProfile;
import me.pietelite.nope.common.host.Profile;
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
  private final SettingCategory category;
  private final boolean global;
  private final boolean playerRestrictive;
  @Setter
  private boolean functional;

  SettingKey(String id, @NotNull M manager,
             T defaultData, T naturalData,
             String description, String blurb,
             SettingCategory category,
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

  public abstract Evaluation<T> extractValue(@NotNull List<Host> hosts,
                                             @Nullable final UUID userUuid);

  public abstract String type();

  @Override
  public String name() {
    return id;
  }

  @Override
  public int compareTo(@NotNull SettingKey<?, ?, ?> o) {
    return this.id().compareTo(o.id());
  }

  public final V defaultValue() {
    return manager.wrapData(defaultData);
  }

  /**
   * An implementation of a setting key builder.
   *
   * @param <T> the data type
   * @param <K> the setting key type
   * @param <V> the value type
   * @param <M> the manager type
   * @param <B> the builder type, for chaining
   */
  @SuppressWarnings("unchecked")
  public abstract static class Builder<T,
      K extends SettingKey<T, V, M>,
      V extends SettingValue<T>,
      M extends SettingKey.Manager<T, V>,
      B extends Builder<T, K, V, M, B>> implements SettingKeyBuilder<T, B> {
    protected final String id;
    protected final M manager;
    protected T defaultValue;
    protected T naturalValue;
    protected String description;
    protected String blurb;
    protected SettingCategory category = SettingCategory.MISC;
    protected boolean functional = false;
    protected boolean global = false;
    protected boolean playerRestrictive = false;

    private boolean updatedDefaultValue = false;

    protected Builder(String id, M manager) {
      this.id = id;
      this.manager = manager;
    }

    @Override
    public B defaultValue(T defaultValue) {
      this.defaultValue = defaultValue;
      updatedDefaultValue = true;
      if (naturalValue == null) {
        this.naturalValue = defaultValue;
      }
      return (B) this;
    }

    @Override
    public B naturalValue(T naturalValue) {
      this.naturalValue = naturalValue;
      return (B) this;
    }

    @Override
    public B description(String description) {
      this.description = description;
      return (B) this;
    }

    @Override
    public B blurb(String blurb) {
      if (blurb.length() > 36) {
        throw new IllegalStateException("A 'blurb' may not be longer than 36 characters");
      }
      this.blurb = blurb;
      return (B) this;
    }

    @Override
    public B category(SettingCategory category) {
      this.category = category;
      return (B) this;
    }

    /**
     * Specify that this key is implemented.
     *
     * @return this builder, for chaining
     */
    @Override
    public B functional() {
      this.functional = true;
      return (B) this;
    }

    /**
     * Specify that this key can only be set for the entire server, globally.
     *
     * @return this builder, for chaining
     */
    @Override
    public B global() {
      this.global = true;
      return (B) this;
    }

    /**
     * Specify that when a non-default value of this setting is set, it is restrictive for players.
     * This feature is intended to determine when unrestricted-type players should be able to ingore
     * restrictive types of changes to behavior.
     * For example, this plugin should generally not prevent administrators from breaking blocks where
     * breaking blocks is disallowed.
     *
     * @return this builder, for chaining
     */
    @Override
    public B playerRestrictive() {
      this.playerRestrictive = true;
      return (B) this;
    }

    protected final void validate() {
      if (id == null) {
        throw new IllegalStateException("A SettingKey's id may not be null");
      } else if (manager == null) {
        throw new IllegalStateException("A SettingKey's manager may not be null");
      } else if (defaultValue == null) {
        throw new IllegalStateException("A SettingKey's default value may not be null");
      } else if (!updatedDefaultValue) {
        throw new IllegalStateException("A SettingKey's default value must be set");
      } else if (naturalValue == null) {
        throw new IllegalStateException("A SettingKey's natural value may not be null");
      } else if (blurb == null) {
        throw new IllegalStateException("A SettingKey's blurb may not be null");
      } else if (description == null) {
        throw new IllegalStateException("A SettingKey's description may not be null");
      } else if (category == null) {
        throw new IllegalStateException("A SettingKey's category may not be null");
      }
    }

    /**
     * Construct this setting key.
     *
     * @return the setting key
     */
    public abstract K build();
  }

  /**
   * A {@link SettingKey} that stores values such that a single {@link T} datum is returned
   * when evaluating the key on the whole system.
   *
   * @param <T> the data type
   */
  public static class Unary<T> extends SettingKey<T, SettingValue.Unary<T>, Manager.Unary<T>> {

    Unary(String id, Manager.Unary<T> manager,
          T defaultData, T naturalValue,
          String description, String blurb,
          SettingCategory category,
          boolean functional,
          boolean global, boolean playerRestrictive) {
      super(id, manager,
          defaultData, naturalValue,
          description, blurb,
          category,
          functional,
          global, playerRestrictive);
    }

    public static <X> Builder<X> builder(String id, Manager.Unary<X> manager) {
      return new Builder<>(id, manager);
    }

    @Override
    public Evaluation<T> extractValue(@NotNull List<Host> hosts,
                                      @Nullable final UUID userUuid) {

      /* Choose a data structure that will optimize searching for highest priority matching */
      Evaluation<T> evaluation = new Evaluation<>(this);
      if (hosts.isEmpty()) {
        return evaluation;
      }

      Optional<Setting<T, SettingValue.Unary<T>>> currentSetting;
      Target activeTarget = null;
      Target currentTarget;
      T data;

      // Assume targeted until target is set and specifically does not target us
      ListIterator<Host> hostIterator = hosts.listIterator(hosts.size());
      Host host;
      List<HostedProfile> profiles;
      ListIterator<HostedProfile> profileIterator;
      HostedProfile currentProfile;
      while (hostIterator.hasPrevious()) {
        host = hostIterator.previous();
        profiles = host.allProfiles();
        profileIterator = profiles.listIterator(profiles.size());
        while (profileIterator.hasPrevious()) {
          currentProfile = profileIterator.previous();
          currentSetting = currentProfile.profile().get(this);
          if (!currentSetting.isPresent()) {
            continue;
          }
          currentTarget = currentProfile.activeTargetFor(this);
          if (currentTarget != null && activeTarget == null) {
            // Ignore if target has already been specified: the last one takes precedence.
            activeTarget = currentTarget;
          }
          data = currentSetting.get().value().get();
          if (data != null) {
            // the active target acts here
            if (activeTarget == null || activeTarget.test(userUuid, playerRestrictive())) {
              evaluation.add(host, currentProfile.profile(), data);
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

    /**
     * A builder for a {@link SettingKey.Unary}.
     *
     * @param <T> the data type
     */
    public static class Builder<T> extends SettingKey.Builder<T,
        SettingKey.Unary<T>,
        SettingValue.Unary<T>,
        SettingKey.Manager.Unary<T>,
        Builder<T>> implements SettingKeyBuilder.Unary<T, Builder<T>> {

      private Builder(String id, Manager.Unary<T> manager) {
        super(id, manager);
      }

      /**
       * Construct this setting key.
       *
       * @return the setting key
       */
      @Override
      public SettingKey.Unary<T> build() {
        validate();
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

  /**
   * A {@link SettingKey} that stores values such that <i>multiple</i> {@link T} data is returned
   * when evaluating the key on the whole system.
   *
   * @param <T> the data type
   */
  public static class Poly<T, S extends AltSet<T>> extends SettingKey<S,
      SettingValue.Poly<T, S>,
      Manager.Poly<T, S>> {

    Poly(String id, Manager.Poly<T, S> manager,
         S defaultData, S naturalValue,
         String description, String blurb,
         SettingCategory category,
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

    public static <X, Y extends AltSet<X>> Builder<X, Y> builder(String id, Manager.Poly<X, Y> manager) {
      return new Builder<>(id, manager);
    }

    @Override
    public Evaluation<S> extractValue(@NotNull List<Host> hosts,
                                      @Nullable UUID userUuid) {
      /* Choose a data structure that will optimize searching for highest priority matching */
      Evaluation<S> evaluation = new Evaluation<>(this);
      if (hosts.isEmpty()) {
        return evaluation;
      }

      Optional<Setting<S, SettingValue.Poly<T, S>>> currentSetting;
      Target activeTarget = null;
      Target currentTarget;
      SettingValue.Poly<T, S> value;

      // Setup structures for final evaluation
      Stack<Host> valuedHosts = new Stack<>();
      Stack<Profile> valuedProfiles = new Stack<>();
      Stack<SettingValue.Poly<T, S>> values = new Stack<>();

      // Assume targeted until target is set and specifically does not target us
      ListIterator<Host> hostIterator = hosts.listIterator(hosts.size());
      Host host;
      List<HostedProfile> profiles;
      ListIterator<HostedProfile> profileIterator;
      HostedProfile currentProfile;
      while (hostIterator.hasPrevious()) {
        host = hostIterator.previous();
        profiles = host.allProfiles();
        profileIterator = profiles.listIterator(profiles.size());
        while (profileIterator.hasPrevious()) {
          currentProfile = profileIterator.previous();
          currentSetting = currentProfile.profile().get(this);
          if (!currentSetting.isPresent()) {
            continue;
          }
          currentTarget = currentProfile.activeTargetFor(this);
          if (currentTarget != null && activeTarget == null) {
            // Ignore if target has already been specified: the last one takes precedence.
            activeTarget = currentTarget;
          }
          value = currentSetting.get().value();
          if (value != null) {
            // the active target acts here
            if (activeTarget == null || activeTarget.test(userUuid, playerRestrictive())) {
              values.add(value);
              valuedHosts.add(host);
              valuedProfiles.add(currentProfile.profile());
            } else {
              // We have found the data which was specifically not targeted.
              // So, pass through this data value and continue on anew.
              activeTarget = null;
            }
          }
        }
      }

      // now apply our values in the appropriate order
      S result = manager().copySet(defaultData());
      while (!values.isEmpty()) {
        result = manager().copySet(values.pop().applyTo(result));
        evaluation.add(valuedHosts.pop(), valuedProfiles.pop(), result);
      }
      return evaluation;

    }

    @Override
    public String type() {
      return "Multiple Value";
    }

    /**
     * A builder for a {@link SettingKey.Poly}.
     *
     * @param <T> the data type
     */
    public static class Builder<T, S extends AltSet<T>> extends SettingKey.Builder<S,
        SettingKey.Poly<T, S>,
        SettingValue.Poly<T, S>,
        SettingKey.Manager.Poly<T, S>,
        Builder<T, S>> implements SettingKeyBuilder.Poly<T, S, Builder<T, S>> {

      private Builder(String id, Manager.Poly<T, S> manager) {
        super(id, manager);
      }

      @Override
      public SettingKey.Poly<T, S> build() {
        validate();
        return new SettingKey.Poly<>(
            id, manager,
            defaultValue, naturalValue,
            description, blurb,
            category,
            functional,
            global, playerRestrictive
        );
      }

      @Override
      public Builder<T, S> fillDefaultData() {
        return defaultValue(AltSet.full(this.manager.emptySet()));
      }

      @Override
      public Builder<T, S> emptyDefaultData() {
        return defaultValue(this.manager.emptySet());
      }

      @Override
      public Builder<T, S> fillNaturalData() {
        return naturalValue(AltSet.full(this.manager.emptySet()));
      }

      @Override
      public Builder<T, S> emptyNaturalData() {
        return naturalValue(this.manager.emptySet());
      }
    }
  }

  /**
   * A manager of data stored under {@link SettingKey} in {@link Setting}s.
   *
   * @param <T> the data type
   * @param <V> the setting value type
   */
  public abstract static class Manager<T, V extends SettingValue<T>> {

    public abstract Class<T> dataType() throws ParseSettingException;

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

    /**
     * Wrap data into a value that, if applied to an evaluation of setting value,
     * would yield the data.
     *
     * @param data the data
     * @return the wrapped value
     */
    public abstract V wrapData(T data);

    /**
     * Get a piece of data different from the input. The new piece of data should be opposite
     * to the input, if possible.
     * This method should only be used for non-critical functionality.
     *
     * @param data the data
     * @return the opposite
     */
    public abstract T createAlternate(T data);

    /**
     * A manager for {@link SettingKey.Unary}s.
     *
     * @param <T> the data type
     */
    public abstract static class Unary<T> extends Manager<T, SettingValue.Unary<T>> {

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

      public SettingKeyBuilder.Unary<T, SettingKey.Unary.Builder<T>> keyBuilder(String id) {
        return new SettingKey.Unary.Builder<T>(id, this);
      }
    }

    /**
     * A manager for {@link SettingKey.Poly}s.
     *
     * @param <T> the data type
     * @param <S> the set of data type
     */
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

      @Override
      public final S createAlternate(S data) {
        S opposite = emptySet();
        opposite.addAll(data);
        opposite.invert();
        return opposite;
      }

      /**
       * Parse a set of data from a string.
       *
       * @param data the data in string form
       * @return the set of data
       * @throws ParseSettingException if it could not be parsed
       */
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

      /**
       * Add a group of values. When returning the contents of the set of values,
       * the list of groups will be considered to see how to shorten the returned
       * set by combining those in a group into just the group's name.
       * This improves the user experience.
       *
       * @param id          the id/name of the group
       * @param description the description of the group
       * @param group       the actual set of values to combine as a single group
       */
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

      /**
       * Same as {@link #elementOptions()}, but do not combine elements into groups.
       *
       * @return a map of value to description
       */
      @NotNull
      public abstract Map<String, Object> elementOptionsWithoutGroups();

      /**
       * Parse a single element.
       *
       * @param element the element in string form
       * @return the data
       * @throws ParseSettingException if it cannot be parsed
       */
      public abstract T parseElement(String element) throws ParseSettingException;

      public abstract Class<T> elementType();

      /**
       * Generate a new empty set.
       *
       * @return the set
       */
      public abstract S emptySet();

      public final S fullSet() {
        return AltSet.full(emptySet());
      }

      /**
       * Shallow copy the set into another set.
       *
       * @param set the set
       * @return a new set
       */
      public final S copySet(S set) {
        S newSet = emptySet();
        newSet.addAll(set);
        return newSet;
      }

      public SettingKeyBuilder.Poly<T, S, SettingKey.Poly.Builder<T, S>> keyBuilder(String id) {
        return new SettingKey.Poly.Builder<>(id, this);
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
