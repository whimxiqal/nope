/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
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
 *
 */

package com.minecraftonline.nope.structures;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A data structure optimized to find any volumes which
 * contain a specific point in 3D space, where the Y dimension
 * is significantly smaller than X and Z.
 * <p>
 * We assume the regions are distributed evenly between all
 * dimensions between the maximum and minimum values.
 * <p>
 * Search in the following order:
 * XMIN -> ZMIN -> XMAX -> ZMAX -> ...
 * Then check Y values at the end
 */
public class VolumeTree<S, T extends VolumeTree.Volume> {

  enum Dimension {
    X, Z
  }

  enum Comparison {
    MIN, MAX
  }

  private final HashMap<S, T> volumes = Maps.newHashMap();
  private Node root = null;
  @Getter
  private int height = 0;
  @Getter
  private int size = 0;

  public VolumeTree() {
    construct();
  }

  public List<T> containingVolumes(int x, int y, int z) {
    if (this.root == null) {
      throw new IllegalStateException("Root of VolumeTree is not initialized");
    }
    return root.findVolumes(x, y, z).stream().map(volumes::get).collect(Collectors.toList());
  }

  public T push(S key, T volume) {
    T replaced = volumes.put(key, volume);
    construct();
    return replaced;
  }

  public void pushAll(Map<S, T> map) {
    volumes.putAll(map);
    construct();
  }

  public T remove(S key) {
    T removed = volumes.remove(key);
    construct();
    return removed;
  }

  public Set<S> keySet() {
    return volumes.keySet();
  }

  public T get(S key) {
    return volumes.get(key);
  }

  public Collection<T> volumes() {
    return volumes.values();
  }

  public boolean containsKey(S key) {
    return volumes.containsKey(key);
  }

  private void construct() {
    root = construct(Dimension.X, Comparison.MIN, Lists.newLinkedList(volumes.keySet()), 0);
    height = calculateHeight(root);
    size = calculateSize(root);
  }

  private Node construct(Dimension dimension,
                         Comparison comparison,
                         List<S> keys,
                         int unchangedCount) {
    int count = keys.size();

    if (count == 0) {
      return new EmptyNode();
    }

    if (count == 1 || unchangedCount >= 4) {
      return new ViabilityLeaf(Sets.newHashSet(keys));
    }

    int divIndex = count / 2;
    int divider;

    List<S> leftKeys;
    List<S> rightKeys;
    boolean changed;
    Node left;
    Node right;

    if (dimension == Dimension.X) {
      if (comparison == Comparison.MIN) {

        /* X MIN */
        keys.sort(Comparator.comparing(key -> volumes.get(key).getMinX()));
        while (divIndex > 0 && volumes.get(keys.get(divIndex)).getMinX() == volumes.get(keys.get(divIndex - 1)).getMinX()) {
          divIndex--;
        }
        divider = volumes.get(keys.get(divIndex)).getMinX();
        leftKeys = keys.subList(0, divIndex);
        rightKeys = Lists.newLinkedList(keys);
        // get rid of invalid elements in rightKeys
        changed = rightKeys.removeIf(key -> volumes.get(key).getMaxX() < divider);

        left = construct(Dimension.Z, Comparison.MIN, leftKeys, 0);
        right = construct(Dimension.Z, Comparison.MIN, rightKeys, changed ? 0 : unchangedCount + 1);
        return new DimensionDividerXMin(divider, left, right);
      } else {  // comparison == Comparison.MAX

        /* X MAX */
        keys.sort(Comparator.comparing(key -> volumes.get(key).getMaxX()));
        while (divIndex < count - 1 && volumes.get(keys.get(divIndex)).getMaxX() == volumes.get(keys.get(divIndex - 1)).getMaxX()) {
          divIndex++;
        }
        divider = volumes.get(keys.get(divIndex - 1)).getMaxX();
        leftKeys = Lists.newLinkedList(keys);
        rightKeys = keys.subList(divIndex, count);
        // get rid of invalid elements in leftKeys
        changed = leftKeys.removeIf(key -> volumes.get(key).getMinX() > divider);

        left = construct(Dimension.Z, Comparison.MAX, leftKeys, changed ? 0 : unchangedCount + 1);
        right = construct(Dimension.Z, Comparison.MAX, rightKeys, 0);
        return new DimensionDividerXMax(divider, left, right);
      }
    } else {  // dimension == Dimension.Z
      if (comparison == Comparison.MIN) {

        /* Z MIN */
        keys.sort(Comparator.comparing(key -> volumes.get(key).getMinZ()));
        while (divIndex > 0 && volumes.get(keys.get(divIndex)).getMinZ() == volumes.get(keys.get(divIndex - 1)).getMinZ()) {
          divIndex--;
        }
        divider = volumes.get(keys.get(divIndex)).getMinZ();
        leftKeys = keys.subList(0, divIndex);
        rightKeys = Lists.newLinkedList(keys);
        // get rid of invalid elements in rightKeys
        changed = rightKeys.removeIf(key -> volumes.get(key).getMaxZ() < divider);

        left = construct(Dimension.X, Comparison.MAX, leftKeys, 0);
        right = construct(Dimension.X, Comparison.MAX, rightKeys, changed ? 0 : unchangedCount + 1);
        return new DimensionDividerZMin(divider, left, right);
      } else {

        /* Z MAX */
        keys.sort(Comparator.comparing(key -> volumes.get(key).getMaxZ()));
        while (divIndex < count - 1 && volumes.get(keys.get(divIndex)).getMaxZ() == volumes.get(keys.get(divIndex - 1)).getMaxZ()) {
          divIndex++;
        }
        divider = volumes.get(keys.get(divIndex - 1)).getMaxZ();
        leftKeys = Lists.newLinkedList(keys);
        rightKeys = keys.subList(divIndex, count);
        // get rid of invalid elements in leftKeys
        changed = leftKeys.removeIf(key -> volumes.get(key).getMinZ() > divider);

        left = construct(Dimension.X, Comparison.MIN, leftKeys, changed ? 0 : unchangedCount + 1);
        right = construct(Dimension.X, Comparison.MIN, rightKeys, 0);
        return new DimensionDividerZMax(divider, left, right);
      }
    }


  }

