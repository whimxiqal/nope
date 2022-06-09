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

import java.util.UUID;
import me.pietelite.nope.common.MockNope;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.MultipleValueSettingEditor;
import me.pietelite.nope.common.api.setting.BlockChange;
import me.pietelite.nope.common.setting.SettingKeys;
import me.pietelite.nope.common.setting.sets.BlockChangeSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EvaluateTest extends ApiTest {

  @Test
  void evaluateDefaults() {
    Assertions.assertEquals(SettingKeys.BLOCK_CHANGE.defaultData(), service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, MockNope.DOMAIN_1, BlockChange.class));
    Assertions.assertEquals(SettingKeys.BLOCK_CHANGE.defaultData(), service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, MockNope.DOMAIN_1, UUID.randomUUID(), BlockChange.class));
    Assertions.assertEquals(SettingKeys.RIDE.defaultData(), service().evaluator().unarySetting(SettingKeys.RIDE.name(), 0, 0, 0, MockNope.DOMAIN_1, UUID.randomUUID(), Boolean.class));
    Assertions.assertEquals( SettingKeys.RIDE.defaultData(), service().evaluator().unarySetting(SettingKeys.RIDE.name(), 0, 0, 0, MockNope.DOMAIN_1, UUID.randomUUID(), Boolean.class));
  }

  @Test
  void evaluateWithGlobalChanges() {
    BlockChangeSet globalBlockChanges = new BlockChangeSet(true);
    globalBlockChanges.remove(BlockChange.BREAK);
    globalBlockChanges.remove(BlockChange.PLACE);
    service().editSystem().editScope(Nope.NOPE_SCOPE).editProfile(Nope.GLOBAL_ID).editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class).setDeclarative(globalBlockChanges);
    Assertions.assertEquals(globalBlockChanges, service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, MockNope.DOMAIN_1, BlockChange.class));
    Assertions.assertEquals(globalBlockChanges, service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, MockNope.DOMAIN_1, UUID.randomUUID(), BlockChange.class));
    Assertions.assertEquals(globalBlockChanges, service().evaluator().polySettingGlobal(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class));
    Assertions.assertEquals(globalBlockChanges, service().evaluator().polySettingGlobal(SettingKeys.BLOCK_CHANGE.name(), UUID.randomUUID(), BlockChange.class));
    service().editSystem().editScope(Nope.NOPE_SCOPE).editProfile(Nope.GLOBAL_ID).editSingleValueSetting(SettingKeys.RIDE.name(), Boolean.class).set(false);
    Assertions.assertEquals(false, service().evaluator().unarySetting(SettingKeys.RIDE.name(), 0, 0, 0, MockNope.DOMAIN_1, UUID.randomUUID(), Boolean.class));
    Assertions.assertEquals(false, service().evaluator().unarySetting(SettingKeys.RIDE.name(), 0, 0, 0, MockNope.DOMAIN_1, UUID.randomUUID(), Boolean.class));
  }

  @Test
  void evaluateCascadeDeclarative() {
    // edit global
    BlockChangeSet globalBlockChanges = new BlockChangeSet(true);
    globalBlockChanges.remove(BlockChange.BREAK);
    globalBlockChanges.remove(BlockChange.PLACE);
    service().editSystem().editScope(Nope.NOPE_SCOPE).editProfile(Nope.GLOBAL_ID).editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class)
        .setDeclarative(globalBlockChanges);

    // edit domain
    service().editSystem().editScope(Nope.NOPE_SCOPE).createProfile(MockNope.DOMAIN_1);
    service().editSystem().editDomain(MockNope.DOMAIN_1).addProfile(Nope.NOPE_SCOPE, MockNope.DOMAIN_1, 0);
    BlockChangeSet domainBlockChanges = new BlockChangeSet(false);
    domainBlockChanges.add(BlockChange.GROW);
    service().editSystem().editScope(Nope.NOPE_SCOPE).editProfile(MockNope.DOMAIN_1).editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class)
        .setDeclarative(domainBlockChanges);

    Assertions.assertEquals(globalBlockChanges, service().evaluator().polySettingGlobal(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class));
    Assertions.assertEquals(domainBlockChanges, service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, MockNope.DOMAIN_1, BlockChange.class));
    Assertions.assertEquals(domainBlockChanges, service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, MockNope.DOMAIN_1, UUID.randomUUID(), BlockChange.class));
    Assertions.assertEquals(globalBlockChanges, service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, MockNope.DOMAIN_2, BlockChange.class));
    Assertions.assertEquals(globalBlockChanges, service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, MockNope.DOMAIN_2, UUID.randomUUID(), BlockChange.class));

    // edit global
    service().editSystem().editScope(Nope.NOPE_SCOPE).editProfile(Nope.GLOBAL_ID).editSingleValueSetting(SettingKeys.RIDE.name(), Boolean.class).set(false);

    // edit domain
    service().editSystem().editScope(Nope.NOPE_SCOPE).editProfile(MockNope.DOMAIN_1).editSingleValueSetting(SettingKeys.RIDE.name(), Boolean.class).set(true);

    Assertions.assertEquals(service().evaluator().unarySetting(SettingKeys.RIDE.name(), 0, 0, 0, MockNope.DOMAIN_1, Boolean.class), true);
    Assertions.assertEquals(service().evaluator().unarySetting(SettingKeys.RIDE.name(), 0, 0, 0, MockNope.DOMAIN_1, UUID.randomUUID(), Boolean.class), true);
    Assertions.assertEquals(service().evaluator().unarySetting(SettingKeys.RIDE.name(), 0, 0, 0, MockNope.DOMAIN_2, Boolean.class), false);
    Assertions.assertEquals(service().evaluator().unarySetting(SettingKeys.RIDE.name(), 0, 0, 0, MockNope.DOMAIN_2, UUID.randomUUID(), Boolean.class), false);
  }

  @Test
  void evaluateCascadeManipulative() {
    assert(SettingKeys.BLOCK_CHANGE.defaultData().isFull());  // test only works if we have a key with a default full set
    // edit global
    BlockChangeSet globalBlockChanges = new BlockChangeSet(false);
    globalBlockChanges.add(BlockChange.BREAK);
    globalBlockChanges.add(BlockChange.PLACE);
    service().editSystem().editScope(Nope.NOPE_SCOPE).editProfile(Nope.GLOBAL_ID).editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class)
        .setManipulative(globalBlockChanges, MultipleValueSettingEditor.ManipulativeType.SUBTRACTIVE);

    // edit domain
    service().editSystem().editScope(Nope.NOPE_SCOPE).createProfile(MockNope.DOMAIN_1);
    service().editSystem().editDomain(MockNope.DOMAIN_1).addProfile(Nope.NOPE_SCOPE, MockNope.DOMAIN_1, 0);
    BlockChangeSet domainBlockChanges = new BlockChangeSet(false);
    domainBlockChanges.add(BlockChange.GROW);
    service().editSystem().editScope(Nope.NOPE_SCOPE).editProfile(MockNope.DOMAIN_1).editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class)
        .setManipulative(domainBlockChanges, MultipleValueSettingEditor.ManipulativeType.SUBTRACTIVE);

    BlockChangeSet d1Result = new BlockChangeSet(true);
    d1Result.removeAll(globalBlockChanges);
    d1Result.removeAll(domainBlockChanges);

    BlockChangeSet d2Result = new BlockChangeSet(true);
    d2Result.removeAll(globalBlockChanges);

    Assertions.assertEquals(d1Result, service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, MockNope.DOMAIN_1, BlockChange.class));
    Assertions.assertEquals(d1Result, service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, MockNope.DOMAIN_1, UUID.randomUUID(), BlockChange.class));
    Assertions.assertEquals(d2Result, service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, MockNope.DOMAIN_2, BlockChange.class));
    Assertions.assertEquals(d2Result, service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, MockNope.DOMAIN_2, UUID.randomUUID(), BlockChange.class));
  }

  // TODO add tests validating cascading effects of multiple-profile hosts

}
