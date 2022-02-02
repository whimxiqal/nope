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

package com.minecraftonline.nope.common.host;

import com.minecraftonline.nope.common.math.Cuboid;
import com.minecraftonline.nope.common.math.Dimension;
import com.minecraftonline.nope.common.math.Geometry;
import com.minecraftonline.nope.common.math.Volume;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * A data structure optimized to find any volumes which
 * contain a specific point in 3D space, where the Y dimension
 * is significantly smaller than X and Z.
 *
 * <p>We assume the zones are distributed evenly between all
 * dimensions between the maximum and minimum values.
 *
 * <p>Search in the following order:
 * Min X -> Min Z -> Max X -> Max Z -> ...
 * Then check Y values at the end
 */
public class VolumeTree {

  private final Map<Volume, Zone> volumes = new HashMap<>();
  protected Node root = null;
  @Getter
  protected int height = 0;
  @Getter
  protected int size = 0;

  public VolumeTree(boolean construct) {
    construct();
  }

  public VolumeTree() {
    this(false);
  }

  /**
   * Calculate all zones which have any intersecting volumes
   * with this one.
   *
   * @param zone the zone to check for intersection
   * @return all intersecting zones
   */
  public Set<Zone> intersecting(Zone zone) {
    Set<Zone> all = new HashSet<>();
    for (Volume volume : zone.volumes) {
      all.addAll(intersecting(volume));
    }
    return all;
  }

  /**
   * Calculate all zones which have any intersecting volumes
   * with this volume.
   *
   * @param volume the volume to check for intersection
   * @return all intersecting zones
   */
  public Set<Zone> intersecting(Volume volume) {
    Set<Zone> all = new HashSet<>();
    volumes.forEach((v, z) -> {
      if (Geometry.intersects(v, volume) && v != volume) {
        all.add(z);
      }
    });
    return all;
  }

  public Set<Zone> containing(Volume volume, boolean discriminate) {
    Set<Zone> all = new HashSet<>();
    Cuboid approximation = discriminate ? volume.circumscribed() : volume.inscribed();
    int[] listX = new int[]{approximation.minX(), approximation.maxX()};
    int[] listY = new int[]{approximation.minY(), approximation.maxY()};
    int[] listZ = new int[]{approximation.minZ(), approximation.maxZ()};
    boolean first = true;
    for (int x = 0; x < 2; x++) {
      for (int y = 0; y < 2; y++) {
        for (int z = 0; z < 2; z++) {
          if (first) {
            all.addAll(containing(listX[x], listY[y], listZ[z]));
            first = false;
          } else {
            all.retainAll(containing(listX[x], listY[y], listZ[z]));
          }
        }
      }
    }
    return all;
  }

  @NotNull
  public Set<Zone> containing(int x, int y, int z) {
    if (this.root == null) {
      throw new IllegalStateException("Root of VolumeTree is not initialized. Did you forget to construct?");
    }
    return root.findVolumes(x, y, z).stream().map(volumes::get).collect(Collectors.toSet());
  }

  public int size() {
    return size;
  }

  public void construct() {
    root = construct(Dimension.X, Comparison.MIN, new ArrayList<>(this.volumes.keySet()), 0);
    height = calculateHeight(root);
    size = calculateSize(root);
  }