  public interface Volume {

    /**
     * Get minimum X value, inclusive.
     *
     * @return x min
     */
    int getMinX();

    /**
     * Get maximum X value, inclusive.
     *
     * @return x max
     */
    int getMaxX();

    /**
     * Get minimum Y value, inclusive.
     *
     * @return y min
     */
    int getMinY();

    /**
     * Get maximum Y value, inclusive.
     *
     * @return y max
     */
    int getMaxY();

    /**
     * Get minimum Z value, inclusive.
     *
     * @return z min
     */
    int getMinZ();

    /**
     * Get maximum Z value, inclusive.
     *
     * @return z max
     */
    int getMaxZ();

    /**
     * Check if this volume contains a point, given three
     * cartesian coordinates.
     *
     * @param x x value
     * @param y y value
     * @param z z value
     * @return true if the point is contained
     */
    default boolean contains(int x, int y, int z) {
      return x >= this.getMinX()
          && x <= this.getMaxX()
          && y >= this.getMinY()
          && y <= this.getMaxY()
          && z >= this.getMinZ()
          && z <= this.getMaxZ();
    }

    /**
     * Check if this volume intersects with another volume
     * or shares a face.
     *
     * @param other another volume
     * @return true if intersects
     */
    default boolean intersects(Volume other) {
      return (this.getMinX() <= other.getMaxX() && this.getMaxX() >= other.getMinX()) &&
          (this.getMinY() <= other.getMaxY() && this.getMaxY() >= other.getMinY()) &&
          (this.getMinZ() <= other.getMaxZ() && this.getMaxZ() >= other.getMinZ());
    }

  }

  private abstract class Node {
    abstract Set<S> findVolumes(int x, int y, int z);
  }

  private class EmptyNode extends Node {
    @Override
    Set<S> findVolumes(int x, int y, int z) {
      return Sets.newHashSet();
    }
  }

  @EqualsAndHashCode(callSuper = false)
  @Data
  private abstract class DimensionDivider extends Node {
    protected final int divider;
    protected final Node left;
    protected final Node right;
  }

  private class DimensionDividerXMin extends DimensionDivider {
    public DimensionDividerXMin(int divider, Node left, Node right) {
      super(divider, left, right);
    }

    @Override
    Set<S> findVolumes(int x, int y, int z) {
      if (x < divider) {
        return left.findVolumes(x, y, z);
      } else {
        return right.findVolumes(x, y, z);
      }
    }
  }

