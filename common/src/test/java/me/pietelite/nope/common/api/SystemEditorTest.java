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
import me.pietelite.nope.common.api.edit.TargetEditor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SystemEditorTest extends ApiTest {

  // TODO add tests validating every editor operation

  @Test
  void editHost_correctHost() {
    HostEditor hostEditor = service().editSystem().editHost(Nope.GLOBAL_ID);
    Assertions.assertEquals(hostEditor.name(), Nope.GLOBAL_ID);
    hostEditor = service().editSystem().editHost(MockNope.DOMAIN_1);
    Assertions.assertEquals(hostEditor.name(), MockNope.DOMAIN_1);
    Assertions.assertThrows(NoSuchElementException.class, () -> service().editSystem().editHost("die hard"));
  }

  @Test
  void editHost_correctHostWithWrongCase() {
    HostEditor hostEditor = service().editSystem().editHost(Nope.GLOBAL_ID.toUpperCase());
    Assertions.assertEquals(hostEditor.name(), Nope.GLOBAL_ID);
    hostEditor = service().editSystem().editHost(MockNope.DOMAIN_1.toUpperCase());
    Assertions.assertEquals(hostEditor.name(), MockNope.DOMAIN_1);
  }

  @Test
  void editGlobal_systemTest() {
    HostEditor hostEditor = service().editSystem().editGlobal();
    Assertions.assertEquals(hostEditor.name(), Nope.GLOBAL_ID);
    Assertions.assertEquals(1, hostEditor.profiles().size());
    Assertions.assertThrows(NoSuchElementException.class, () -> hostEditor.addProfile("banana", -1));
    service().editSystem().createProfile("banana");
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.addProfile("banana", -1));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.addProfile("banana", 2));
    // Cannot place a profile at index 0 for Global
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.addProfile("banana", 0));
    hostEditor.addProfile("banana", 1);
    Assertions.assertEquals(hostEditor.profiles().size(), 2);
    Assertions.assertEquals(hostEditor.profiles().get(0), Nope.GLOBAL_ID);
    Assertions.assertEquals(hostEditor.profiles().get(1), "banana");
    // Cannot place the same profile again
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.addProfile("banana", 2));
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.removeProfile(0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.removeProfile(Nope.GLOBAL_ID));
    hostEditor.removeProfile(1);
    hostEditor.addProfile("banana", 1);
    hostEditor.removeProfile("banana");
    hostEditor.addProfile("banana", 1);

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
    TargetEditor anotherBananaTargetEditor = hostEditor.editTarget("banana");
    anotherBananaTargetEditor.remove();
    Assertions.assertFalse(hostEditor.hasTarget(1));
  }

  @Test
  void domains() {
    Assertions.assertTrue(service().editSystem().domains().contains(MockNope.DOMAIN_1));
    Assertions.assertTrue(service().editSystem().domains().contains(MockNope.DOMAIN_2));
    Assertions.assertFalse(service().editSystem().domains().contains("narnia"));
  }

  @Test
  void editDomain() {
    Assertions.assertThrows(NoSuchElementException.class, () -> service().editSystem().editDomain("bikini-bottom"));
    Assertions.assertThrows(NoSuchElementException.class, () -> service().editSystem().editDomain(Nope.GLOBAL_ID));
    HostEditor hostEditor = service().editSystem().editDomain(MockNope.DOMAIN_1);
    Assertions.assertEquals(hostEditor.name(), MockNope.DOMAIN_1);
    editNonGlobalHost(hostEditor);
  }

  @Test
  void createScene() {
    service().editSystem().createScene("sixth-sense", 0);

  }

  @Test
  void editScene() {
    Assertions.assertThrows(NoSuchElementException.class, () -> service().editSystem().editScene("pulp-fiction"));
    service().editSystem().createScene("pulp-fiction", 0);
    HostEditor hostEditor = service().editSystem().editScene("pulp-fiction");
    Assertions.assertEquals(hostEditor.name(), "pulp-fiction");
    editNonGlobalHost(hostEditor);

    service().editSystem().createScene("shutter-island", 0);
    service().editSystem().createProfile("shutter-island");
    service().editSystem().editScene("shutter-island").addProfile("shutter-island", 0);
    Assertions.assertEquals(service().editSystem().editScene("shutter-island").profiles().size(), 1);
  }

  void editNonGlobalHost(HostEditor hostEditor) {
    Assertions.assertEquals(hostEditor.profiles().size(), 0);
    Assertions.assertThrows(NoSuchElementException.class, () -> hostEditor.addProfile("banana", -1));
    service().editSystem().createProfile("banana");
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.addProfile("banana", -1));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.addProfile("banana", 1));
    hostEditor.addProfile("banana", 0);
    Assertions.assertEquals(hostEditor.profiles().size(), 1);
    Assertions.assertEquals(hostEditor.profiles().get(0), "banana");
    // Cannot place the same profile again
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.addProfile("banana", 1));
    hostEditor.removeProfile(0);
    hostEditor.addProfile("banana", 0);
    hostEditor.removeProfile("banana");
    Assertions.assertEquals(hostEditor.profiles().size(), 0);
    hostEditor.addProfile("banana", 0);

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
    TargetEditor anotherBananaTargetEditor = hostEditor.editTarget("banana");
    anotherBananaTargetEditor.remove();
    Assertions.assertFalse(hostEditor.hasTarget(0));
  }

  @Test
  void addTwoProfilesWithSameName() {
    service().editSystem().createProfile("lollipop");
    Assertions.assertThrows(IllegalArgumentException.class, () -> service().editSystem().createProfile("lollipop"));
    Assertions.assertThrows(IllegalArgumentException.class, () -> service().editSystem().createProfile("LolliPop"));
  }

  @Test
  void addTwoProfilesWithSameNameDifferentCase() {
    service().editSystem().createProfile("watermelon");
    Assertions.assertThrows(IllegalArgumentException.class, () -> service().editSystem().createProfile("watermelon"));
    Assertions.assertThrows(IllegalArgumentException.class, () -> service().editSystem().createProfile("wAterMeloN"));
  }

}