  protected final Node construct(Dimension dimension,
                                 Comparison comparison,
                                 List<Volume> subset,
                                 int unchangedCount) {
    int count = subset.size();

    if (count == 0) {
      return new EmptyNode();
    }

    if (count == 1 || unchangedCount >= 4) {
      return new ViabilityLeaf(new HashSet<>(subset));
    }

    int divIndex = count / 2;
    int divider;

    List<Volume> leftKeys;
    List<Volume> rightKeys;
    boolean changed;
    Node left;
    Node right;

    if (dimension == Dimension.X) {
      if (comparison == Comparison.MIN) {

        /* X MIN */
        subset.sort(Comparator.comparing(volume -> volume.circumscribed().minX()));
        while (divIndex > 0 && (
            subset.get(divIndex).circumscribed().minX()
                == subset.get(divIndex - 1).circumscribed().minX())
        ) {
          divIndex--;
        }
        divider = subset.get(divIndex).circumscribed().minX();
        leftKeys = subset.subList(0, divIndex);
        rightKeys = new LinkedList<>(subset);
        // get rid of invalid elements in rightKeys
        changed = rightKeys.removeIf(volume -> volume.circumscribed().maxX() < divider);

        left = construct(Dimension.Z, Comparison.MIN, leftKeys, 0);
        right = construct(Dimension.Z, Comparison.MIN, rightKeys, changed ? 0 : unchangedCount + 1);
        return new DimensionDividerMinX(divider, left, right);
      } else {  // comparison == Comparison.MAX

        /* X MAX */
        subset.sort(Comparator.comparing(volume -> volume.circumscribed().maxX()));
        while (divIndex < count - 1 && (
            subset.get(divIndex).circumscribed().maxX()
                == subset.get(divIndex - 1).circumscribed().maxX())
        ) {
          divIndex++;
        }
        divider = subset.get(divIndex - 1).circumscribed().maxX();
        leftKeys = new LinkedList<>(subset);
        rightKeys = subset.subList(divIndex, count);
        // get rid of invalid elements in leftKeys
        changed = leftKeys.removeIf(volume -> volume.circumscribed().minX() > divider);

        left = construct(Dimension.Z, Comparison.MAX, leftKeys, changed ? 0 : unchangedCount + 1);
        right = construct(Dimension.Z, Comparison.MAX, rightKeys, 0);
        return new DimensionDividerMaxX(divider, left, right);
      }
    } else {  // dimension == Dimension.Z
      if (comparison == Comparison.MIN) {

        /* Z MIN */
        subset.sort(Comparator.comparing(volume -> volume.circumscribed().minZ()));
        while (divIndex > 0 && (
            subset.get(divIndex).circumscribed().minZ()
                == subset.get(divIndex - 1).circumscribed().minZ())
        ) {
          divIndex--;
        }
        divider = subset.get(divIndex).circumscribed().minZ();
        leftKeys = subset.subList(0, divIndex);
        rightKeys = new LinkedList<>(subset);
        // get rid of invalid elements in rightKeys
        changed = rightKeys.removeIf(volume -> volume.circumscribed().maxZ() < divider);

        left = construct(Dimension.X, Comparison.MAX, leftKeys, 0);
        right = construct(Dimension.X, Comparison.MAX, rightKeys, changed ? 0 : unchangedCount + 1);
        return new DimensionDividerMinZ(divider, left, right);
      } else {

        /* Z MAX */
        subset.sort(Comparator.comparing(volume -> volume.circumscribed().maxZ()));
        while (divIndex < count - 1 && (
            subset.get(divIndex).circumscribed().maxZ()
                == subset.get(divIndex - 1).circumscribed().maxZ())
        ) {
          divIndex++;
        }
        divider = subset.get(divIndex - 1).circumscribed().maxZ();
        leftKeys = new LinkedList<>(subset);
        rightKeys = subset.subList(divIndex, count);
        // get rid of invalid elements in leftKeys
        changed = leftKeys.removeIf(volume -> volume.circumscribed().minZ() > divider);

        left = construct(Dimension.X, Comparison.MIN, leftKeys, changed ? 0 : unchangedCount + 1);
        right = construct(Dimension.X, Comparison.MIN, rightKeys, 0);
        return new DimensionDividerMaxZ(divider, left, right);
      }
    }


  }

  protected int calculateHeight(Node node) {
    if (node instanceof DimensionDivider) {
      return 1 + Math.max(
          calculateHeight(((DimensionDivider) node).left),
          calculateHeight(((DimensionDivider) node).right));
    }
    return 0;
  }

