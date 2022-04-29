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

package me.pietelite.nope.common.host;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.pietelite.nope.common.math.Cuboid;
import me.pietelite.nope.common.math.Dimension;
import me.pietelite.nope.common.math.Geometry;
import me.pietelite.nope.common.math.Volume;
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
 * Min X, Min Z, Max X, Max Z, ...
 * Then check Y values at the end
 */
public class VolumeTree {

  private final Map<Volume, Scene> volumes = new HashMap<>();
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
   * @param scene the scene to check for intersection
   * @return all intersecting zones
   */
  public Set<Scene> intersecting(Scene scene) {
    Set<Scene> all = new HashSet<>();
    for (Volume volume : scene.volumes) {
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
  public Set<Scene> intersecting(Volume volume) {
    Set<Scene> all = new HashSet<>();
    volumes.forEach((v, z) -> {
      if (Geometry.intersects(v, volume) && v != volume) {
        all.add(z);
      }
    });
    return all;
  }

  /**
   * Get all {@link Scene}s that wholly contain the given {@link Volume}.
   * The math for this is pretty complex, so to simplify this, we check how this volume is contained
   * by checking the volumes that contain the corners of a cuboid that's merely an approximation of
   * the given volume.
   *
   * <p>The <code>discriminate</code> parameter decides how strict to be with the approximation, depending
   * on whether it is better to accidentally end up with some added incorrect {@link Scene}s or to accidentally
   * end up with some missing correct {@link Scene}s.
   *
   * @param volume       the volume to contain
   * @param discriminate true to ensure that every correct {@link Scene} is included in the returned list,
   *                     but extras are possible. False to ensure that there are no extra {@link Scene}s
   *                     included, but it may be missing some correct ones
   * @return the set of {@link Scene}s containing this {@link Volume}
   */
  public Set<Scene> containing(Volume volume, boolean discriminate) {
    Set<Scene> all = new HashSet<>();
    Cuboid approximation = discriminate ? volume.circumscribed() : volume.inscribed();
    float[] listX = new float[]{approximation.minX(), approximation.maxX()};
    float[] listY = new float[]{approximation.minY(), approximation.maxY()};
    float[] listZ = new float[]{approximation.minZ(), approximation.maxZ()};
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

  /**
   * Get the set of {@link Volume}s that contain this given point.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param z the z coordinate
   * @return the set of zones
   */
  @NotNull
  public Set<Scene> containing(float x, float y, float z) {
    return containingCuboid(x, y, z, x, y, z, false);
  }

  /**
   * Get the set of {@link Volume}s that contain this given point.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param z the z coordinate
   * @return the set of zones
   */
  @NotNull
  public Set<Scene> containingBlock(int x, int y, int z) {
    return containingCuboid(x, y, z, x + 1, y + 1, z + 1, true);

  }

  /**
   * Get the set of {@link Volume}s that contain this given cuboid.
   *
   * @param minX the minimum X value
   * @param minY the minimum Y value
   * @param minZ the minimum Z value
   * @param maxX the maximum X value
   * @param maxY the maximum Y value
   * @param maxZ the maximum Z value
   * @param maxInclusive whether we will say it is contained with inclusive max boundaries
   * @return the set of zones
   */
  @NotNull
  public Set<Scene> containingCuboid(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, boolean maxInclusive) {
    if (this.root == null) {
      throw new IllegalStateException("Root of VolumeTree is not initialized. Did you forget to construct?");
    }
    return root.findVolumes(minX, minY, minZ, maxX, maxY, maxZ, maxInclusive).stream().map(volumes::get).collect(Collectors.toSet());
  }

  public int size() {
    return size;
  }

  /**
   * Construct the internal data structure for querying containing {@link Volume}s.
   */
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
    float divider;

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

  /**
   * Add a new volume into the volume tree.
   *
   * @param volume    the volume
   * @param scene      the scene of which the volume is a part
   * @param construct whether to construct the entire tree again. If this is called multiple
   *                  times consecutively, this should be false and then this stucture should
   *                  be rebuilt again manually with {@link #construct()}
   */
  public void put(Volume volume, Scene scene, boolean construct) {
    this.volumes.put(volume, scene);
    if (construct) {
      construct();
    }
  }

  public Collection<Volume> all() {
    return volumes.keySet();
  }

  /**
   * Remove a volume from the tree.
   *
   * @param volume    the volume
   * @param construct whether to construct the entire tree again. If this is called multiple
   *                  times consecutively, this should be false and then this stucture should
   *                  be rebuilt again manually with {@link #construct()}
   * @return the removed volume (the input) or null if nothing was removed
   */
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
    abstract Set<Volume> findVolumes(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, boolean maxInclusive);
  }

  /**
   * A node that reports 0 volumes.
   */
  protected static class EmptyNode extends Node {
    @Override
    Set<Volume> findVolumes(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, boolean maxInclusive) {
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
    protected final float divider;
    protected final Node left;
    protected final Node right;
  }

  /**
   * A dividing node which represents a division for
   * the minimum x value of {@link Cuboid}s.
   */
  protected static class DimensionDividerMinX extends DimensionDivider {
    public DimensionDividerMinX(float divider, Node left, Node right) {
      super(divider, left, right);
    }

    @Override
    Set<Volume> findVolumes(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, boolean maxInclusive) {
      if (maxX < divider || (maxInclusive && maxX == divider)) {
        return left.findVolumes(minX, minY, minZ, maxX, maxY, maxZ, maxInclusive);
      } else {
        return right.findVolumes(minX, minY, minZ, maxX, maxY, maxZ, maxInclusive);
      }
    }
  }

  /**
   * A dividing node which represents a division for
   * the maximum x value of {@link Cuboid}s.
   */
  protected static class DimensionDividerMaxX extends DimensionDivider {
    public DimensionDividerMaxX(float divider, Node left, Node right) {
      super(divider, left, right);
    }

    @Override
    Set<Volume> findVolumes(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, boolean maxInclusive) {
      if (minX <= divider) {
        return left.findVolumes(minX, minY, minZ, maxX, maxY, maxZ, maxInclusive);
      } else {
        return right.findVolumes(minX, minY, minZ, maxX, maxY, maxZ, maxInclusive);
      }
    }
  }

  /**
   * A dividing node which represents a division for
   * the minimum z value of {@link Cuboid}s.
   */
  protected static class DimensionDividerMinZ extends DimensionDivider {
    public DimensionDividerMinZ(float divider, Node left, Node right) {
      super(divider, left, right);
    }

    @Override
    Set<Volume> findVolumes(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, boolean maxInclusive) {
      if (maxZ < divider || (maxInclusive && maxZ == divider)) {
        return left.findVolumes(minX, minY, minZ, maxX, maxY, maxZ, maxInclusive);
      } else {
        return right.findVolumes(minX, minY, minZ, maxX, maxY, maxZ, maxInclusive);
      }
    }
  }

  /**
   * A dividing node which represents a division for
   * the maximum z value of {@link Cuboid}s.
   */
  protected static class DimensionDividerMaxZ extends DimensionDivider {
    public DimensionDividerMaxZ(float divider, Node left, Node right) {
      super(divider, left, right);
    }

    @Override
    Set<Volume> findVolumes(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, boolean maxInclusive) {
      if (minZ <= divider) {
        return left.findVolumes(minX, minY, minZ, maxX, maxY, maxZ, maxInclusive);
      } else {
        return right.findVolumes(minX, minY, minZ, maxX, maxY, maxZ, maxInclusive);
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
    Set<Volume> findVolumes(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, boolean maxInclusive) {
      return viable.stream().filter(volume -> {
        if (minX == maxX && minY == maxY && minZ == maxZ) {
          return volume.containsPoint(minX, minY, minZ);
        } else {
          return volume.containsCuboid(minX, minY, minZ, maxX, maxY, maxZ, maxInclusive);
        }
      }).collect(Collectors.toSet());
    }
  }

}
