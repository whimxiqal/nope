package me.pietelite.nope.common.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.pietelite.nope.common.api.edit.ProfileEditor;
import me.pietelite.nope.common.api.edit.SystemEditor;
import me.pietelite.nope.common.api.register.data.BlockChange;
import me.pietelite.nope.common.setting.SettingKeys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CaseTest extends ApiTest {

  @Test
  void testMarketplace() {
    SystemEditor editor = service.editSystem();

    // setup marketplace
    String name = "marketplace";
    assertSuccess(editor.createScene(name, 10));
    assertSuccess(editor.createProfile(name));
    assertSuccess(editor.editScene(name).addProfile(name, 0));
    assertSuccess(editor.editScene(name).addCuboid(D1, 0, 0, 0, 100, 100, 100));
    ProfileEditor marketplaceProfile = editor.editProfile(name);
    assertSuccess(marketplaceProfile.editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class)
        .setDeclarative(SettingKeys.BLOCK_CHANGE.manager().emptySet()));
    assertSuccess(marketplaceProfile.editMultipleValueSetting(SettingKeys.INTERACTIVE_BLOCKS.name(), String.class)
        .setDeclarative(SettingKeys.INTERACTIVE_BLOCKS.manager().emptySet()));
    assertSuccess(marketplaceProfile.editMultipleValueSetting(SettingKeys.INTERACTIVE_ENTITIES.name(), String.class)
        .setDeclarative(SettingKeys.INTERACTIVE_ENTITIES.manager().emptySet()));

    // setup stall profile
    assertSuccess(editor.createProfile("stall"));
    ProfileEditor stallProfile = editor.editProfile("stall");
    assertSuccess(marketplaceProfile.editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class)
        .setDeclarative(SettingKeys.BLOCK_CHANGE.manager().fullSet()));
    assertSuccess(marketplaceProfile.editMultipleValueSetting(SettingKeys.INTERACTIVE_BLOCKS.name(), String.class)
        .setDeclarative(SettingKeys.INTERACTIVE_BLOCKS.manager().fullSet()));
    assertSuccess(marketplaceProfile.editMultipleValueSetting(SettingKeys.INTERACTIVE_ENTITIES.name(), String.class)
        .setDeclarative(SettingKeys.INTERACTIVE_ENTITIES.manager().fullSet()));

    int index = 0;
    String stallName;
    Map<Integer, UUID> playerUuids = new HashMap<>();
    for (int x = 0; x < 10; x++) {
      for (int z = 0; z < 10; z++) {
        stallName = "stall" + index;
        assertSuccess(editor.createScene(stallName, 20));
        assertSuccess(editor.editScene(stallName).addCuboid(D1,
            x * 10, 0, z * 10,
            (x + 1) * 10, 10, (z + 1) * 10));
        assertSuccess(editor.editScene(stallName).addProfile("stall", 0));

        UUID playerUuid = UUID.randomUUID();
        playerUuids.put(index, playerUuid);
        assertSuccess(editor.editScene(stallName).editTarget(0).targetPlayer(playerUuid));

        index++;
      }
    }

    // In his own stall
    Assertions.assertEquals( SettingKeys.BLOCK_CHANGE.manager().fullSet(),
        service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, D1, playerUuids.get(0), BlockChange.class));
    // On the outside border of this own stall (non-inclusive)
    Assertions.assertEquals(SettingKeys.BLOCK_CHANGE.manager().emptySet(),
        service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 10, 10, 10, D1, playerUuids.get(0), BlockChange.class));
    // Outside the marketplace
    Assertions.assertEquals(SettingKeys.BLOCK_CHANGE.manager().fullSet(),
        service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 1000, 1000, 1000, D1, playerUuids.get(0), BlockChange.class));
    // Someone else in the first person's stall
    Assertions.assertEquals(SettingKeys.BLOCK_CHANGE.manager().emptySet(),
        service.evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 1, 1, 1, D1, playerUuids.get(12), BlockChange.class));

  }

}