  private class DimensionDividerXMax extends DimensionDivider {
    public DimensionDividerXMax(int divider, Node left, Node right) {
      super(divider, left, right);
    }

    @Override
    Set<S> findVolumes(int x, int y, int z) {
      if (x <= divider) {
        return left.findVolumes(x, y, z);
      } else {
        return right.findVolumes(x, y, z);
      }
    }
  }

  private class DimensionDividerZMin extends DimensionDivider {
    public DimensionDividerZMin(int divider, Node left, Node right) {
      super(divider, left, right);
    }

    @Override
    Set<S> findVolumes(int x, int y, int z) {
      if (z < divider) {
        return left.findVolumes(x, y, z);
      } else {
        return right.findVolumes(x, y, z);
      }
    }
  }

  private class DimensionDividerZMax extends DimensionDivider {
    public DimensionDividerZMax(int divider, Node left, Node right) {
      super(divider, left, right);
    }

    @Override
    Set<S> findVolumes(int x, int y, int z) {
      if (z <= divider) {
        return left.findVolumes(x, y, z);
      } else {
        return right.findVolumes(x, y, z);
      }
    }
  }

  private class ViabilityLeaf extends Node {

    final Set<S> viable = Sets.newHashSet();

    private ViabilityLeaf(Set<S> viable) {
      this.viable.addAll(viable);
    }

    @Override
    Set<S> findVolumes(int x, int y, int z) {
      Set<S> out = Sets.newHashSet();
      viable.stream().filter(i -> volumes.get(i).contains(x, y, z)).forEach(out::add);
      return out;
    }
  }

  private int calculateHeight(Node node) {
    if (node instanceof VolumeTree.DimensionDivider) {
      return 1 + Math.max(
          calculateHeight(((DimensionDivider) node).left),
          calculateHeight(((DimensionDivider) node).right));
    }
    return 0;
  }

  private int calculateSize(Node node) {
    if (node instanceof VolumeTree.DimensionDivider) {
      return 1 + calculateSize(((DimensionDivider) node).left)
          + calculateSize(((DimensionDivider) node).right);
    }
    return 0;
  }

  public void print() {
    int xWidth = (0x1 << (getHeight()) + 2);
    int yWidth = getHeight() * 3;
    char[][] treeBoard = new char[xWidth][yWidth];
    for (int x = 0; x < xWidth; x++) {
      for (int y = 0; y < yWidth; y++) {
        treeBoard[x][y] = ' ';
      }
    }
    print(treeBoard, root, xWidth / 2, 0, xWidth);
    for (int y = 0; y < yWidth; y++) {
      for (int x = 0; x < xWidth; x++) {
        System.out.print(treeBoard[x][y]);
      }
      System.out.println();
    }
  }

  private void print(char[][] board, Node node, int x, int y, int xWidth) {
    if (node instanceof VolumeTree.EmptyNode) {
      board[x][y] = '[';
      board[x + 1][y] = ']';
      return;
    }
    if (node instanceof VolumeTree.ViabilityLeaf) {
      for (S key : ((ViabilityLeaf) node).viable) {
        for (char c : key.toString().toCharArray()) {
          board[x++][y] = c;
        }
        board[x++][y] = ',';
      }
      return;
    }
    if (node instanceof VolumeTree.DimensionDivider) {
      int divider = ((DimensionDivider) node).divider;
      int a = x;
      for (char c : String.valueOf(divider).toCharArray()) {
        board[a++][y] = c;
      }
      for (int i = x - xWidth / 8 + 1; i < x + xWidth / 8; i++) {
        if (i < x - 1 || i > a) {
          board[i][y] = '_';
        }
      }
      board[x - xWidth / 8][y + 1] = '/';
      board[x + xWidth / 8][y + 1] = '\\';
      for (int i = x - xWidth / 4 + 2; i < x - xWidth / 8; i++) {
        board[i][y + 1] = '_';
      }
      for (int i = x + xWidth / 8 + 1; i < x + xWidth / 4; i++) {
        board[i][y + 1] = '_';
      }
      print(board, ((DimensionDivider) node).left, x - xWidth / 4, y + 2, xWidth / 2);
      print(board, ((DimensionDivider) node).right, x + xWidth / 4, y + 2, xWidth / 2);
    }
  }
}
