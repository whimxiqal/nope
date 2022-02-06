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

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AltSetTest {

  @Test
  void isEmpty() {
    AltSet<Integer> set = HashAltSet.infinite();
    Assertions.assertTrue(set.isEmpty());
    set.add(1);
    Assertions.assertFalse(set.isEmpty());
    set.fill();
    Assertions.assertFalse(set.isEmpty());
    set.add(1);
    Assertions.assertFalse(set.isEmpty());
  }

  @Test
  void contains() {
    AltSet<Integer> set = HashAltSet.infinite();
    Assertions.assertFalse(set.contains(0));
    set.add(0);
    Assertions.assertTrue(set.contains(0));
    set.fill();
    Assertions.assertTrue(set.contains(10));
    set.remove(10);
    Assertions.assertFalse(set.contains(10));
  }

  @Test
  void add() {
    AltSet<Integer> set = HashAltSet.infinite();
    Assertions.assertTrue(set.add(0));
    Assertions.assertFalse(set.add(0));
    Assertions.assertTrue(set.add(5));
    set.fill();
    Assertions.assertFalse(set.add(10));
  }

  @Test
  void remove() {
    AltSet<Integer> set = HashAltSet.infinite();
    Assertions.assertFalse(set.remove(0));
    set.fill();
    Assertions.assertTrue(set.remove(1));
    Assertions.assertFalse(set.remove(1));
  }

  @Test
  void containsAll() {
    HashAltSet<Integer> set1 = HashAltSet.infinite();
    HashAltSet<Integer> set2 = HashAltSet.infinite();
    set1.addAll(Arrays.asList(1, 2, 3, 4, 5));
    set2.addAll(Arrays.asList(4, 5));
    Assertions.assertTrue(set1.containsAll(set2));
    set2.add(6);
    Assertions.assertFalse(set1.containsAll(set2));
    set1.fill();
    set1.remove(10);
    Assertions.assertTrue(set1.containsAll(set2));
    set1.remove(5);
    Assertions.assertFalse(set1.containsAll(set2));
    set2.fill();
    Assertions.assertFalse(set1.containsAll(set2));
    set2.removeAll(Arrays.asList(10, 11, 5));
    System.out.println(set1.printAll());
    System.out.println(set2.printAll());
    Assertions.assertTrue(set1.containsAll(set2));
    set1.clear();
    Assertions.assertFalse(set1.containsAll(set2));
    set1.add(4);
    Assertions.assertTrue(set2.containsAll(set1));

    AltSet<Integer> setA = HashAltSet.finite(100);
    HashAltSet<Integer> setB = HashAltSet.finite(100);
    // Fill set B with "all the numbers" (it has no idea what they actually are)
    setB.fill();
    Assertions.assertFalse(setA.containsAll(setB));
    // Fill set A with numbers 0-100... because the max sizes are both 100, we can say that A is now filled
    setA.addAll(IntStream.range(0, 100).<HashSet<Integer>>collect(HashSet::new,
        HashSet::add,
        AbstractCollection::addAll));
    Assertions.assertTrue(setA.containsAll(setB));
    setB.remove(5);
    Assertions.assertTrue(setA.containsAll(setB));
    setA.remove(3);
    Assertions.assertFalse(setA.containsAll(setB));

    AltSet<Animal> setC = HashAltSet.ofEnum(Animal.class);
    HashAltSet<Animal> setD = HashAltSet.ofEnum(Animal.class);
    setD.fill();
    Assertions.assertFalse(setC.containsAll(setD));
    setC.addAll(Arrays.asList(Animal.CAT, Animal.DOG, Animal.MOUSE, Animal.BIRD));
    Assertions.assertTrue(setC.containsAll(setD));
    setD.remove(Animal.CAT);
    Assertions.assertTrue(setC.containsAll(setD));
    setC.remove(Animal.BIRD);
    Assertions.assertFalse(setC.containsAll(setD));
  }

  @Test
  void addAll() {
    AltSet<Integer> set1 = HashAltSet.infinite();
    HashAltSet<Integer> set2 = HashAltSet.infinite();

    set2.fill();
    set1.addAll(set2);
    Assertions.assertFalse(set1.isEmpty());
    Assertions.assertTrue(set1.contains(1));

    set1.remove(5);
    set1.remove(6);
    set2.clear();
    set2.add(5);
    set1.addAll(set2);
    Assertions.assertTrue(set1.contains(4));
    Assertions.assertTrue(set1.contains(5));
    Assertions.assertFalse(set1.contains(6));
  }

  @Test
  void retainAll() {
    AltSet<Integer> set1 = HashAltSet.infinite();
    HashAltSet<Integer> set2 = HashAltSet.infinite();
    set1.add(0);
    set2.add(1);
    set1.retainAll(set2);
    Assertions.assertTrue(set1.isEmpty());
    set1.add(0);
    set2.add(0);
    set2.add(1);
    set1.retainAll(set2);
    Assertions.assertTrue(set1.contains(0));
    set2.fill();
    set1.add(10);
    set1.retainAll(set2);
    Assertions.assertTrue(set1.contains(10));
    Assertions.assertFalse(set1.contains(9));
    set1.fill();
    set2.remove(3);
    set1.retainAll(set2);
    Assertions.assertFalse(set1.contains(3));
    Assertions.assertTrue(set1.contains(4));
    set2.clear();
    set2.add(6);
    set1.retainAll(set2);
    Assertions.assertTrue(set1.contains(6));
    Assertions.assertFalse(set1.contains(7));
  }

  @Test
  void removeAll() {
    AltSet<Integer> set1 = HashAltSet.infinite();
    HashAltSet<Integer> set2 = HashAltSet.infinite();
    set1.add(0);
    set1.add(1);
    set2.add(1);
    set1.removeAll(set2);
    Assertions.assertTrue(set1.contains(0));
    Assertions.assertFalse(set1.contains(1));
    set2.fill();
    set2.remove(3);
    set1.removeAll(set2);
    Assertions.assertFalse(set1.contains(0));
    Assertions.assertFalse(set1.contains(3));
    set1.add(3);
    set1.removeAll(set2);
    Assertions.assertTrue(set1.contains(3));
    set1.fill();
    set1.remove(5);
    Assertions.assertTrue(set1.contains(3));
    Assertions.assertTrue(set1.contains(11));
    set1.removeAll(set2);
    Assertions.assertTrue(set1.contains(3));
    Assertions.assertFalse(set1.contains(11));
    set1.fill();
    set2.clear();
    set2.add(0);
    set1.removeAll(set2);
    Assertions.assertFalse(set1.contains(0));
    Assertions.assertTrue(set1.contains(5));
  }

  enum Animal {
    CAT,
    DOG,
    MOUSE,
    BIRD,
  }
}