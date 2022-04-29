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

package me.pietelite.nope.common.struct;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

public class IgnoreCaseStringHashMap<T> {

  private final HashMap<String, T> valueMap;
  private final HashMap<String, String> realKeyMap;

  public IgnoreCaseStringHashMap(int initialCapacity) {
    this.valueMap = new HashMap<>(initialCapacity);
    this.realKeyMap = new HashMap<>(initialCapacity);
  }

  public IgnoreCaseStringHashMap() {
    this.valueMap = new HashMap<>();
    this.realKeyMap = new HashMap<>();
  }

  public IgnoreCaseStringHashMap(Map<? extends String, ? extends T> m) {
    this();
    putAll(m);
  }

  private String lower(String s) {
    return s.toLowerCase();
  }

  public int size() {
    return valueMap.size();
  }

  public boolean isEmpty() {
    return valueMap.isEmpty();
  }

  public void clear() {
    valueMap.clear();
    realKeyMap.clear();
  }

  public boolean containsValue(Object value) {
    return valueMap.containsValue(value);
  }

  public T get(Object key) {
    if (!(key instanceof String)) {
      return null;
    }
    return valueMap.get(lower((String) key));
  }

  public boolean containsKey(Object key) {
    if (!(key instanceof String)) {
      return false;
    }
    return valueMap.containsKey(lower((String) key));
  }

  public T put(String key, T value) {
    T out = valueMap.put(lower(key), value);
    realKeyMap.put(lower(key), key);
    return out;
  }

  public void putAll(Map<? extends String, ? extends T> m) {
    m.forEach(this::put);
  }

  public T remove(Object key) {
    if (!(key instanceof String)) {
      return null;
    }
    String l = lower((String) key);
    T out = valueMap.remove(l);
    realKeyMap.remove(l);
    return out;
  }

  public Collection<T> values() {
    return valueMap.values();
  }

  public T getOrDefault(Object key, T defaultValue) {
    T out = this.get(key);
    if (out == null) {
      return defaultValue;
    }
    return out;
  }

  public T putIfAbsent(String key, T value) {
    if (this.containsKey(key)) {
      return this.get(key);
    }
    this.put(key, value);
    return null;
  }

  public boolean remove(Object key, Object value) {
    if (this.get(key).equals(value)) {
      this.remove(key, value);
      return true;
    }
    return false;
  }

  public boolean replace(String key, T oldValue, T newValue) {
    if (this.get(key).equals(oldValue)) {
      this.put(key, newValue);
      return true;
    }
    return false;
  }

  public T replace(String key, T value) {
    if (this.containsKey(key)) {
      return this.put(key, value);
    }
    return null;
  }

  public void forEach(BiConsumer<? super String, ? super T> action) {
    valueMap.forEach(action);
  }

  public HashMap<String, T> map() {
    HashMap<String, T> out = new HashMap<>();
    for (Map.Entry<String, T> entry : valueMap.entrySet()) {
      out.put(realKeyMap.get(entry.getKey()), entry.getValue());
    }
    return out;
  }

  public Set<Map.Entry<String, T>> entrySet() {
    return map().entrySet();
  }

  public Set<String> realKeys() {
    return new HashSet<>(realKeyMap.values());
  }

}
