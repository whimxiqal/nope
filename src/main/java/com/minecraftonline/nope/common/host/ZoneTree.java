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

package com.minecraftonline.nope.common.host;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minecraftonline.nope.common.struct.Cuboid;
import com.minecraftonline.nope.common.struct.Volume;
import java.util.Collection;
import java.util.Comparator;
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
import org.jetbrains.annotations.Nullable;

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
public class ZoneTree {

  private final Map<String, Zone> zones = Maps.newHashMap();
  private final Map<Volume, Zone> volumes = Maps.newHashMap();
  protected Node root = null;
  @Getter
  protected int height = 0;
  @Getter
  protected int size = 0;

  public ZoneTree() {
    construct();
  }

  /**
   * Calculate all zones which have any intersecting volumes
   * with this one. The discrimination flag determines which
   * approximate cuboid structure to use for non-cuboid volumes.
   * Discriminate means that every single volume returned must be
   * intersecting, but some ones that intersect will be missed.
   * Indiscriminate means that all volumes that can possibly intersect
   * will be returned, but not all of those returned actually do intersect.
   *
   * @param zone         the zone to check for intersection
   * @param discriminate true to be discriminate, false to be indiscriminate
   * @return all intersecting zones
   */
  public Set<Zone> intersecting(Zone zone, boolean discriminate) {
    Set<Zone> all = new HashSet<>();
    for (Volume volume : zone.volumes) {
      all.addAll(intersecting(volume, discriminate));
    }
    return all;
  }

  /**
   * Calculate all zones which have any intersecting volumes
   * with this volume. The discrimination flag determines which
   * approximate cuboid structure to use for non-cuboid volumes.
   * Discriminate means that every single volume returned must be
   * intersecting, but some ones that intersect will be missed.
   * Indiscriminate means that all volumes that can possibly intersect
   * will be returned, but not all of those returned actually do intersect.
   *
   * @param volume       the volume to check for intersection
   * @param discriminate true to be discriminate, false to be indiscriminate
   * @return all intersecting zones
   */
  public Set<Zone> intersecting(Volume volume, boolean discriminate) {
    Set<Zone> all = new HashSet<>();
    Cuboid approximation = discriminate ? volume.inscribed() : volume.circumscribed();
    int[] listX = new int[]{approximation.minX(), approximation.maxX()};
    int[] listY = new int[]{approximation.minY(), approximation.maxY()};
    int[] listZ = new int[]{approximation.minZ(), approximation.maxZ()};
    for (int x = 0; x < 2; x++) {
      for (int y = 0; y < 2; y++) {
        for (int z = 0; z < 2; z++) {
          all.addAll(containing(listX[x], listY[y], listZ[z]));
        }
      }
    }
    return all;
  }

  public Set<Zone> containing(Zone zone, boolean discriminate) {
    Set<Zone> all = new HashSet<>();
    boolean first = true;
    // Only keep the zones which contain every single volume of the given zone
    for (Volume volume : zone.volumes) {
      if (first) {
        all.addAll(containing(volume, discriminate));
        first = false;
      } else {
        all.retainAll(containing(volume, discriminate));
      }
    }
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
      throw new IllegalStateException("Root of VolumeTree is not initialized");
    }
    return root.findVolumes(x, y, z).stream().map(volumes::get).collect(Collectors.toSet());
  }

  public Zone add(Zone zone) {
    Zone replaced = zones.put(zone.name(), zone);
    zone.volumes.forEach(volume -> volumes.put(volume, zone));
    construct();
    return replaced;
  }

  /**
   * Much faster way to add many volumes than using
   * {@link ZoneTree#add(Zone)}.
   *
   * @param zones the keys and volumes
   */
  public void addAll(Collection<Zone> zones) {
    zones.forEach(zone -> this.zones.put(zone.name(), zone));
    zones.forEach(zone -> zone.volumes.forEach(volume -> volumes.put(volume, zone)));
    construct();
  }

  @Nullable
  public Zone remove(String zoneName) {
    Zone removed = zones.remove(zoneName);
    if (removed != null) {
      removed.volumes.forEach(volumes::remove);
      construct();
    }
    removed.delete();
    return removed;
  }

  @Nullable
  public Zone get(String zoneName) {
    return zones.get(zoneName);
  }

  public boolean hasName(String zoneName) {
    return zones.containsKey(zoneName);
  }

  public Collection<Zone> getAll() {
    return zones.values();
  }

  public int size() {
    return size;
  }

  public void construct() {
    List<Volume> volumes = new LinkedList<>();
    zones.values().forEach(zone -> volumes.addAll(zone.volumes));
    root = construct(Dimension.X, Comparison.MIN, volumes, 0);
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
      return new ViabilityLeaf(Sets.newHashSet(subset));
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
        rightKeys = Lists.newLinkedList(subset);
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
        leftKeys = Lists.newLinkedList(subset);
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
        rightKeys = Lists.newLinkedList(subset);
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
        leftKeys = Lists.newLinkedList(subset);
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
    if (node instanceof ZoneTree.DimensionDivider) {
      return 1 + Math.max(
          calculateHeight(((DimensionDivider) node).left),
          calculateHeight(((DimensionDivider) node).right));
    }
    return 0;
  }

  protected int calculateSize(Node node) {
    if (node instanceof ZoneTree.DimensionDivider) {
      return 1 + calculateSize(((DimensionDivider) node).left)
          + calculateSize(((DimensionDivider) node).right);
    }
    return 0;
  }

  enum Dimension {
    X, Z
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
      return Sets.newHashSet();
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

    final Set<Volume> viable = Sets.newHashSet();

    private ViabilityLeaf(Set<Volume> viable) {
      this.viable.addAll(viable);
    }

    @Override
    Set<Volume> findVolumes(int x, int y, int z) {
      return viable.stream().filter(volume -> volume.contains(x, y, z)).collect(Collectors.toSet());
    }
  }

}
