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
import me.pietelite.nope.common.api.edit.ScopeEditor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SystemEditorTest extends ApiTest {

  @Test
  void editGlobal() {
    HostEditor hostEditor = service().editSystem().editGlobal();
    Assertions.assertEquals(hostEditor.name(), Nope.GLOBAL_ID);
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
  }

  @Test
  void editScope() {
    Assertions.assertThrows(NoSuchElementException.class, () -> service().editSystem().editScope("league-of-legends"));
    ScopeEditor editor = service().editSystem().editScope(Nope.NOPE_SCOPE);
    ScopeEditor uppercaseEditor = service().editSystem().editScope(Nope.NOPE_SCOPE.toUpperCase());
    Assertions.assertEquals(editor.name(), uppercaseEditor.name());
  }

  @Test
  void createScope() {
    Assertions.assertEquals(1, service().editSystem().scopes().size());
    Assertions.assertTrue(service().editSystem().scopes().contains(Nope.NOPE_SCOPE));
    Assertions.assertThrows(NoSuchElementException.class, () -> service().editSystem().editScope("rocket-league"));
    service().editSystem().registerScope("rocket-league");
    Assertions.assertEquals(2, service().editSystem().scopes().size());
    Assertions.assertTrue(service().editSystem().scopes().contains(Nope.NOPE_SCOPE));
    Assertions.assertTrue(service().editSystem().scopes().contains("rocket-league"));
    Assertions.assertDoesNotThrow(() -> service().editSystem().editScope("rocket-league"));
  }

}
