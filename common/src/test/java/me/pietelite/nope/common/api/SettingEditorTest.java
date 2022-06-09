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
import me.pietelite.nope.common.api.edit.MultipleValueSettingEditor;
import me.pietelite.nope.common.api.edit.SettingEditor;
import me.pietelite.nope.common.api.edit.SingleValueSettingEditor;
import me.pietelite.nope.common.api.setting.BlockChange;
import me.pietelite.nope.common.setting.SettingKeys;
import me.pietelite.nope.common.setting.sets.BlockChangeSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SettingEditorTest extends ApiTest {

  @Test
  void editSettingUsingUnary() {
    SettingEditor editor = nopeScopeEditor().createProfile("the_godfather").editSetting("concrete-solidification");
    TargetEditorTest.editTarget(editor.editTarget(), editor::hasTarget);
    Assertions.assertFalse(editor.hasValue());
    Assertions.assertFalse(editor.unsetValue());
  }

  @Test
  void editSettingsUsingPoly() {
    SettingEditor editor = nopeScopeEditor().createProfile("the_godfather_2").editSetting(SettingKeys.PLAYER_DAMAGE_SOURCE.name());
    TargetEditorTest.editTarget(editor.editTarget(), editor::hasTarget);
    Assertions.assertFalse(editor.hasValue());
    Assertions.assertFalse(editor.unsetValue());
  }

  @Test
  void editUnarySetting() {
    SingleValueSettingEditor<Boolean> editor = nopeScopeEditor().createProfile("tarzan").editSingleValueSetting(SettingKeys.CONCRETE_SOLIDIFICATION.name(), Boolean.class);
    TargetEditorTest.editTarget(editor.editTarget(), editor::hasTarget);
    Assertions.assertFalse(editor.hasValue());
    Assertions.assertThrows(NoSuchElementException.class, editor::get);
    editor.set(true);
    Assertions.assertTrue(editor.hasValue());
    Assertions.assertTrue(editor.get());
    editor.set(false);
    Assertions.assertTrue(editor.hasValue());
    Assertions.assertFalse(editor.get());
    editor.unsetValue();
    Assertions.assertFalse(editor.hasValue());
    Assertions.assertThrows(NoSuchElementException.class, editor::get);
  }

  @Test
  void editPolySetting() {
    MultipleValueSettingEditor<BlockChange> editor = nopeScopeEditor().createProfile("charlie-and-the-chocolate-factory")
        .editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class);
    TargetEditorTest.editTarget(editor.editTarget(), editor::hasTarget);
    Assertions.assertFalse(editor.hasValue());
    Assertions.assertFalse(editor.isDeclarative());
    Assertions.assertFalse(editor.isManipulative());
    Assertions.assertThrows(NoSuchElementException.class, editor::getDeclarative);
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.getManipulative(MultipleValueSettingEditor.ManipulativeType.ADDITIVE));
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.getManipulative(MultipleValueSettingEditor.ManipulativeType.SUBTRACTIVE));

    // declarative
    editor.setAll();
    Assertions.assertTrue(editor.hasValue());
    Assertions.assertTrue(editor.isDeclarative());
    Assertions.assertFalse(editor.isManipulative());
    Assertions.assertEquals(SettingKeys.BLOCK_CHANGE.manager().fullSet(), editor.getDeclarative());
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.getManipulative(MultipleValueSettingEditor.ManipulativeType.ADDITIVE));
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.getManipulative(MultipleValueSettingEditor.ManipulativeType.SUBTRACTIVE));
    editor.setNone();
    Assertions.assertTrue(editor.hasValue());
    Assertions.assertTrue(editor.isDeclarative());
    Assertions.assertFalse(editor.isManipulative());
    Assertions.assertEquals(SettingKeys.BLOCK_CHANGE.manager().emptySet(), editor.getDeclarative());
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.getManipulative(MultipleValueSettingEditor.ManipulativeType.ADDITIVE));
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.getManipulative(MultipleValueSettingEditor.ManipulativeType.SUBTRACTIVE));
    BlockChangeSet set = SettingKeys.BLOCK_CHANGE.manager().emptySet();
    set.add(BlockChange.BREAK);
    editor.setDeclarative(set);
    Assertions.assertTrue(editor.hasValue());
    Assertions.assertTrue(editor.isDeclarative());
    Assertions.assertFalse(editor.isManipulative());
    Assertions.assertEquals(set, editor.getDeclarative());
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.getManipulative(MultipleValueSettingEditor.ManipulativeType.ADDITIVE));
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.getManipulative(MultipleValueSettingEditor.ManipulativeType.SUBTRACTIVE));

    // manipulative
    editor.setManipulative(set, MultipleValueSettingEditor.ManipulativeType.ADDITIVE);
    Assertions.assertTrue(editor.hasValue());
    Assertions.assertFalse(editor.isDeclarative());
    Assertions.assertTrue(editor.isManipulative());
    Assertions.assertThrows(NoSuchElementException.class, editor::getDeclarative);
    Assertions.assertEquals(set, editor.getManipulative(MultipleValueSettingEditor.ManipulativeType.ADDITIVE));
    Assertions.assertEquals(SettingKeys.BLOCK_CHANGE.manager().emptySet(), editor.getManipulative(MultipleValueSettingEditor.ManipulativeType.SUBTRACTIVE));
    BlockChangeSet set2 = SettingKeys.BLOCK_CHANGE.manager().emptySet();
    set2.add(BlockChange.PLACE);
    editor.setManipulative(set2, MultipleValueSettingEditor.ManipulativeType.SUBTRACTIVE);
    Assertions.assertTrue(editor.hasValue());
    Assertions.assertFalse(editor.isDeclarative());
    Assertions.assertTrue(editor.isManipulative());
    Assertions.assertThrows(NoSuchElementException.class, editor::getDeclarative);
    Assertions.assertEquals(set, editor.getManipulative(MultipleValueSettingEditor.ManipulativeType.ADDITIVE));
    Assertions.assertEquals(set2, editor.getManipulative(MultipleValueSettingEditor.ManipulativeType.SUBTRACTIVE));

    // editing our set here shouldn't change the internal set
    set2.add(BlockChange.GROW);
    Assertions.assertNotEquals(set2, editor.getManipulative(MultipleValueSettingEditor.ManipulativeType.SUBTRACTIVE));

    // there can be no overlap between the two sets added to additive versus subtractive
    set2.add(BlockChange.BREAK);
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.setManipulative(set2, MultipleValueSettingEditor.ManipulativeType.SUBTRACTIVE));
    set.add(BlockChange.GROW);
    Assertions.assertThrows(IllegalArgumentException.class, () -> editor.setManipulative(set2, MultipleValueSettingEditor.ManipulativeType.ADDITIVE));

    editor.unsetValue();
    Assertions.assertFalse(editor.hasValue());
    Assertions.assertThrows(NoSuchElementException.class, editor::getDeclarative);
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.getManipulative(MultipleValueSettingEditor.ManipulativeType.ADDITIVE));
    Assertions.assertThrows(NoSuchElementException.class, () -> editor.getManipulative(MultipleValueSettingEditor.ManipulativeType.SUBTRACTIVE));
  }
}
