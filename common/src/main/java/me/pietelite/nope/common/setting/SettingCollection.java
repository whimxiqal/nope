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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import me.pietelite.nope.common.storage.Persistent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A data structure to store multiple {@link Setting}s.
 */
public abstract class SettingCollection {

  private final Set<SettingKey<?, ?, ?>> keys = new HashSet<>();
  private final Map<SettingKey<?, ?, ?>, SettingValue<?>> data = new HashMap<>();
  private final Map<SettingKey<?, ?, ?>, Target> targets = new HashMap<>();

  private Set<Setting<?, ?>> buildSet() {
    return keys.stream()
        .map(key -> Setting.ofUnchecked(key, data.get(key), targets.get(key)))
        .collect(Collectors.toSet());
  }

  public final int size() {
    return keys.size();
  }

  public final boolean isEmpty() {
    return keys.isEmpty();
  }

  public final boolean isSet(SettingKey<?, ?, ?> key) {
    return keys.contains(key);
  }

  @NotNull
  public final Iterator<Setting<?, ?>> iterator() {
    return buildSet().iterator();
  }

  /**
   * Put a {@link Setting} on this {@link SettingCollection}.
   * The {@link Setting} will only be applied if this type of collection accepts
   * the corresponding {@link SettingKey}.
   *
   * @param setting the setting
   * @param <T>     the data type
   * @param <V>     the value type
   * @return true if the internal state was changed
   * @see #canHave(SettingKey)
   */
  public final <T, V extends SettingValue<T>> boolean set(Setting<T, V> setting) {
    if (!canHave(setting.key())) {
      return false;
    }
    boolean changedData = setValue(setting.key(), setting.value());
    Target target = setting.target();
    boolean changedTarget;
    if (target == null) {
      Target removedTarget = removeTarget(setting.key());
      changedTarget = removedTarget != null;
    } else {
      changedTarget = setTarget(setting.key(), Objects.requireNonNull(setting.target()));
    }
    return changedData || changedTarget;
  }

  /**
   * Get the whole {@link Setting} stored in this container corresponding to a
   * {@link SettingKey}, if it exists.
   *
   * @param key the key
   * @param <T> the data type
   * @param <V> the setting value type
   * @return maybe a {@link Setting}
   */
  public final <T, V extends SettingValue<T>> Optional<Setting<T, V>> get(SettingKey<T, V, ?> key) {
    Optional<V> data = getValue(key);
    Optional<Target> target = getTarget(key);
    if (!data.isPresent() && !target.isPresent()) {
      return Optional.empty();
    } else {
      return Optional.of(Setting.of(key, data.orElse(null), target.orElse(null)));
    }
  }

  /**
   * Get the {@link SettingValue} stored in a given {@link SettingKey} if it exists.
   *
   * @param key the key
   * @param <T> the value type
   * @param <V> the setting value type
   * @return maybe the value
   */
  @SuppressWarnings("unchecked")
  public final <T, V extends SettingValue<T>> Optional<V> getValue(SettingKey<T, V, ?> key) {
    if (data.containsKey(key)) {
      return Optional.ofNullable((V) data.get(key));
    } else {
      return Optional.empty();
    }
  }

  /**
   * Similar to {@link Map#compute(Object, BiFunction)}, get a stored {@link SettingValue}
   * under a given {@link SettingKey}, and if it doesn't exist, then get a new value
   * with the given {@link Supplier}, store it, and use it as the return value here.
   *
   * @param key           the key
   * @param valueSupplier the supplier of a new value to be used if a value doesn't already exist
   * @param <T>           the data type
   * @param <V>           the value type
   * @return the setting value
   */
  public final <T, V extends SettingValue<T>> V computeValue(SettingKey<T, V, ?> key,
                                                             Supplier<V> valueSupplier) {
    Optional<V> dataOptional = getValue(key);
    if (dataOptional.isPresent()) {
      return dataOptional.get();
    } else {
      V value = valueSupplier.get();
      setValue(key, value);
      return value;
    }
  }

  /**
   * Get a {@link SettingValue} from under a {@link SettingKey} and require that it exists.
   *
   * @param key the key
   * @param <T> the data type
   * @param <V> the setting value type
   * @return the setting value
   * @throws IllegalArgumentException if the given key does not have a {@link SettingValue} as requested
   */
  public final <T, V extends SettingValue<T>> V requireValue(SettingKey<T, V, ?> key)
      throws IllegalArgumentException {
    return getValue(key).orElseThrow(() ->
        new IllegalArgumentException("The setting collection did not have the required data"));
  }

  public final <T, V extends SettingValue<T>> boolean setValue(SettingKey<T, V, ?> key, V data) {
    return this.setValueUnchecked(key, data);
  }

  /**
   * Set a new {@link SettingValue} to a {@link SettingKey}. This method assumes the
   * generic types match.
   *
   * @param key   the key
   * @param value the value
   * @return true if set, false if this exact mapping already existed
   * @see #setValue(SettingKey, SettingValue)
   */
  public boolean setValueUnchecked(SettingKey<?, ?, ?> key, SettingValue<?> value) {
    if (keys.contains(key)) {
      if (this.data.containsKey(key) && this.data.get(key).equals(value)) {
        return false;
      }
    } else {
      keys.add(key);
    }
    this.data.put(key, value);
    return true;
  }

