package me.pietelite.nope.common.api;

import java.util.UUID;
import me.pietelite.nope.common.api.edit.MultipleValueSettingEditor;
import me.pietelite.nope.common.api.register.data.BlockChange;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingKeys;
import me.pietelite.nope.common.setting.sets.BlockChangeSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EvaluateTest extends ApiTest {

  @Test
  void evaluateDefaults() {
    Assertions.assertEquals(service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, D1, BlockChange.class),
        SettingKeys.BLOCK_CHANGE.defaultData());
    Assertions.assertEquals(service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, D1, UUID.randomUUID(), BlockChange.class),
        SettingKeys.BLOCK_CHANGE.defaultData());
    Assertions.assertEquals(service.evaluator().polySetting(SettingKeys.RIDE.name(), 0, 0, 0, D1, UUID.randomUUID(), Boolean.class),
        SettingKeys.RIDE.defaultData());
    Assertions.assertEquals(service.evaluator().polySetting(SettingKeys.RIDE.name(), 0, 0, 0, D1, UUID.randomUUID(), Boolean.class),
        SettingKeys.RIDE.defaultData());
  }

  @Test
  void evaluateWithGlobalChanges() {
    service.editSystem().editGlobal().addProfile("_global", 0);
    BlockChangeSet globalBlockChanges = new BlockChangeSet(true);
    globalBlockChanges.remove(BlockChange.BREAK);
    globalBlockChanges.remove(BlockChange.PLACE);
    service.editSystem().editProfile("_global").editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class)
        .setDeclarative(globalBlockChanges);
    Assertions.assertEquals(service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, D1, BlockChange.class),
        globalBlockChanges);
    Assertions.assertEquals(service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, D1, UUID.randomUUID(), BlockChange.class),
        globalBlockChanges);
    service.editSystem().editProfile("_global").editSingleValueSetting(SettingKeys.RIDE.name(), Boolean.class).set(false);
    Assertions.assertEquals(service.evaluator().unarySetting(SettingKeys.RIDE.name(), 0, 0, 0, D1, UUID.randomUUID(), Boolean.class),
        false);
    Assertions.assertEquals(service.evaluator().unarySetting(SettingKeys.RIDE.name(), 0, 0, 0, D1, UUID.randomUUID(), Boolean.class),
        false);
  }

  @Test
  void evaluateCascadeDeclarative() {
    // edit global
    service.editSystem().editGlobal().addProfile("_global", 0);
    BlockChangeSet globalBlockChanges = new BlockChangeSet(true);
    globalBlockChanges.remove(BlockChange.BREAK);
    globalBlockChanges.remove(BlockChange.PLACE);
    service.editSystem().editProfile("_global").editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class)
        .setDeclarative(globalBlockChanges);

    // edit domain
    service.editSystem().editDomain(D1).addProfile(D1, 0);
    BlockChangeSet domainBlockChanges = new BlockChangeSet(false);
    domainBlockChanges.add(BlockChange.GROW);
    service.editSystem().editProfile(D1).editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class)
        .setDeclarative(domainBlockChanges);

    Assertions.assertEquals(service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, D1, BlockChange.class),
        domainBlockChanges);
    Assertions.assertEquals(service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, D1, UUID.randomUUID(), BlockChange.class),
        domainBlockChanges);
    Assertions.assertEquals(service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, D2, BlockChange.class),
        globalBlockChanges);
    Assertions.assertEquals(service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, D2, UUID.randomUUID(), BlockChange.class),
        globalBlockChanges);

    // edit global
    service.editSystem().editProfile("_global").editSingleValueSetting(SettingKeys.RIDE.name(), Boolean.class).set(false);

    // edit domain
    service.editSystem().editProfile(D1).editSingleValueSetting(SettingKeys.RIDE.name(), Boolean.class).set(true);

    Assertions.assertEquals(service.evaluator().unarySetting(SettingKeys.RIDE.name(), 0, 0, 0, D1, Boolean.class), true);
    Assertions.assertEquals(service.evaluator().unarySetting(SettingKeys.RIDE.name(), 0, 0, 0, D1, UUID.randomUUID(), Boolean.class), true);
    Assertions.assertEquals(service.evaluator().unarySetting(SettingKeys.RIDE.name(), 0, 0, 0, D2, Boolean.class), false);
    Assertions.assertEquals(service.evaluator().unarySetting(SettingKeys.RIDE.name(), 0, 0, 0, D2, UUID.randomUUID(), Boolean.class), false);
  }

  @Test
  void evaluateCascadeManipulative() {
    assert(SettingKeys.BLOCK_CHANGE.defaultData().isFull());  // test only works if we have a key with a default full set
    // edit global
    service.editSystem().editGlobal().addProfile("_global", 0);
    BlockChangeSet globalBlockChanges = new BlockChangeSet(false);
    globalBlockChanges.add(BlockChange.BREAK);
    globalBlockChanges.add(BlockChange.PLACE);
    service.editSystem().editProfile("_global").editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class)
        .setManipulative(globalBlockChanges, MultipleValueSettingEditor.ManipulativeType.SUBTRACTIVE);

    // edit domain
    service.editSystem().editDomain(D1).addProfile(D1, 0);
    BlockChangeSet domainBlockChanges = new BlockChangeSet(false);
    domainBlockChanges.add(BlockChange.GROW);
    service.editSystem().editProfile(D1).editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class)
        .setManipulative(domainBlockChanges, MultipleValueSettingEditor.ManipulativeType.SUBTRACTIVE);

    BlockChangeSet d1Result = SettingKeys.BLOCK_CHANGE.defaultData();
    d1Result.removeAll(globalBlockChanges);
    d1Result.removeAll(domainBlockChanges);

    BlockChangeSet d2Result = SettingKeys.BLOCK_CHANGE.defaultData();
    d2Result.removeAll(globalBlockChanges);

    Assertions.assertEquals(service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, D1, BlockChange.class),
        d1Result);
    Assertions.assertEquals(service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, D1, UUID.randomUUID(), BlockChange.class),
        d1Result);
    Assertions.assertEquals(service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, D2, BlockChange.class),
        d2Result);
    Assertions.assertEquals(service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, D2, UUID.randomUUID(), BlockChange.class),
        d2Result);
  }

  // TODO add tests validating cascading effects of multiple-profile hosts

}
