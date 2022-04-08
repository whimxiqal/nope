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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import me.pietelite.nope.common.api.struct.AltSet;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link AltSet} using a {@link HashSet}
 * as its underlying data structure.
 *
 * @param <T> the element type to store in this set
 */
public abstract class HashAltSet<T> implements AltSet<T> {

  public static final int STANDARD_MAX_SIZE = 8;

  protected final int maxSize;
  /**
   * This set is "normal" when "inverted" is false because this object
   * works the same way as a normal HashSet.
   * This set is "subtractive" from everything when uber if true
   * because this object works negatively from all terms in uber mode.
   */
  protected final HashSet<T> set = new HashSet<>();
  protected boolean inverted;

  protected HashAltSet(int maxSize) {
    this.maxSize = maxSize;
  }

  /**
   * Create a set that can hold an infinite amount of elements,
   * like all {@link Integer}s or {@link String}s.
   *
   * @param <X> the type stored in the set
   * @return a new set
   */
  public static <X> HashAltSet<X> infinite() {
    return new UnboundedAlternate<>();
  }

  /**
   * Create a set that can only hold a finite number of elements.
   * This value may not be less than or equal to this class's
   * <code>STANDARD_MAX_SIZE</code> unless you use {@link #finite(int, Supplier)}.
   *
   * @param maxSize set the maximum number of elements that can be held in this set
   * @param <X>     the type stored in the set
   * @return a new set
   */
  public static <X> HashAltSet<X> finite(int maxSize) {
    if (maxSize <= STANDARD_MAX_SIZE) {
      throw new IllegalArgumentException("If the max size is below "
          + STANDARD_MAX_SIZE
          + ", you must specify a supplier for all possible elements");
    }
    return finiteLarge(maxSize);
  }

  /**
   * Create a set that can only hold a finite number of elements.
   *
   * @param maxSize  set the maximum number of elements that can be held in this set
   * @param elements a getter for all possible elements in this set
   * @param <X>      the type stored in the set
   * @return a new set
   */
  public static <X> HashAltSet<X> finite(int maxSize, Supplier<Collection<X>> elements) {
    if (maxSize <= STANDARD_MAX_SIZE) {
      return finiteSmall(elements.get());
    } else {
      return finiteLarge(maxSize);
    }
  }

  private static <X> HashAltSet<X> finiteSmall(Collection<X> elements) {
    return new Standard<>(elements.size(), elements);
  }

  private static <X> HashAltSet<X> finiteLarge(int maxSize) {
    return new Alternate<>(maxSize);
  }

  /**
   * Create a set that stores enums.
   *
   * @param clazz the enum class
   * @param <E>   the type of enum
   * @return a new set
   */
  public static <E extends Enum<E>> HashAltSet<E> ofEnum(Class<E> clazz) {
    E[] enums = clazz.getEnumConstants();
    return finite(enums.length, () -> Arrays.asList(enums));
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
  public boolean isFull() {
    if (inverted) {
      return set.isEmpty();
    } else {
      return set.size() == maxSize;
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
  public boolean addAll(@NotNull Collection<T> collection) {
    boolean success = true;
    for (T element : collection) {
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
  public boolean removeAll(@NotNull Collection<T> collection) {
    boolean success = true;
    for (T element : collection) {
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

  /**
   * An {@link AltSet} that works effectively just like a {@link HashSet}.
   * In other words, it cannot be inverted.
   *
   * @param <T> the type stored in the set
   */
  public static class Standard<T> extends HashAltSet<T> {

    private final Collection<T> options;

    private Standard(int maxSize, Collection<T> options) {
      super(maxSize);
      this.options = options;
    }

    @Override
    public void fill() {
      this.set.addAll(options);
      this.inverted = false;
    }

  }

  /**
   * An {@link AltSet} that can hold only a finite quantity of values.
   * This type is designed to possible hold many values, though, because
   * this type can alternate between an inverted and non-inverted state.
   *
   * @param <T> the type stored in the set
   */
  private static class Alternate<T> extends HashAltSet<T> {

    private Alternate(int maxSize) {
      super(maxSize);
    }

    @Override
    public void fill() {
      this.set.clear();
      this.inverted = true;
    }

  }

  /**
   * An {@link AltSet} that can hold an infinite quantity of values.
   *
   * @param <T> the type stored in the set
   */
  public static class UnboundedAlternate<T> extends Alternate<T> {

    /**
     * General constructor that sets the "max size" to infinity.
     */
    public UnboundedAlternate() {
      super(Integer.MAX_VALUE);
    }
  }

  /**
   * A standard {@link AltSet} that can only hold a few number of values.
   * This number is determined by <code>HashAltSet.STANDARD_MAX_SIZE</code>
   *
   * @param <T> the type stored in the set
   */
  public abstract static class FewStandard<T> extends Standard<T> {

    /**
     * General constructor that ensures there are only a few possible values to put in the set.
     *
     * @param options the possible values to store in this set
     */
    public FewStandard(Collection<T> options) {
      super(options.size(), options);
      if (maxSize > STANDARD_MAX_SIZE) {
        throw new IllegalArgumentException("You may not make a Small Limited Alt Set with more than "
            + STANDARD_MAX_SIZE + " possible options.");
      }
    }
  }

  /**
   * A set designed to be able to store only a few enums.
   *
   * @param <E> the type of enum stored in the set
   */
  public static class FewEnum<E extends Enum<E>> extends FewStandard<E> {

    /**
     * General constructor.
     *
     * @param clazz the enum class
     */
    public FewEnum(Class<E> clazz) {
      super(Arrays.asList(clazz.getEnumConstants()));
    }
  }

}
