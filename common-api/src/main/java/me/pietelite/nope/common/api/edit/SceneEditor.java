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

package me.pietelite.nope.common.api.edit;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * A {@link HostEditor} specifically for scenes.
 * Scenes are hosts that encapsulate the space within a collection of volumes,
 * called zones. These zones are three-dimensional geometric objects.
 */
public interface SceneEditor extends HostEditor {

  /**
   * Set the name of the scene.
   *
   * @param name the name
   * @return false if the name was already the input name
   * @throws IllegalArgumentException if the name is illegal
   */
  boolean name(String name) throws IllegalArgumentException;

  /**
   * Add a cuboid (box) zone to this scene.
   *
   * @param domain the name of the domain
   * @param x1     one side of the x-axis range
   * @param y1     one side of the y-axis range
   * @param z1     one side of the z-axis range
   * @param x2     another side of the x-axis range
   * @param y2     another side of the y-axis range
   * @param z2     another side of the z-axis range
   * @throws NoSuchElementException if the domain does not exist
   */
  void addCuboid(String domain, float x1, float y1, float z1, float x2, float y2, float z2)
      throws NoSuchElementException;

  /**
   * Add a cylinder zone to this scene.
   *
   * @param domain the name of the domain
   * @param x      the x coordinate of the center of the cylinder
   * @param y      the y coordinate of the center, bottom side of the cylinder
   * @param z      the z coordinate of the center of the cylinder
   * @param radius the radius of the cylinder
   * @param height the height of the cylinder
   * @throws NoSuchElementException if the domain does not exist
   */
  void addCylinder(String domain, float x, float y, float z, float radius, float height)
      throws NoSuchElementException;

  /**
   * Add a slab zone to this scene. A slab is bounded in the y dimension but is unbounded
   * in the x and z dimensions.
   *
   * @param domain the name of the domain
   * @param y      the y value of the lower boundary of the slab
   * @param height the height of the slab
   * @throws NoSuchElementException if the domain does not exist
   */
  void addSlab(String domain, float y, float height) throws NoSuchElementException;

  /**
   * Add a sphere to this scene.
   *
   * @param domain the name of the domain
   * @param x      the x coordinate of the center of the sphere
   * @param y      the y coordinate of the center of the sphere
   * @param z      the z coordinate of the center of the sphere
   * @param radius the radius of the sphere
   * @throws NoSuchElementException if the domain does not exist
   */
  void addSphere(String domain, float x, float y, float z, float radius) throws NoSuchElementException;

  /**
   * Set the priority of this scene. In the event that a setting is evaluated and multiple hosts
   * encapsulate the location at which the setting is evaluated, higher priority hosts are evaluated
   * last. This means that the setting's value is the last one to affect the evaluation.
   *
   * <p>No two overlapping hosts may have the same priority. Otherwise, two setting values may have equal
   * priority, and then we wouldn't know which to evaluate ahead of the other. So, this method may change
   * other scenes' priorities as well, but the ordering of priority will never change. So, this is effectively
   * a cosmetic change to those scenes.
   *
   * @param priority the new priority
   * @return the number of scenes whose priority changed as a result of this priority changing
   */
  int priority(int priority);

  /**
   * Gets a list of every volume type in order of the volume list.
   *
   * @return the list of zone types
   */
  List<ZoneType> zoneTypes();

  /**
   * Gets an editor for the zone at the specified index.
   *
   * @param index the index of the volume to edit
   * @return the editor
   * @throws IndexOutOfBoundsException if the index is out of bounds
   */
  ZoneEditor editZone(int index) throws IndexOutOfBoundsException;

  /**
   * Gets a {@link ZoneEditor} specifically for a cuboid (box).
   *
   * @param index the index of the cuboid to edit
   * @return the editor
   * @throws IndexOutOfBoundsException if the index is out of bounds
   * @throws IllegalArgumentException  if the volume at the given index is not a cuboid (box)
   */
  CuboidEditor editCuboid(int index) throws IndexOutOfBoundsException, IllegalArgumentException;

  /**
   * Gets a {@link ZoneEditor} specifically for a cylinder.
   *
   * @param index the index of the cylinder to edit
   * @return the editor
   * @throws IndexOutOfBoundsException if the index is out of bounds
   * @throws IllegalArgumentException  if the volume at the given index is not a cylinder
   */
  CylinderEditor editCylinder(int index) throws IndexOutOfBoundsException, IllegalArgumentException;

  /**
   * Gets a {@link ZoneEditor} specifically for a slab.
   *
   * @param index the index of the slab to edit
   * @return the editor
   * @throws IndexOutOfBoundsException if the index is out of bounds
   * @throws IllegalArgumentException  if the volume at the given index is not a slab
   */
  SlabEditor editSlab(int index) throws IndexOutOfBoundsException, IllegalArgumentException;

  /**
   * Gets a {@link ZoneEditor} specifically for a sphere.
   *
   * @param index the index of the sphere to edit
   * @return the editor
   * @throws IndexOutOfBoundsException if the index is out of bounds
   * @throws IllegalArgumentException  if the volume at the given index is not a sphere
   */
  SphereEditor editSphere(int index) throws IndexOutOfBoundsException, IllegalArgumentException;

  /**
   * Destroy this scene.
   */
  void destroy();

}
