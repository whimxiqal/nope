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

package me.pietelite.nope.common.api;

import me.pietelite.nope.common.MockNope;
import me.pietelite.nope.common.api.edit.CuboidEditor;
import me.pietelite.nope.common.api.edit.CylinderEditor;
import me.pietelite.nope.common.api.edit.SceneEditor;
import me.pietelite.nope.common.api.edit.SlabEditor;
import me.pietelite.nope.common.api.edit.SphereEditor;
import me.pietelite.nope.common.api.edit.ZoneEditor;
import me.pietelite.nope.common.api.edit.ZoneType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ZoneEditorTest extends ApiTest {

  @Test
  void editZone() {
    SceneEditor editor = nopeScopeEditor().createScene("the-avengers", 0);
    Assertions.assertTrue(editor.zoneTypes().isEmpty());
    editor.addCuboid(MockNope.DOMAIN_1, 1, 2, 3, 4, 5, 6);
    Assertions.assertEquals(1, editor.zoneTypes().size());
    Assertions.assertEquals(ZoneType.CUBOID, editor.zoneTypes().get(0));

    ZoneEditor zoneEditor = editor.editZone(0);
    testZoneEditor(zoneEditor);
    destroyZone(zoneEditor);
  }

  @Test
  void editCuboid() {
    SceneEditor editor = nopeScopeEditor().createScene("the-avengers", 0);
    Assertions.assertTrue(editor.zoneTypes().isEmpty());
    CuboidEditor cuboidEditor = editor.addCuboid(MockNope.DOMAIN_1, 1, 2, 3, 4, 5, 6);
    Assertions.assertEquals(1, editor.zoneTypes().size());
    Assertions.assertEquals(ZoneType.CUBOID, editor.zoneTypes().get(0));

    testZoneEditor(cuboidEditor);
    Assertions.assertEquals(1, cuboidEditor.minX());
    Assertions.assertEquals(2, cuboidEditor.minY());
    Assertions.assertEquals(3, cuboidEditor.minZ());
    Assertions.assertEquals(4, cuboidEditor.maxX());
    Assertions.assertEquals(5, cuboidEditor.maxY());
    Assertions.assertEquals(6, cuboidEditor.maxZ());
    cuboidEditor.setDimensions(20, 19, 18, 17, 16, 15);
    Assertions.assertEquals(17, cuboidEditor.minX());
    Assertions.assertEquals(16, cuboidEditor.minY());
    Assertions.assertEquals(15, cuboidEditor.minZ());
    Assertions.assertEquals(20, cuboidEditor.maxX());
    Assertions.assertEquals(19, cuboidEditor.maxY());
    Assertions.assertEquals(18, cuboidEditor.maxZ());
    destroyZone(cuboidEditor);
  }

  @Test
  void editCylinder() {
    SceneEditor editor = nopeScopeEditor().createScene("the-avengers", 0);
    Assertions.assertTrue(editor.zoneTypes().isEmpty());
    CylinderEditor cuboidEditor = editor.addCylinder(MockNope.DOMAIN_1, 1, 2, 3, 4, 5);
    Assertions.assertEquals(1, editor.zoneTypes().size());
    Assertions.assertEquals(ZoneType.CYLINDER, editor.zoneTypes().get(0));

    testZoneEditor(cuboidEditor);
    Assertions.assertEquals(1, cuboidEditor.x());
    Assertions.assertEquals(2, cuboidEditor.y());
    Assertions.assertEquals(3, cuboidEditor.z());
    Assertions.assertEquals(4, cuboidEditor.radius());
    Assertions.assertEquals(5, cuboidEditor.height());
    cuboidEditor.setDimensions(20, 19, 18, 17, 16);
    Assertions.assertEquals(20, cuboidEditor.x());
    Assertions.assertEquals(19, cuboidEditor.y());
    Assertions.assertEquals(18, cuboidEditor.z());
    Assertions.assertEquals(17, cuboidEditor.radius());
    Assertions.assertEquals(16, cuboidEditor.height());
    Assertions.assertThrows(IllegalArgumentException.class, () -> cuboidEditor.setDimensions(20, 19, 18, 0, 1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> cuboidEditor.setDimensions(20, 19, 18, 1, 0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> cuboidEditor.setDimensions(20, 19, 18, -1, -1));
    destroyZone(cuboidEditor);
  }

  @Test
  void editSlab() {
    SceneEditor editor = nopeScopeEditor().createScene("the-avengers", 0);
    Assertions.assertTrue(editor.zoneTypes().isEmpty());
    SlabEditor slabEditor = editor.addSlab(MockNope.DOMAIN_1, 1, 2);
    Assertions.assertEquals(1, editor.zoneTypes().size());
    Assertions.assertEquals(ZoneType.SLAB, editor.zoneTypes().get(0));

    testZoneEditor(slabEditor);
    Assertions.assertEquals(1, slabEditor.y());
    Assertions.assertEquals(2, slabEditor.height());
    slabEditor.setDimensions(20, 19);
    Assertions.assertEquals(20, slabEditor.y());
    Assertions.assertEquals(19, slabEditor.height());
    destroyZone(slabEditor);
  }

  @Test
  void editSphere() {
    SceneEditor editor = nopeScopeEditor().createScene("the-avengers", 0);
    Assertions.assertTrue(editor.zoneTypes().isEmpty());
    SphereEditor sphereEditor = editor.addSphere(MockNope.DOMAIN_1, 1, 2, 3, 4);
    Assertions.assertEquals(1, editor.zoneTypes().size());
    Assertions.assertEquals(ZoneType.SPHERE, editor.zoneTypes().get(0));

    testZoneEditor(sphereEditor);
    Assertions.assertEquals(1, sphereEditor.x());
    Assertions.assertEquals(2, sphereEditor.y());
    Assertions.assertEquals(3, sphereEditor.z());
    Assertions.assertEquals(4, sphereEditor.radius());
    sphereEditor.setDimensions(20, 19, 18, 17);
    Assertions.assertEquals(20, sphereEditor.x());
    Assertions.assertEquals(19, sphereEditor.y());
    Assertions.assertEquals(18, sphereEditor.z());
    Assertions.assertEquals(17, sphereEditor.radius());
    destroyZone(sphereEditor);
  }

  void testZoneEditor(ZoneEditor zoneEditor) {
    Assertions.assertEquals(MockNope.DOMAIN_1, zoneEditor.domain());
    zoneEditor.domain(MockNope.DOMAIN_2);
    Assertions.assertEquals(MockNope.DOMAIN_2, zoneEditor.domain());
  }

  void destroyZone(ZoneEditor zoneEditor) {
    zoneEditor.destroy();
    // Cannot access the domain if the zone is destroyed
    Assertions.assertThrows(IllegalStateException.class, zoneEditor::domain);
  }

}
