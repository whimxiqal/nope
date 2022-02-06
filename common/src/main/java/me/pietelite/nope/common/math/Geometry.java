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

package me.pietelite.nope.common.math;

public final class Geometry {
  private Geometry() {
  }

  public static String typeOf(Volume volume) {
    if (volume instanceof Cuboid) {
      return "box";
    } else if (volume instanceof Cylinder) {
      return "cylinder";
    } else if (volume instanceof Sphere) {
      return "sphere";
    } else if (volume instanceof Slab) {
      return "slab";
    } else {
      throw new IllegalArgumentException("The volume type is unknown: " + volume.getClass().getName());
    }
  }

  public static boolean intersects(Volume v1, Volume v2) {
    if (v1 instanceof Cuboid) {
      if (v2 instanceof Cuboid) {
        return touches((Cuboid) v1, (Cuboid) v2);
      } else if (v2 instanceof Cylinder) {
        return touches((Cuboid) v1, (Cylinder) v2);
      } else if (v2 instanceof Sphere) {
        return touches((Cuboid) v1, (Sphere) v2);
      } else if (v2 instanceof Slab) {
        return touches((Cuboid) v1, (Slab) v2);
      } else {
        throw new IllegalArgumentException("Volume type is not recognized: " + v2.getClass().getName());
      }
    } else if (v1 instanceof Cylinder) {
      if (v2 instanceof Cuboid) {
        return touches((Cuboid) v2, (Cylinder) v1);
      } else if (v2 instanceof Cylinder) {
        return touches((Cylinder) v1, (Cylinder) v2);
      } else if (v2 instanceof Sphere) {
        return touches((Cylinder) v1, (Sphere) v2);
      } else if (v2 instanceof Slab) {
        return touches((Cylinder) v1, (Slab) v2);
      } else {
        throw new IllegalArgumentException("Volume type is not recognized: " + v2.getClass().getName());
      }
    } else if (v1 instanceof Sphere) {
      if (v2 instanceof Cuboid) {
        return touches((Cuboid) v2, (Sphere) v1);
      } else if (v2 instanceof Cylinder) {
        return touches((Cylinder) v2, (Sphere) v1);
      } else if (v2 instanceof Sphere) {
        return touches((Sphere) v1, (Sphere) v2);
      } else if (v2 instanceof Slab) {
        return touches((Sphere) v1, (Slab) v2);
      } else {
        throw new IllegalArgumentException("Volume type is not recognized: " + v2.getClass().getName());
      }
    } else if (v1 instanceof Slab) {
      if (v2 instanceof Cuboid) {
        return touches((Cuboid) v2, (Slab) v1);
      } else if (v2 instanceof Cylinder) {
        return touches((Cylinder) v2, (Slab) v1);
      } else if (v2 instanceof Sphere) {
        return touches((Sphere) v2, (Slab) v1);
      } else if (v2 instanceof Slab) {
        return touches((Slab) v1, (Slab) v2);
      } else {
        throw new IllegalArgumentException("Volume type is not recognized: " + v2.getClass().getName());
      }
    } else {
      throw new IllegalArgumentException("Volume type is not recognized: " + v1.getClass().getName());
    }
  }

  private static boolean touches(Cuboid cuboid1, Cuboid cuboid2) {
    return cuboid1.intersects(cuboid2);
  }

  private static boolean touches(Cuboid cuboid, Cylinder cylinder) {
    if (cuboid.minY() < cylinder.maxY() && cuboid.maxY() > cylinder.minY()) {
      // Y dimensions intersect, good
      if (cuboid.minX() < cylinder.posX() && cuboid.maxX() > cylinder.posX()) {
        // X dimensions of cuboid surround x position of cylinder, so just check z dimension
        //  (see if z-axis normal faces touch)
        return cuboid.minZ() < cylinder.posZ() + cylinder.radius()
            && cuboid.maxZ() > cylinder.posZ() - cylinder.radius();
      } else if (cuboid.minZ() < cylinder.posZ() && cuboid.maxZ() > cylinder.posZ()) {
        // Z dimensions of cuboid surround z position of cylinder, so just check x dimension
        //  (see if z-axis normal faces touch)
        return cuboid.minX() < cylinder.posX() + cylinder.radius()
            && cuboid.maxX() > cylinder.posX() - cylinder.radius();
      } else {
        // See if corner of cuboid touches cylinder edge
        // TODO WRONG, instead check if any of the x,z coord pairs (4 of them) are inside radius
        return cuboid.midPointXZPlane().distanceSquared(cylinder.midPoint2d())
            < (cuboid.radiusXZPlane() + cylinder.radius())
            * (cuboid.radiusXZPlane() + cylinder.radius());
      }
    } else {
      return false;
    }
  }

