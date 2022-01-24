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

package com.minecraftonline.nope.common.struct;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public abstract class HashAltSet<T> implements AltSet<T> {

  private static final int MAX_SIZE_BEFORE_UBER = 8;

  protected final int maxSize;
  /**
   * This set is "normal" when uber is false because this object
   * works the same way as a normal HashSet.
   * This set is "subtractive" from everything when uber if true
   * because this object works negatively from all terms in uber mode.
   */
  protected final HashSet<T> set = new HashSet<>();
  protected boolean inverted;

  protected HashAltSet(int maxSize) {
    this.maxSize = maxSize;
  }

  public static <X> HashAltSet<X> infinite() {
    return new HashAltSet.Infinite<>();
  }

  public static <X> HashAltSet<X> limited(int maxSize) {
    if (maxSize <= MAX_SIZE_BEFORE_UBER) {
      throw new IllegalArgumentException("If the max size is below "
          + MAX_SIZE_BEFORE_UBER
          + ", you must specify a supplier for all possible elements");
    }
    return limitedLarge(maxSize);
  }

  public static <X> HashAltSet<X> limited(int maxSize, Supplier<Collection<X>> elements) {
    if (maxSize <= MAX_SIZE_BEFORE_UBER) {
      return limitedSmall(elements.get());
    } else {
      return limitedLarge(maxSize);
    }
  }

  private static <X> HashAltSet<X> limitedSmall(Collection<X> elements) {
    return new HashAltSet.NeverUber<>(elements.size(), elements);
  }

  private static <X> HashAltSet<X> limitedLarge(int maxSize) {
    return new HashAltSet.MaybeUber<>(maxSize);
  }

  public static <E extends Enum<E>> HashAltSet<E> ofEnum(Class<E> clazz) {
    E[] enums = clazz.getEnumConstants();
    return limited(enums.length, () -> Arrays.asList(enums));
  }

  @Override
  public boolean isEmpty() {
    if (inverted) {
      return set.size() == maxSize;
    } else {
      return set.isEmpty();
    }
  }

  @Override
  public boolean contains(T element) {
    return inverted != set.contains(element);
  }

  @Override
  public boolean add(T element) {
    return inverted
        ? set.remove(element)
        : set.add(element);
  }

  @Override
  public boolean remove(T element) {
    return inverted
        ? set.add(element)
        : set.remove(element);
  }

  @Override
  public boolean containsAll(@NotNull AltSet<T> other) {
    if (this.inverted) {
      if (other.inverted()) {
        return containsAllOf(other.set(), this.set);
      } else {
        return containsNoneOf(other.set(), this.set);
      }
    } else {
      if (other.inverted()) {
        int subtractiveValuesInsideCount = 0;
        for (T element : other.set()) {
          if (this.set.contains(element)) {
            subtractiveValuesInsideCount++;
          }
        }
        return this.set.size() - subtractiveValuesInsideCount >= this.maxSize - other.set().size();
      } else {
        return containsAllOf(this.set, other.set());
      }
    }
  }

  @Override
  public void addAll(@NotNull AltSet<T> other) {
    if (this.inverted) {
      if (other.inverted()) {
        this.set.removeIf(e -> !other.set().contains(e));
      } else {
        this.set.removeAll(other.set());
      }
    } else {
      if (other.inverted()) {
        Set<T> oldSet = new HashSet<>(this.set);
        this.set.addAll(other.set());
        this.set.removeAll(oldSet);
        this.inverted = true;
      } else {
        this.set.addAll(other.set());
      }
    }
  }

  @Override
  public boolean addAll(@NotNull Collection<T> other) {
    boolean success = true;
    for (T element : other) {
      if (!add(element)) {
        success = false;
      }
    }
    return success;
  }

  @Override
  public void retainAll(@NotNull AltSet<T> other) {
    if (this.inverted) {
      if (other.inverted()) {
        this.set.addAll(other.set());
      } else {
        HashSet<T> oldSet = new HashSet<>(this.set);
        this.set.clear();
        this.inverted = false;
        for (T element : other.set()) {
          if (!oldSet.contains(element)) {
            this.set.add(element);
          }
        }
      }
    } else {
      if (other.inverted()) {
        this.set.removeIf(e -> other.set().contains(e));
      } else {
        this.set.retainAll(other.set());
      }
    }
  }

  @Override
  public void removeAll(@NotNull AltSet<T> other) {
    if (this.inverted) {
      if (other.inverted()) {
        HashSet<T> oldSet = new HashSet<>(this.set);
        this.set.clear();
        this.inverted = false;
        for (T element : other.set()) {
          if (!oldSet.contains(element)) {
            this.set.add(element);
          }
        }
      } else {
        this.set.addAll(other.set());
      }
    } else {
      if (other.inverted()) {
        this.set.removeIf(e -> !other.set().contains(e));
      } else {
        this.set.removeAll(other.set());
      }
    }
  }

  @Override
  public boolean removeAll(@NotNull Collection<T> other) {
    boolean success = true;
    for (T element : other) {
      if (!remove(element)) {
        success = false;
      }
    }
    return success;
  }

  @Override
  public void clear() {
    this.set.clear();
    this.inverted = false;
  }

  @Override
  public void invert() {
    this.inverted = !this.inverted;
  }

  @Override
  public boolean inverted() {
    return inverted;
  }

  @Override
  public Set<T> set() {
    return set;
  }

  private boolean containsAllOf(Set<T> first, Set<T> second) {
    return first.containsAll(second);
  }

  private boolean containsNoneOf(Set<T> first, Set<T> second) {
    for (T t : second) {
      if (first.contains(t)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String printAll() {
    String setString = set.stream().map(Object::toString).collect(Collectors.joining(", "));
    if (inverted) {
      if (set.isEmpty()) {
        return "all";
      } else {
        return "(all except) " + setString;
      }
    } else {
      if (set.isEmpty()) {
        return "none";
      } else {
        return setString;
      }
    }
  }

  @Override
  public String toString() {
    return this.printAll();
  }

//  public void copyFrom(HashAltSet<T> other) {
//    if (!compatibleWith(other)) {
//      throw new IllegalArgumentException("You can not copy from an Alt Set with a different max size");
//    }
//    this.set.clear();
//    this.set.addAll(other.set);
//    this.inverted = other.inverted;
//  }

//  private boolean compatibleWith(HashAltSet<T> other) {
//    return this.maxSize == other.maxSize;
//  }

  private static class NeverUber<T> extends HashAltSet<T> {

    private final Collection<T> options;

    private NeverUber(int maxSize, Collection<T> options) {
      super(maxSize);
      this.options = options;
    }

    @Override
    public void fill() {
      this.set.addAll(options);
      this.inverted = false;
    }

  }

  private static class MaybeUber<T> extends HashAltSet<T> {

    private MaybeUber(int maxSize) {
      super(maxSize);
    }

    @Override
    public void fill() {
      this.set.clear();
      this.inverted = true;
    }

  }

  public static class Infinite<T> extends MaybeUber<T> {
    public Infinite() {
      super(Integer.MAX_VALUE);
    }
  }

  public abstract static class FewLimited<T> extends NeverUber<T> {
    public FewLimited(Collection<T> options) {
      super(options.size(), options);
      if (maxSize > MAX_SIZE_BEFORE_UBER) {
        throw new IllegalArgumentException("You may not make a Small Limited Alt Set with more than "
            + MAX_SIZE_BEFORE_UBER + " possible options.");
      }
    }
  }

  public static class FewEnum<E extends Enum<E>> extends FewLimited<E> {
    public FewEnum(Class<E> clazz) {
      super(Arrays.asList(clazz.getEnumConstants()));
    }
  }

  public static class ManyLimited<T> extends MaybeUber<T> {
    private ManyLimited(int maxSize) {
      super(maxSize);
      if (maxSize <= MAX_SIZE_BEFORE_UBER) {
        throw new IllegalArgumentException("You may not make a Large Limited Alt Set with less "
            + "than or equal to"
            + MAX_SIZE_BEFORE_UBER + " possible options.");
      }
    }
  }

}