  protected int calculateSize(Node node) {
    if (node instanceof VolumeTree.DimensionDivider) {
      return 1 + calculateSize(((DimensionDivider) node).left)
          + calculateSize(((DimensionDivider) node).right);
    }
    return 0;
  }

  public void put(Volume volume, Zone zone, boolean construct) {
    this.volumes.put(volume, zone);
    if (construct) {
      construct();
    }
  }

  public Collection<Volume> all() {
    return volumes.keySet();
  }

  public Volume remove(Volume volume, boolean construct) {
    Volume toRemove = volumes.containsKey(volume) ? volume : null;
    this.volumes.remove(volume);
    if (construct) {
      construct();
    }
    return toRemove;
  }

  enum Comparison {
    MIN, MAX
  }

  /**
   * A node in the volume tree. It returns volumes that contain the given
   * coordinates.
   */
  protected abstract static class Node {
    abstract Set<Volume> findVolumes(int x, int y, int z);
  }

  /**
   * A node that reports 0 volumes.
   */
  protected static class EmptyNode extends Node {
    @Override
    Set<Volume> findVolumes(int x, int y, int z) {
      return new HashSet<>();
    }
  }

  /**
   * A node which represents a division across a dimension
   * at some dividing integer value.
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  protected abstract static class DimensionDivider extends Node {
    protected final int divider;
    protected final Node left;
    protected final Node right;
  }

  /**
   * A dividing node which represents a division for
   * the minimum x value of {@link Cuboid}s.
   */
  protected static class DimensionDividerMinX extends DimensionDivider {
    public DimensionDividerMinX(int divider, Node left, Node right) {
      super(divider, left, right);
    }

    @Override
    Set<Volume> findVolumes(int x, int y, int z) {
      if (x < divider) {
        return left.findVolumes(x, y, z);
      } else {
        return right.findVolumes(x, y, z);
      }
    }
  }

  /**
   * A dividing node which represents a division for
   * the maximum x value of {@link Cuboid}s.
   */
  protected static class DimensionDividerMaxX extends DimensionDivider {
    public DimensionDividerMaxX(int divider, Node left, Node right) {
      super(divider, left, right);
    }

    @Override
    Set<Volume> findVolumes(int x, int y, int z) {
      if (x <= divider) {
        return left.findVolumes(x, y, z);
      } else {
        return right.findVolumes(x, y, z);
      }
    }
  }

  /**
   * A dividing node which represents a division for
   * the minimum z value of {@link Cuboid}s.
   */
  protected static class DimensionDividerMinZ extends DimensionDivider {
    public DimensionDividerMinZ(int divider, Node left, Node right) {
      super(divider, left, right);
    }

    @Override
    Set<Volume> findVolumes(int x, int y, int z) {
      if (z < divider) {
        return left.findVolumes(x, y, z);
      } else {
        return right.findVolumes(x, y, z);
      }
    }
  }

  /**
   * A dividing node which represents a division for
   * the maximum z value of {@link Cuboid}s.
   */
  protected static class DimensionDividerMaxZ extends DimensionDivider {
    public DimensionDividerMaxZ(int divider, Node left, Node right) {
      super(divider, left, right);
    }

    @Override
    Set<Volume> findVolumes(int x, int y, int z) {
      if (z <= divider) {
        return left.findVolumes(x, y, z);
      } else {
        return right.findVolumes(x, y, z);
      }
    }
  }


  /**
   * A "leaf" of a tree which essentially just iterates through a set
   * of "viable" volumes and gives all volumes which do actually
   * contain the coordinates.
   */
  protected static class ViabilityLeaf extends Node {

    final Set<Volume> viable = new HashSet<>();

    private ViabilityLeaf(Set<Volume> viable) {
      this.viable.addAll(viable);
    }

    @Override
    Set<Volume> findVolumes(int x, int y, int z) {
      return viable.stream().filter(volume -> volume.containsPoint(x, y, z)).collect(Collectors.toSet());
    }
  }

}