  private static boolean touches(Cuboid cuboid, Sphere sphere) {
    boolean centeredX = cuboid.minX() < sphere.posX() && cuboid.maxX() > sphere.posX();
    boolean centeredY = cuboid.minY() < sphere.posY() && cuboid.maxY() > sphere.posY();
    boolean centeredZ = cuboid.minZ() < sphere.posZ() && cuboid.maxZ() > sphere.posZ();
    if (centeredX && centeredY && centeredZ) {
      // Centered around the origin of the sphere, so must intersect
      return true;
    } else if (centeredX && centeredY) {
      // Centered on X and Y dimensions (see if z-axis normal faces touch)
      return cuboid.minZ() < sphere.posZ() + sphere.radius()
          && cuboid.maxZ() > sphere.posZ() - sphere.radius();
    } else if (centeredX && centeredZ) {
      // Centered on X and Z dimensions (see if y-axis normal faces touch)
      return cuboid.minY() < sphere.posY() + sphere.radius()
          && cuboid.maxY() > sphere.posY() - sphere.radius();
    } else if (centeredY && centeredZ) {
      // Centered on Y and Z dimensions (see if x-axis normal faces touch)
      return cuboid.minX() < sphere.posX() + sphere.radius()
          && cuboid.maxX() > sphere.posX() - sphere.radius();
    } else if (centeredX) {
      // Check if any edge pointing in x direction intersects
      return
          Vector2d.of(cuboid.minY(), cuboid.minZ()).distanceSquared(
              Vector2d.of(sphere.posY(), sphere.posZ())) < sphere.radiusSquared()
              || Vector2d.of(cuboid.minY(), cuboid.maxZ()).distanceSquared(
              Vector2d.of(sphere.posY(), sphere.posZ())) < sphere.radiusSquared()
              || Vector2d.of(cuboid.maxY(), cuboid.minZ()).distanceSquared(
              Vector2d.of(sphere.posY(), sphere.posZ())) < sphere.radiusSquared()
              || Vector2d.of(cuboid.maxY(), cuboid.maxZ()).distanceSquared(
              Vector2d.of(sphere.posY(), sphere.posZ())) < sphere.radiusSquared();
    } else if (centeredY) {
      // Check if any edge pointing in y direction intersects
      return
          Vector2d.of(cuboid.minX(), cuboid.minZ()).distanceSquared(
              Vector2d.of(sphere.posX(), sphere.posZ())) < sphere.radiusSquared()
              || Vector2d.of(cuboid.minX(), cuboid.maxZ()).distanceSquared(
              Vector2d.of(sphere.posX(), sphere.posZ())) < sphere.radiusSquared()
              || Vector2d.of(cuboid.maxX(), cuboid.minZ()).distanceSquared(
              Vector2d.of(sphere.posX(), sphere.posZ())) < sphere.radiusSquared()
              || Vector2d.of(cuboid.maxX(), cuboid.maxZ()).distanceSquared(
              Vector2d.of(sphere.posX(), sphere.posZ())) < sphere.radiusSquared();
    } else if (centeredZ) {
      // Check if any edge pointing in z direction intersects
      return
          Vector2d.of(cuboid.minX(), cuboid.minY()).distanceSquared(
              Vector2d.of(sphere.posX(), sphere.posY())) < sphere.radiusSquared()
              || Vector2d.of(cuboid.minX(), cuboid.maxY()).distanceSquared(
              Vector2d.of(sphere.posX(), sphere.posY())) < sphere.radiusSquared()
              || Vector2d.of(cuboid.maxX(), cuboid.minY()).distanceSquared(
              Vector2d.of(sphere.posX(), sphere.posY())) < sphere.radiusSquared()
              || Vector2d.of(cuboid.maxX(), cuboid.maxY()).distanceSquared(
              Vector2d.of(sphere.posX(), sphere.posY())) < sphere.radiusSquared();
    } else {
      return
          Vector3d.of(cuboid.minX(), cuboid.minY(), cuboid.minZ())
              .distanceSquared(sphere.midPoint3d()) < sphere.radiusSquared()
              || Vector3d.of(cuboid.minX(), cuboid.minY(), cuboid.maxZ())
              .distanceSquared(sphere.midPoint3d()) < sphere.radiusSquared()
              || Vector3d.of(cuboid.minX(), cuboid.maxY(), cuboid.minZ())
              .distanceSquared(sphere.midPoint3d()) < sphere.radiusSquared()
              || Vector3d.of(cuboid.minX(), cuboid.maxY(), cuboid.maxZ())
              .distanceSquared(sphere.midPoint3d()) < sphere.radiusSquared()
              || Vector3d.of(cuboid.maxX(), cuboid.minY(), cuboid.minZ())
              .distanceSquared(sphere.midPoint3d()) < sphere.radiusSquared()
              || Vector3d.of(cuboid.maxX(), cuboid.minY(), cuboid.maxZ())
              .distanceSquared(sphere.midPoint3d()) < sphere.radiusSquared()
              || Vector3d.of(cuboid.maxX(), cuboid.maxY(), cuboid.minZ())
              .distanceSquared(sphere.midPoint3d()) < sphere.radiusSquared()
              || Vector3d.of(cuboid.maxX(), cuboid.maxY(), cuboid.maxZ())
              .distanceSquared(sphere.midPoint3d()) < sphere.radiusSquared();
    }
  }

