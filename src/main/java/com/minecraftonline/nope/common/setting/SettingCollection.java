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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minecraftonline.nope.common.storage.Persistent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SettingCollection implements Persistent {

  private final Set<SettingKey<?, ?>> keys = Sets.newHashSet();
  private final Map<SettingKey<?, ?>, SettingValue<?>> data = Maps.newHashMap();
  private final Map<SettingKey<?, ?>, Target> targets = Maps.newHashMap();

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

  public final boolean isSet(SettingKey<?, ?> key) {
    return keys.contains(key);
  }

  @NotNull
  public final Iterator<Setting<?, ?>> iterator() {
    return buildSet().iterator();
  }

  public final <T, V extends SettingValue<T>> boolean set(Setting<T, V> setting) {
    return this.set(setting, true);
  }

  private <T, V extends SettingValue<T>> boolean set(Setting<T, V> setting, boolean save) {
    boolean changedData = setValue(setting.key(), setting.value());
    Target target = setting.target();
    boolean changedTarget;
    if (target == null) {
      Target removedTarget = removeTarget(setting.key());
      changedTarget = removedTarget != null;
    } else {
      changedTarget = setTarget(setting.key(), Objects.requireNonNull(setting.target()));
    }
    if (changedData || changedTarget) {
      if (save) {
        save();
      }
      return true;
    } else {
      return false;
    }
  }

  public final <T, V extends SettingValue<T>> Optional<Setting<T, V>> get(SettingKey<T, V> key) {
    Optional<V> data = getValue(key);
    Optional<Target> target = getTarget(key);
    if (!data.isPresent() && !target.isPresent()) {
      return Optional.empty();
    } else {
      return Optional.of(Setting.of(key, data.orElse(null), target.orElse(null)));
    }
  }

  @SuppressWarnings("unchecked")
  public final <T, V extends SettingValue<T>> Optional<V> getValue(SettingKey<T, V> key) {
    if (data.containsKey(key)) {
      return Optional.ofNullable((V) data.get(key));
    } else {
      return Optional.empty();
    }
  }

  public final <T, V extends SettingValue<T>> V computeValue(SettingKey<T, V> key, Supplier<V> valueSupplier) {
    Optional<V> dataOptional = getValue(key);
    if (dataOptional.isPresent()) {
      return dataOptional.get();
    } else {
      V value = valueSupplier.get();
      setValue(key, value);
      return value;
    }
  }

  public final <T, V extends SettingValue<T>> V requireValue(SettingKey<T, V> key) {
    return getValue(key).orElseThrow(() ->
        new RuntimeException("The setting collection did not have the required data"));
  }

  public final <T, V extends SettingValue<T>> V getValueOrDefault(SettingKey<T, V> key) {
    return getValue(key).orElse(key.defaultValue());
  }

  public final <T, V extends SettingValue<T>> boolean setValue(SettingKey<T, V> key, V data) {
    return this.setValueUnchecked(key, data);
  }

  public boolean setValueUnchecked(SettingKey<?, ?> key, SettingValue<?> value) {
    if (keys.contains(key)) {
      if (this.data.containsKey(key) && this.data.get(key).equals(value)) {
        return false;
      }
    } else {
      keys.add(key);
    }
    this.data.put(key, value);
    this.save();
    return true;
  }

  public final <T, V extends SettingValue<T>> Optional<Target> getTarget(SettingKey<T, V> key) {
    return Optional.ofNullable(targets.get(key));
  }

  public final <T, V extends SettingValue<T>> Target computeTarget(SettingKey<T, V> key, Supplier<Target> targetSupplier) {
    Optional<Target> targetOptional = getTarget(key);
    if (targetOptional.isPresent()) {
      return targetOptional.get();
    } else {
      Target target = targetSupplier.get();
      setTarget(key, target);
      return target;
    }
  }

  public final <T, V extends SettingValue<T>> boolean setTarget(SettingKey<T, V> key, @NotNull Target target) {
    if (keys.contains(key)) {
      if (this.data.containsKey(key) && target.equals(this.targets.get(key))) {
        return false;
      } else {
        this.targets.put(key, target);
        save();
        return true;
      }
    }
    keys.add(key);
    this.targets.put(key, target);
    save();
    return true;
  }

  public final boolean remove(SettingKey<?, ?> key) {
    boolean changed = keys.remove(key);
    data.remove(key);
    targets.remove(key);
    return changed;
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public final <T, V extends SettingValue<T>> T removeValue(SettingKey<T, V> key) {
    T removed = (T) data.remove(key);
    if (!data.containsKey(key) && !targets.containsKey(key)) {
      keys.remove(key);
    }
    return removed;
  }

  public final Target removeTarget(SettingKey<?, ?> key) {
    Target removed = targets.remove(key);
    if (!data.containsKey(key) && !targets.containsKey(key)) {
      keys.remove(key);
    }
    return removed;
  }

  public final Collection<Setting<?, ?>> settings() {
    return keys.stream()
        .map(key -> Setting.ofUnchecked(key, data.get(key), targets.get(key)))
        .collect(Collectors.toList());
  }

  public void setAll(SettingCollection settingMap) {
    settingMap.settings().forEach(setting -> this.set(setting, false));
    save();
  }

  public void setAll(Iterable<Setting<?, ?>> settings) {
    settings.forEach(setting -> this.set(setting, false));
    save();
  }

  public void clear() {
    keys.clear();
    data.clear();
    targets.clear();
  }
}
