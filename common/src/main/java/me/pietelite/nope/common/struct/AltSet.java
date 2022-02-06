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
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * An alternative to a {@link Set} that allow for an "inverted" state, in which
 * the set is considered to hold everything <i>except</i> some set of values.
 * As such, you can "fill" an {@link AltSet}, basically saying that the set now contains all
 * possible values of the given type {@link T}.
 *
 * <p>This does not implement {@link Set} because there are a few methods that are incongruous,
 * such as {@link Set#iterator()}.
 *
 * @param <T> the type of value stored in this set.
 */
public interface AltSet<T> {

  /**
   * Run {@link #fill()} on the given set and return it.
   *
   * @param set a set to fill
   * @param <X> the type of {@link AltSet} used and to return
   * @return the filled {@link AltSet}
   */
  static <X extends AltSet<?>> X full(X set) {
    set.fill();
    return set;
  }

  /**
   * Return whether this set contains any terms.
   * Returns false if the set is full.
   *
   * @return true if empty
   * @see Set#isEmpty()
   */
  boolean isEmpty();

  /**
   * Return whether this set contains all possible terms.
   * Returns false if the set is empty or only contains some terms.
   *
   * @return true if full
   */
  boolean isFull();

  /**
   * Return whether this set contains this element.
   *
   * @param element the element
   * @return true if contains
   * @see Set#contains(Object)
   */
  boolean contains(T element);

  /**
   * Try to add the element to the set.
   *
   * @param element the element to add
   * @return false if the element could not be added, like if it was already in the set
   * @see Set#add(Object)
   */
  boolean add(T element);

  /**
   * Try to remove the element from the set.
   *
   * @param element the element to remove
   * @return false if the element could not be added, like if it wasn't in the set before
   * @see Set#remove(Object)
   */
  boolean remove(T element);

  /**
   * Check whether the set contains all the same elements as the other
   * {@link AltSet}. For example, if the other {@link AltSet} was full,
   * this would only return true if this {@link AltSet} were full.
   *
   * @param other the other set
   * @return true if all are contained
   * @see Set#containsAll(Collection)
   */
  boolean containsAll(@NotNull AltSet<T> other);

  /**
   * Try to add all elements in another {@link AltSet}.
   *
   * @param other the other set.
   */
  void addAll(@NotNull AltSet<T> other);

  /**
   * Try to add all elements in some {@link Collection}.
   *
   * @param collection the collection
   * @return true if all could be added, as individually specified in {@link #add}
   * @see Set#addAll(Collection)
   */
  boolean addAll(@NotNull Collection<T> collection);

  /**
   * Try to retain only the elements that exist in the other {@link AltSet}.
   *
   * @param other the other set
   */
  void retainAll(@NotNull AltSet<T> other);

  /**
   * Try to remove all elements found in another {@link AltSet}.
   *
   * @param other the other set
   */
  void removeAll(@NotNull AltSet<T> other);

  /**
   * Try to remove all elements found in some {@link Collection}.
   *
   * @param collection the collection
   * @return true if all were removed, as individually specified in {@link #remove}
   * @see Set#removeAll(Collection)
   */
  boolean removeAll(@NotNull Collection<T> collection);

  /**
   * Remove all elements from this set.
   */
  void clear();

  /**
   * Add all possible elements to the set.
   * Once the set is full, {@link #isEmpty()} will return false and {@link #contains} will always
   * return true. You may still {@link #add} and {@link #remove} as normal.
   *
   * <p>This method is not found in a normal {@link Set} and is what makes this class important.
   * While some types of objects can only be combined into a set in some finite number of combinations,
   * like enums, some types cannot, like integers or strings. Put another way, the
   * <a href=https://en.wikipedia.org/wiki/Power_set>Power Set</a> of which the elements are some
   * single Java data type may be infinitely large.
   *
   * <p>This method allows us to accommodate such cases by simply saying that "all types are present here",
   * possibly without actually storing any individual values.
   */
  void fill();

  /**
   * Invert the set so all element that the set contains now does not contain and vice versa.
   *
   * <p>For example, if you have an empty {@link AltSet}, add 1, and then invert the set,
   * the set does not contain 1.
   */
  void invert();

  /**
   * The set is inverted if the underlying data structure handling all normal
   * set behavior is considering the contained values to be "everything except"
   * a specific set of values. Use {@link #set()} to obtain this underlying set.
   *
   * @return true if this set is considered "inverted"
   * @see #set()
   */
  boolean inverted();

  /**
   * Get the underlying set structure that is responsible for most logic.
   *
   * @return the set.
   */
  Set<T> set();

  /**
   * Print the entire set in a suitable way to represent its contents.
   *
   * @return a printed version of the set
   */
  String printAll();

}