  private static boolean touches(Cuboid cuboid, Slab slab) {
    return cuboid.maxY() > slab.minY() && cuboid.minY() < slab.maxY();
  }

  private static boolean touches(Cylinder cylinder1, Cylinder cylinder2) {
    return cylinder1.maxY() > cylinder2.minY() && cylinder1.minY() < cylinder2.maxY()
        && cylinder1.midPoint2d().distanceSquared(cylinder2.midPoint2d()) < (cylinder1.radius() + cylinder2.radius()) * (cylinder1.radius() + cylinder2.radius());
  }

  private static boolean touches(Cylinder cylinder, Sphere sphere) {
    // Three cases: cylinder's side touches equator of sphere (compare radii),
    //  cylinder's top or bottom coincide with sphere's 2d location (compare y dimensions),
    //  or the hardest one, which is when the edge of the top or bottom touch an arbitrary latitude
    if ((cylinder.radius() + sphere.radius()) * (cylinder.radius() + sphere.radius()) < cylinder.midPoint2d().distanceSquared(sphere.midPoint2d())) {
      // could intersect
      if (cylinder.minY() < sphere.posY() && cylinder.maxY() > sphere.posY()) {
        return true;
      } else {
        if (cylinder.minY() < sphere.posY() + sphere.radius() && cylinder.maxY() > sphere.posY() - sphere.radius()) {
          if (cylinder.midPoint2d().distanceSquared(sphere.midPoint2d()) < cylinder.radiusSquared()) {
            // Cylinder's top or bottom coincide with one of the sphere's poles
            return true;
          } else {
            if (cylinder.minY() > sphere.posY()) {
              // Get point on bottom edge that would intersect
              return cylinder.midPoint3dBottom()
                  .plus(sphere.midPoint2d()
                      .minus(cylinder.midPoint2d())
                      .normalize()
                      .times(cylinder.radius()))
                  .distanceSquared(sphere.midPoint3d()) < sphere.radiusSquared();
            } else {
              // cylinder.maxY() must be < sphere.posY()
              // Get point on top edge that would intersect
              return cylinder.midPoint3dTop()
                  .plus(sphere.midPoint2d()
                      .minus(cylinder.midPoint2d())
                      .normalize()
                      .times(cylinder.radius()))
                  .distanceSquared(sphere.midPoint3d()) < sphere.radiusSquared();
            }
          }
        }
      }
    }
    return false;
  }

  private static boolean touches(Cylinder cylinder, Slab slab) {
    return cylinder.maxY() > slab.minY() && cylinder.minY() < slab.maxY();
  }

  private static boolean touches(Sphere sphere1, Sphere sphere2) {
    return sphere1.midPoint3d().distanceSquared(sphere2.midPoint3d()) < (sphere1.radius() + sphere2.radius()) * (sphere1.radius() + sphere2.radius());
  }

  private static boolean touches(Sphere sphere, Slab slab) {
    return sphere.posY() + sphere.radius() > slab.minY() && sphere.posY() - sphere.radius() < slab.maxY();
  }

  private static boolean touches(Slab slab1, Slab slab2) {
    return slab1.maxY() > slab2.minY() && slab1.minY() < slab2.maxY();
  }

}