  /**
   * Get the {@link Target} under this {@link SettingKey}, if it exists.
   *
   * @param key the key
   * @param <T> the data type
   * @param <V> the setting value type
   * @return maybe the target
   */
  public final <T, V extends SettingValue<T>> Optional<Target> getTarget(SettingKey<T, V, ?> key) {
    return Optional.ofNullable(targets.get(key));
  }

  /**
   * Similar to {@link Map#compute(Object, BiFunction)}, get a stored {@link Target}
   * under a given {@link SettingKey}, and if it doesn't exist, then get a new target
   * with the given {@link Supplier}, store it, and use it as the return value here.
   *
   * @param key            the key
   * @param targetSupplier the supplier of a new target to be used if a target doesn't already exist
   * @param <T>            the data type
   * @param <V>            the value type
   * @return the target
   */
  public final <T, V extends SettingValue<T>> Target computeTarget(SettingKey<T, V, ?> key,
                                                                   Supplier<Target> targetSupplier) {
    Optional<Target> targetOptional = getTarget(key);
    if (targetOptional.isPresent()) {
      return targetOptional.get();
    } else {
      Target target = targetSupplier.get();
      setTarget(key, target);
      return target;
    }
  }

  /**
   * Set a new {@link Target} under a {@link SettingKey}.
   *
   * @param key    the key
   * @param target the target
   * @param <T>    the data type
   * @param <V>    the setting value type
   * @return true if it was set correctly, false if this exact target was already set under this key
   */
  public final <T, V extends SettingValue<T>> boolean setTarget(SettingKey<T, V, ?> key,
                                                                @NotNull Target target) {
    if (keys.contains(key)) {
      if (this.data.containsKey(key) && target.equals(this.targets.get(key))) {
        return false;
      }
    } else {
      keys.add(key);
    }
    this.targets.put(key, target);
    return true;
  }

  /**
   * Completely remove all data associated with this key.
   *
   * @param key the key
   * @return true if there was anything to remove.
   */
  public final boolean remove(SettingKey<?, ?, ?> key) {
    boolean changed = keys.remove(key);
    data.remove(key);
    targets.remove(key);
    return changed;
  }

  /**
   * Remove the {@link SettingValue} set under this {@link SettingKey}, if one exists.
   *
   * @param key the key
   * @param <T> the data type
   * @param <V> the setting value type
   * @return the removed value, or null if it doesn't exist
   */
  @Nullable
  @SuppressWarnings("unchecked")
  public final <T, V extends SettingValue<T>> V removeValue(SettingKey<T, V, ?> key) {
    V removed = (V) data.remove(key);
    cleanKeyMapFor(key);
    return removed;
  }

  /**
   * Remove the {@link Target} set under this {@link SettingKey}, if one exists.
   *
   * @param key the key
   * @return the removed target, or null if it doesn't exist
   */
  @Nullable
  public final Target removeTarget(SettingKey<?, ?, ?> key) {
    Target removed = targets.remove(key);
    cleanKeyMapFor(key);
    return removed;
  }

  private void cleanKeyMapFor(SettingKey<?, ?, ?> key) {
    if (!data.containsKey(key) && !targets.containsKey(key)) {
      keys.remove(key);
    }
  }

  /**
   * Stream all information about {@link Setting}s into a {@link List}.
   *
   * @return all stored settings
   */
  public final List<Setting<?, ?>> settings() {
    return keys.stream()
        .map(key -> Setting.ofUnchecked(key, data.get(key), targets.get(key)))
        .collect(Collectors.toList());
  }

  /**
   * Set all the {@link Setting}s in the given map into this one.
   *
   * @param settingMap the other map
   */
  public void setAll(SettingCollection settingMap) {
    settingMap.settings().forEach(this::set);
  }

  /**
   * Set all the settings in the given {@link Iterable} of {@link Setting}s into this one.
   *
   * @param settings all settings
   */
  public void setAll(Iterable<Setting<?, ?>> settings) {
    settings.forEach(this::set);
  }

  /**
   * Clear all {@link Setting}s.
   */
  public void clear() {
    keys.clear();
    data.clear();
    targets.clear();
  }

  /**
   * Find out whether this collection may store a {@link Setting} with a certain {@link SettingKey}.
   *
   * @param key the key
   * @return true if it can be stored
   */
  public boolean canHave(SettingKey<?, ?, ?> key) {
    return !key.global();
  }

  /**
   * Set the default value on this container for a certain {@link SettingKey}.
   *
   * @param settingKey the key
   * @param <T>        the data type
   * @param <V>        the setting value type
   * @param <M>        the manager type
   */
  public <T, V extends SettingValue<T>, M extends SettingKey.Manager<T, V>> void setDefaultValue(
      SettingKey<T, V, M> settingKey) {
    setValue(settingKey, settingKey.manager().wrapData(settingKey.defaultData()));
  }
}
