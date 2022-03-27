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
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * A special type of {@link Set} that only stores one value.
 *
 * @param <T> the type of data stored here
 */
public class SingleValueSet<T> implements Set<T> {

  private final T value;

  public SingleValueSet(@NotNull T value) {
    this.value = Objects.requireNonNull(value);
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean contains(Object o) {
    return value.equals(o);
  }

  @NotNull
  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {

      private boolean moved = false;

      @Override
      public boolean hasNext() {
        return !moved;
      }

      @Override
      public T next() {
        if (moved) {
          throw new NoSuchElementException();
        } else {
          moved = true;
          return value;
        }
      }
    };
  }

  @Override
  public Object @NotNull [] toArray() {
    return new Object[]{value};
  }

  @Override
  public <T1> T1 @NotNull [] toArray(T1 @NotNull [] a) {
    Set<T> helperSet = new HashSet<>();
    helperSet.add(value);
    return helperSet.toArray(a);
  }

  @Override
  public boolean add(T t) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(@NotNull Collection<?> c) {
    if (c.size() == 1) {
      for (Object o : c) {
        if (value.equals(o)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean addAll(@NotNull Collection<? extends T> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(@NotNull Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(@NotNull Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }
}
