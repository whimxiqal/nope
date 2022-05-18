package me.pietelite.nope.common.api;

import me.pietelite.nope.common.MockNope;
import me.pietelite.nope.common.Nope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScopeEditorTest extends ApiTest {

  @Test
  void scenes() {
    Assertions.assertTrue(nopeScopeEditor().scenes().isEmpty());
    nopeScopeEditor().createScene("kung_fu_panda", 0);
    Assertions.assertEquals(1, nopeScopeEditor().scenes().size());
    Assertions.assertTrue(nopeScopeEditor().scenes().contains("kung_fu_panda"));
    // TODO this should eventually work i.e. we should get a set from the API that returns true if it contains
    //  the name regardless of case
    // Assertions.assertTrue(service().editSystem().scenes().contains("Kung_Fu_Panda"));
  }

  @Test
  void createScene() {
    nopeScopeEditor().createScene("sixth-sense", 0);
    Assertions.assertThrows(IllegalArgumentException.class, () -> nopeScopeEditor().createScene("sixth-sense", 0));
    // different case still counts as the same name
    Assertions.assertThrows(IllegalArgumentException.class, () -> nopeScopeEditor().createScene("Sixth-Sense", 0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> nopeScopeEditor().createScene(Nope.GLOBAL_ID, 0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> nopeScopeEditor().createScene(MockNope.DOMAIN_1, 0));
  }

  @Test
  void createScene_invalidName() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> nopeScopeEditor().createScene("_blades-of-glory", 0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> nopeScopeEditor().createScene("blades-of-glory_", 0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> nopeScopeEditor().createScene("blades-of-glory_", 0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> nopeScopeEditor().createScene("amper&sand", 0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> nopeScopeEditor().createScene("aster*isk", 0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> nopeScopeEditor().createScene("sla/sh", 0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> nopeScopeEditor().createScene("other\\slash", 0));
  }

  @Test
  void profiles() {
    Assertions.assertEquals(1, nopeScopeEditor().profiles().size());
    Assertions.assertTrue(nopeScopeEditor().profiles().contains(Nope.GLOBAL_ID));
    nopeScopeEditor().createProfile("beetlejuice");
    Assertions.assertEquals(2, nopeScopeEditor().profiles().size());
    Assertions.assertTrue(nopeScopeEditor().profiles().contains("beetlejuice"));
    // TODO this should eventually work i.e. we should get a set from the API that returns true if it contains
    //  the name regardless of case
    // Assertions.assertTrue(service().editSystem().profiles().contains("BeetleJuice"));
  }

}
