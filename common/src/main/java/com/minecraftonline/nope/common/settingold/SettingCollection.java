/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

///*
// * MIT License
// *
// * Copyright (c) 2020 MinecraftOnline
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// *
// */
//
//package com.minecraftonline.nope.common.settingold;
//
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//import com.minecraftonline.nope.common.storage.Persistent;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.Set;
//import java.util.function.Supplier;
//import java.util.stream.Collectors;
//import org.jetbrains.annotations.NotNull;
//
//public abstract class SettingCollection implements Persistent {
//
//  private final Set<SettingKey<?>> keys = Sets.newHashSet();
//  private final Map<SettingKey<?>, Object> data = Maps.newHashMap();
//  private final Map<SettingKey<?>, Target> targets = Maps.newHashMap();
//
//  private Set<Setting<?>> buildSet() {
//    return keys.stream()
//        .map(key -> Setting.ofUnchecked(key, data.get(key), targets.get(key)))
//        .collect(Collectors.toSet());
//  }
//
//  public final int size() {
//    return keys.size();
//  }
//
//  public final boolean isEmpty() {
//    return keys.isEmpty();
//  }
//
//  public final boolean isSet(SettingKey<?> key) {
//    return keys.contains(key);
//  }
//
//  @NotNull
//  public final Iterator<Setting<?>> iterator() {
//    return buildSet().iterator();
//  }
//
//  public final <T> boolean set(Setting<T> setting) {
//    return this.set(setting, true);
//  }
//
//  private <T> boolean set(Setting<T> setting, boolean save) {
//    boolean changedData = setData(setting.key(), setting.data());
//    boolean changedTarget = setTarget(setting.key(), setting.target());
//    if (changedData || changedTarget) {
//      if (save) {
//        save();
//      }
//      return true;
//    } else {
//      return false;
//    }
//  }
//
//  public final <T> Optional<Setting<T>> get(SettingKey<T> key) {
//    Optional<T> data = getData(key);
//    Optional<Target> target = getTarget(key);
//    if (!data.isPresent() && !target.isPresent()) {
//      return Optional.empty();
//    } else {
//      return Optional.of(Setting.of(key, data.orElse(null), target.orElse(null)));
//    }
//  }
//
//  @SuppressWarnings("unchecked")
//  public final <T> Optional<T> getData(SettingKey<T> key) {
//    if (data.containsKey(key)) {
//      return Optional.ofNullable((T) data.get(key));
//    } else {
//      return Optional.empty();
//    }
//  }
//
//  public final <T> T computeData(SettingKey<T> key, Supplier<T> dataSupplier) {
//    Optional<T> dataOptional = getData(key);
//    if (dataOptional.isPresent()) {
//      return dataOptional.get();
//    } else {
//      T data = dataSupplier.get();
//      setData(key, data);
//      return data;
//    }
//  }
//
//  public final <T> T requireData(SettingKey<T> key) {
//    return getData(key).orElseThrow(() ->
//        new RuntimeException("The setting collection did not have the required data"));
//  }
//
//  public final <T> T getDataOrDefault(SettingKey<T> key) {
//    return getData(key).orElse(key.defaultData());
//  }
//
//  public final <T> boolean setData(SettingKey<T> key, T data) {
//    return this.setDataChecked(key, data);
//  }
//
//  public final boolean setDataUnchecked(SettingKey<?> key, Object data) {
//    if (!key.type().isInstance(data)) {
//      throw new IllegalArgumentException(String.format(
//          "The generic type of the key (%s) does not match the type of the data (%s)",
//          key.type().getName(),
//          data.getClass().getName()));
//    }
//    return setDataChecked(key, data);
//  }
//
//  private boolean setDataChecked(SettingKey<?> key, Object data) {
//    if (keys.contains(key)) {
//      if (this.data.containsKey(key) && this.data.get(key).equals(data)) {
//        return false;
//      } else {
//        this.data.put(key, data);
//        return true;
//      }
//    }
//    keys.add(key);
//    this.data.put(key, data);
//    this.save();
//    return true;
//  }
//
//  public final <T> Optional<Target> getTarget(SettingKey<T> key) {
//    return Optional.ofNullable(targets.get(key));
//  }
//
//  public final <T> Target computeTarget(SettingKey<T> key, Supplier<Target> targetSupplier) {
//    Optional<Target> targetOptional = getTarget(key);
//    if (targetOptional.isPresent()) {
//      return targetOptional.get();
//    } else {
//      Target target = targetSupplier.get();
//      setTarget(key, target);
//      return target;
//    }
//  }
//
//  public final <T> boolean setTarget(SettingKey<T> key, @NotNull Target target) {
//    if (keys.contains(key)) {
//      if (this.data.containsKey(key) && target.equals(this.targets.get(key))) {
//        return false;
//      } else {
//        this.targets.put(key, target);
//        return true;
//      }
//    }
//    keys.add(key);
//    this.targets.put(key, target);
//    save();
//    return true;
//  }
//
//  public final boolean remove(SettingKey<?> key) {
//    boolean changed = keys.remove(key);
//    data.remove(key);
//    targets.remove(key);
//    return changed;
//  }
//
//  @SuppressWarnings("unchecked")
//  public final <T> T removeData(SettingKey<T> key) {
//    T removed = (T) data.remove(key);
//    if (!data.containsKey(key) && !targets.containsKey(key)) {
//      keys.remove(key);
//    }
//    return removed;
//  }
//
//  public final Target removeTarget(SettingKey<?> key) {
//    Target removed = targets.remove(key);
//    if (!data.containsKey(key) && !targets.containsKey(key)) {
//      keys.remove(key);
//    }
//    return removed;
//  }
//
//  public final Collection<Setting<?>> settings() {
//    return keys.stream()
//        .map(key -> Setting.ofUnchecked(key, data.get(key), targets.get(key)))
//        .collect(Collectors.toList());
//  }
//
//  public void setAll(SettingCollection settingMap) {
//    settingMap.settings().forEach(setting -> this.set(setting, false));
//    save();
//  }
//
//  public void setAll(Iterable<Setting<?>> settings) {
//    settings.forEach(setting -> this.set(setting, false));
//    save();
//  }
//
//  public void clear() {
//    keys.clear();
//    data.clear();
//    targets.clear();
//  }
//
//}
