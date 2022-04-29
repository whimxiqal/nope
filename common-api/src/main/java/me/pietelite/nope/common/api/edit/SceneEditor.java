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

public interface SceneEditor extends HostEditor {

  Alteration name(String name);

  Alteration addCuboid(String domain, float x1, float y1, float z1, float x2, float y2, float z2);

  Alteration addCylinder(String domain, float x, float y, float z, float radius, float height);

  Alteration addSlab(String domain, float y, float height);

  Alteration addSphere(String domain, float x, float y, float z, float radius);

  int priority();

  Alteration priority(int priority);

  List<ZoneType> zoneTypes();

  CuboidEditor editCuboid(int index) throws IllegalArgumentException;

  CylinderEditor editCylinder(int index) throws IllegalArgumentException;

  SlabEditor editSlab(int index) throws IllegalArgumentException;

  SphereEditor editSphere(int index) throws IllegalArgumentException;

  Alteration destroy() throws NoSuchElementException;

}
