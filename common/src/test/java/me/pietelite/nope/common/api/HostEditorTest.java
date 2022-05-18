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

import java.util.NoSuchElementException;
import me.pietelite.nope.common.MockNope;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.HostEditor;
import me.pietelite.nope.common.api.edit.SceneEditor;
import me.pietelite.nope.common.api.edit.TargetEditor;
import me.pietelite.nope.common.api.edit.ZoneType;
import me.pietelite.nope.common.util.ApiUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HostEditorTest extends ApiTest {

  @Test
  void editGlobal_generalHostOperations() {
    HostEditor hostEditor = service().editSystem().editGlobal();
    Assertions.assertEquals(hostEditor.name(), Nope.GLOBAL_ID);
    Assertions.assertEquals(1, hostEditor.profiles().size());
    Assertions.assertThrows(NoSuchElementException.class, () -> hostEditor.addProfile(Nope.NOPE_SCOPE, "banana", -1));
    service().editSystem().editScope(Nope.NOPE_SCOPE).createProfile("banana");
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.addProfile(Nope.NOPE_SCOPE, "banana", -1));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.addProfile(Nope.NOPE_SCOPE, "banana", 2));
    // Cannot place a profile at index 0 for Global
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.addProfile(Nope.NOPE_SCOPE, "banana", 0));
    hostEditor.addProfile(Nope.NOPE_SCOPE, "banana", 1);
    Assertions.assertEquals(2, hostEditor.profiles().get(Nope.NOPE_SCOPE).size());
    Assertions.assertEquals(Nope.GLOBAL_ID, hostEditor.profiles().get(Nope.NOPE_SCOPE).get(0));
    Assertions.assertEquals("banana", hostEditor.profiles().get(Nope.NOPE_SCOPE).get(1));
    // Cannot place the same profile again
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.addProfile(Nope.NOPE_SCOPE, "banana", 2));
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.removeProfile(0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.removeProfile(Nope.NOPE_SCOPE, Nope.GLOBAL_ID));
    hostEditor.removeProfile(1);
    hostEditor.addProfile(Nope.NOPE_SCOPE, "banana", 1);
    hostEditor.removeProfile(Nope.NOPE_SCOPE, "banana");
    hostEditor.addProfile(Nope.NOPE_SCOPE, "banana", 1);

    // Targets
    Assertions.assertFalse(hostEditor.hasTarget(0));
    Assertions.assertFalse(hostEditor.hasTarget(1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.editTarget(0));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.editTarget(-1));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.editTarget(2));
    TargetEditor bananaTargetEditor = hostEditor.editTarget(1);
    Assertions.assertFalse(hostEditor.hasTarget(1));
    bananaTargetEditor.targetAll();
    Assertions.assertTrue(hostEditor.hasTarget(1));
    TargetEditor anotherBananaTargetEditor = hostEditor.editTarget(Nope.NOPE_SCOPE, "banana");
    anotherBananaTargetEditor.remove();
    Assertions.assertFalse(hostEditor.hasTarget(1));
  }

  @Test
  void editDomain_generalHostOperations() {
    systemTestEditNonGlobalHost(service().editSystem().editDomain(MockNope.DOMAIN_1));
  }

  @Test
  void editScene_generalHostOperations() {
    systemTestEditNonGlobalHost(service().editSystem().editScope(Nope.NOPE_SCOPE).createScene("pulp-fiction", 0));
  }

  void systemTestEditNonGlobalHost(HostEditor hostEditor) {
    Assertions.assertEquals(hostEditor.profiles().size(), 0);
    Assertions.assertThrows(NoSuchElementException.class, () -> hostEditor.addProfile(Nope.NOPE_SCOPE, "banana", -1));
    service().editSystem().editScope(Nope.NOPE_SCOPE).createProfile("banana");
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.addProfile(Nope.NOPE_SCOPE, "banana", -1));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.addProfile(Nope.NOPE_SCOPE, "banana", 1));
    hostEditor.addProfile(Nope.NOPE_SCOPE, "banana", 0);
    Assertions.assertEquals(hostEditor.profiles().get(Nope.NOPE_SCOPE).size(), 1);
    Assertions.assertEquals(hostEditor.profiles().get(Nope.NOPE_SCOPE).get(0), "banana");
    // Cannot place the same profile again
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.addProfile(Nope.NOPE_SCOPE, "banana", 1));
    hostEditor.removeProfile(0);
    hostEditor.addProfile(Nope.NOPE_SCOPE, "banana", 0);
    hostEditor.removeProfile(Nope.NOPE_SCOPE, "banana");
    Assertions.assertEquals(hostEditor.profiles().size(), 0);
    hostEditor.addProfile(Nope.NOPE_SCOPE, "banana", 0);

    // Targets
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.hasTarget(-1));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.hasTarget(1));
    Assertions.assertFalse(hostEditor.hasTarget(0));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.editTarget(-1));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.editTarget(1));
    Assertions.assertFalse(hostEditor.hasTarget(0));
    TargetEditor bananaTargetEditor = hostEditor.editTarget(0);
    Assertions.assertFalse(hostEditor.hasTarget(0));
    bananaTargetEditor.targetAll();
    Assertions.assertTrue(hostEditor.hasTarget(0));
    TargetEditor anotherBananaTargetEditor = hostEditor.editTarget(Nope.NOPE_SCOPE, "banana");
    anotherBananaTargetEditor.remove();
    Assertions.assertFalse(hostEditor.hasTarget(0));
  }

  @Test
  void editScene_name() {
    SceneEditor editor = service().editSystem().editScope(Nope.NOPE_SCOPE).createScene("inside-out", 0);
    Assertions.assertEquals("inside-out", editor.name());
    Assertions.assertTrue(editor.name("outside-in"));
    Assertions.assertEquals("outside-in", editor.name());
    // Should also be able to change case
    Assertions.assertTrue(editor.name("Outside-In"));
    Assertions.assertEquals("Outside-In", editor.name());

    // Can't change name to an existing one
    service().editSystem().editScope(Nope.NOPE_SCOPE).createScene("up", 0);
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.name("up"));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.name("UP"));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.name(Nope.GLOBAL_ID));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.name(MockNope.DOMAIN_1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.name(MockNope.DOMAIN_2));
  }

  @Test
  void editScene_addZones() {
    service().editSystem().editScope(Nope.NOPE_SCOPE).createScene("free_willy", 0);
    SceneEditor editor = service().editSystem().editScope(Nope.NOPE_SCOPE).editScene("free_willy");
    addVolumes(editor);
    Assertions.assertEquals(4, editor.zoneTypes().size());

    // Invalid volumes
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.addCuboid("wonderland", 0, 0, 0, 1, 1, 1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.addCuboid(MockNope.DOMAIN_1, 0, 0, 0, 0, 0, 0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.addCuboid(MockNope.DOMAIN_1, 0, 0, 0, 0, 1, 1));
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.addCylinder("wonderland", 0, 0, 0, 1, 1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.addCylinder(MockNope.DOMAIN_1, 0, 0, 0, 0, 1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.addCylinder(MockNope.DOMAIN_1, 0, 0, 0, -1, 1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.addCylinder(MockNope.DOMAIN_1, 0, 0, 0, 1, 0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.addCylinder(MockNope.DOMAIN_1, 0, 0, 0, 1, -1));
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.addSlab("wonderland", 0, 1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.addSlab(MockNope.DOMAIN_1, 0, 0));
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.addSphere("wonderland", 0, 0, 0, 1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.addSphere(MockNope.DOMAIN_1, 0, 0, 0, 0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.addSphere(MockNope.DOMAIN_1, 0, 0, 0, -1));
  }

  @Test
  void editScene_tryEditWrongZoneType() {
    SceneEditor editor = nopeScopeEditor().createScene("borne-identity", 0);
    editor.addCuboid(MockNope.DOMAIN_1, 1, 2, 3, 4, 5, 6);
    Assertions.assertEquals(1, editor.zoneTypes().size());
    editor.addCylinder(MockNope.DOMAIN_1, 1, 2, 3, 4, 5);
    Assertions.assertEquals(2, editor.zoneTypes().size());
    Assertions.assertEquals(ZoneType.CUBOID, editor.zoneTypes().get(0));
    Assertions.assertEquals(ZoneType.CYLINDER, editor.zoneTypes().get(1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.editCylinder(0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.editSlab(0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.editSphere(0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.editCuboid(1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.editSlab(1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.editSphere(1));
  }

  @Test
  void editScene_priority() {
    SceneEditor editor = service().editSystem().editScope(Nope.NOPE_SCOPE).createScene("toy-story", 0);
    Assertions.assertEquals(0, editor.priority());
    editor.priority(10);
    Assertions.assertEquals(10, editor.priority());

    addVolumes(editor);
    SceneEditor editor2 = service().editSystem().editScope(Nope.NOPE_SCOPE).createScene("toy-story-2", 10);
    Assertions.assertEquals(10, editor2.priority());
    addVolumes(editor2);
    Assertions.assertEquals(10, editor2.priority());
    // The volumes are the same for the second scene, so when the volumes are added, it pushes up the priority
    // of intersecting scenes
    Assertions.assertEquals(11, editor.priority());

    // Cannot have a negative priority
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.priority(-1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.priority(-10));
  }

  void addVolumes(SceneEditor editor) {
    editor.addCuboid(MockNope.DOMAIN_1, 10, 20, 30, 40, 50, 60);
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.addCuboid("hogwarts", 10, 20, 30, 40, 50, 60));
    editor.addCylinder(MockNope.DOMAIN_1, 10, 20, 30, 40, 50);
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.addCylinder("hogwarts", 10, 20, 30, 40, 50));
    editor.addSlab(MockNope.DOMAIN_1, 10, 20);
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.addCylinder("hogwarts", 10, 20, 30, 40, 50));
    editor.addSphere(MockNope.DOMAIN_1, 10, 20, 30, 40);
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.addCylinder("hogwarts", 10, 20, 30, 40, 50));
  }

  @Test
  void editScene_destroy() {
    SceneEditor editor = service().editSystem().editScope(Nope.NOPE_SCOPE).createScene("unusual-suspects", 22);
    Assertions.assertEquals(1, ApiUtil.editNopeScope().scenes().size());
    editor.destroy();
    Assertions.assertTrue(service().editSystem().editScope(Nope.NOPE_SCOPE).scenes().isEmpty());
  }

}
