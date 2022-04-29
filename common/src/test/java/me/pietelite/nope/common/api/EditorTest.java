package me.pietelite.nope.common.api;

import java.util.NoSuchElementException;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.HostEditor;
import me.pietelite.nope.common.api.edit.TargetEditor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EditorTest extends ApiTest {

  // TODO add tests validating every editor operation

  @Test
  void editGlobal() {
    HostEditor hostEditor = service.editSystem().editGlobal();
    Assertions.assertEquals(hostEditor.name(), Nope.GLOBAL_HOST_NAME);
    Assertions.assertEquals(1, hostEditor.profiles().size());
    Assertions.assertThrows(NoSuchElementException.class, () -> hostEditor.addProfile("banana", -1));
    assertSuccess(service.editSystem().createProfile("banana"));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.addProfile("banana", -1));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.addProfile("banana", 2));
    // Cannot place a profile at index 0 for Global
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.addProfile("banana", 0));
    assertSuccess(hostEditor.addProfile("banana", 1));
    Assertions.assertEquals(hostEditor.profiles().size(), 2);
    Assertions.assertEquals(hostEditor.profiles().get(0), Nope.GLOBAL_HOST_NAME);
    Assertions.assertEquals(hostEditor.profiles().get(1), "banana");
    // Cannot place the same profile again
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.addProfile("banana", 2));
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.removeProfile(0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.removeProfile(Nope.GLOBAL_HOST_NAME));
    assertSuccess(hostEditor.removeProfile(1));
    assertSuccess(hostEditor.addProfile("banana", 1));
    assertSuccess(hostEditor.removeProfile("banana"));
    assertSuccess(hostEditor.addProfile("banana", 1));

    // Targets
    Assertions.assertFalse(hostEditor.hasTarget(0));
    Assertions.assertFalse(hostEditor.hasTarget(1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.editTarget(0));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.editTarget(-1));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.editTarget(2));
    TargetEditor bananaTargetEditor = hostEditor.editTarget(1);
    Assertions.assertFalse(hostEditor.hasTarget(1));
    assertSuccess(bananaTargetEditor.targetAll());
    Assertions.assertTrue(hostEditor.hasTarget(1));
    TargetEditor anotherBananaTargetEditor = hostEditor.editTarget("banana");
    assertSuccess(anotherBananaTargetEditor.remove());
    Assertions.assertFalse(hostEditor.hasTarget(1));
  }

  @Test
  void domains() {
    Assertions.assertTrue(service.editSystem().domains().contains(D1));
    Assertions.assertTrue(service.editSystem().domains().contains(D2));
    Assertions.assertFalse(service.editSystem().domains().contains("narnia"));
  }

  @Test
  void editDomain() {
    Assertions.assertThrows(NoSuchElementException.class, () -> service.editSystem().editDomain("bikini-bottom"));
    HostEditor hostEditor = service.editSystem().editDomain(D1);
    Assertions.assertEquals(hostEditor.name(), D1);
    editNonGlobalHost(hostEditor);
  }

  @Test
  void createScene() {
    assertSuccess(service.editSystem().createScene("sixth-sense", 0));

  }

  @Test
  void editScene() {
    Assertions.assertThrows(NoSuchElementException.class, () -> service.editSystem().editScene("pulp-fiction"));
    assertSuccess(service.editSystem().createScene("pulp-fiction", 0));
    HostEditor hostEditor = service.editSystem().editScene("pulp-fiction");
    Assertions.assertEquals(hostEditor.name(), "pulp-fiction");
    editNonGlobalHost(hostEditor);

    assertSuccess(service.editSystem().createScene("shutter-island", 0));
    assertSuccess(service.editSystem().createProfile("shutter-island"));
    assertSuccess(service.editSystem().editScene("shutter-island").addProfile("shutter-island", 0));
    Assertions.assertEquals(service.editSystem().editScene("shutter-island").profiles().size(), 1);
  }

  void editNonGlobalHost(HostEditor hostEditor) {
    Assertions.assertEquals(hostEditor.profiles().size(), 0);
    Assertions.assertThrows(NoSuchElementException.class, () -> hostEditor.addProfile("banana", -1));
    assertSuccess(service.editSystem().createProfile("banana"));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.addProfile("banana", -1));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.addProfile("banana", 1));
    assertSuccess(hostEditor.addProfile("banana", 0));
    Assertions.assertEquals(hostEditor.profiles().size(), 1);
    Assertions.assertEquals(hostEditor.profiles().get(0), "banana");
    // Cannot place the same profile again
    Assertions.assertThrows(IllegalArgumentException.class, () -> hostEditor.addProfile("banana", 1));
    assertSuccess(hostEditor.removeProfile(0));
    assertSuccess(hostEditor.addProfile("banana", 0));
    assertSuccess(hostEditor.removeProfile("banana"));
    Assertions.assertEquals(hostEditor.profiles().size(), 0);
    assertSuccess(hostEditor.addProfile("banana", 0));

    // Targets
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.hasTarget(-1));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.hasTarget(1));
    Assertions.assertFalse(hostEditor.hasTarget(0));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.editTarget(-1));
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> hostEditor.editTarget(1));
    Assertions.assertFalse(hostEditor.hasTarget(0));
    TargetEditor bananaTargetEditor = hostEditor.editTarget(0);
    Assertions.assertFalse(hostEditor.hasTarget(0));
    assertSuccess(bananaTargetEditor.targetAll());
    Assertions.assertTrue(hostEditor.hasTarget(0));
    TargetEditor anotherBananaTargetEditor = hostEditor.editTarget("banana");
    assertSuccess(anotherBananaTargetEditor.remove());
    Assertions.assertFalse(hostEditor.hasTarget(0));
  }

  @Test
  void addTwoProfilesWithSameName() {
    assertSuccess(service.editSystem().createProfile("lollipop"));
    Assertions.assertThrows(IllegalArgumentException.class, () -> service.editSystem().createProfile("lollipop"));
    Assertions.assertThrows(IllegalArgumentException.class, () -> service.editSystem().createProfile("LolliPop"));
  }

  @Test
  void addTwoProfilesWithSameNameDifferentCase() {
    assertSuccess(service.editSystem().createProfile("watermelon"));
    Assertions.assertThrows(IllegalArgumentException.class, () -> service.editSystem().createProfile("watermelon"));
    Assertions.assertThrows(IllegalArgumentException.class, () -> service.editSystem().createProfile("wAterMeloN"));
  }

}
