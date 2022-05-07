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
import me.pietelite.nope.common.api.edit.ProfileEditor;
import me.pietelite.nope.common.api.register.data.BlockChange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProfileEditorTest extends ApiTest {

  @Test
  void name() {
    Assertions.assertEquals(nopeScopeEditor().profiles().size(), 1);
    Assertions.assertThrows(NoSuchElementException.class, () -> nopeScopeEditor().editProfile("monsters_inc"));
    Assertions.assertEquals(nopeScopeEditor().profiles().size(), 1);

    ProfileEditor profileEditor = nopeScopeEditor().createProfile("monsters_inc");
    Assertions.assertEquals(nopeScopeEditor().profiles().size(), 2);
    Assertions.assertEquals(profileEditor.name(), "monsters_inc");

    profileEditor.name("monsters_university");
    Assertions.assertEquals(nopeScopeEditor().profiles().size(), 2);
    Assertions.assertEquals(profileEditor.name(), "monsters_university");
    Assertions.assertThrows(NoSuchElementException.class, () -> nopeScopeEditor().editProfile("monsters_inc"));
    Assertions.assertDoesNotThrow(() -> nopeScopeEditor().editProfile("monsters_university"));
  }

  @Test
  void editSetting() {
    ProfileEditor profileEditor = nopeScopeEditor().createProfile("the_empire_strikes_back");
    Assertions.assertDoesNotThrow(() -> profileEditor.editSetting("sleep"));
    Assertions.assertDoesNotThrow(() -> profileEditor.editSingleValueSetting("sleep", Boolean.class));
    Assertions.assertDoesNotThrow(() -> profileEditor.editMultipleValueSetting("block-change", BlockChange.class));

    Assertions.assertThrows(NoSuchElementException.class, () -> profileEditor.editSetting("Luke---I-am-you-father"));
  }

  @Test
  void destroy() {
    ProfileEditor profileEditor = nopeScopeEditor().createProfile("moana");
    Assertions.assertTrue(nopeScopeEditor().profiles().contains("moana"));
    profileEditor.destroy();
    Assertions.assertFalse(nopeScopeEditor().profiles().contains("moana"));
  }

}
