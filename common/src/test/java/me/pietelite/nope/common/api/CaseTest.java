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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.pietelite.nope.common.MockNope;
import me.pietelite.nope.common.api.edit.ProfileEditor;
import me.pietelite.nope.common.api.edit.SystemEditor;
import me.pietelite.nope.common.api.register.data.BlockChange;
import me.pietelite.nope.common.setting.SettingKeys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CaseTest extends ApiTest {

  @Test
  void testMarketplace() {
    SystemEditor editor = service().editSystem();

    // setup marketplace
    String name = "marketplace";
    editor.createScene(name, 10);
    editor.createProfile(name);
    editor.editScene(name).addProfile(name, 0);
    editor.editScene(name).addCuboid(MockNope.DOMAIN_1, 0, 0, 0, 100, 100, 100);
    ProfileEditor marketplaceProfile = editor.editProfile(name);
    marketplaceProfile.editSingleValueSetting(SettingKeys.RIDE.name(), Boolean.class).set(false);
    marketplaceProfile.editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class)
        .setDeclarative(SettingKeys.BLOCK_CHANGE.manager().emptySet());
    marketplaceProfile.editMultipleValueSetting(SettingKeys.INTERACTIVE_BLOCKS.name(), String.class)
        .setDeclarative(SettingKeys.INTERACTIVE_BLOCKS.manager().emptySet());
    marketplaceProfile.editMultipleValueSetting(SettingKeys.INTERACTIVE_ENTITIES.name(), String.class)
        .setDeclarative(SettingKeys.INTERACTIVE_ENTITIES.manager().emptySet());

    Assertions.assertEquals( SettingKeys.BLOCK_CHANGE.manager().emptySet(),
        service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, MockNope.DOMAIN_1,
            null, BlockChange.class));
    Assertions.assertEquals( false,
        service().evaluator().unarySetting(SettingKeys.RIDE.name(), 0, 0, 0, MockNope.DOMAIN_1,
            null, Boolean.class));

    // setup stall profile
    editor.createProfile("stall");
    ProfileEditor stallProfile = editor.editProfile("stall");
    stallProfile.editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class)
        .setDeclarative(SettingKeys.BLOCK_CHANGE.manager().fullSet());
    stallProfile.editMultipleValueSetting(SettingKeys.INTERACTIVE_BLOCKS.name(), String.class)
        .setDeclarative(SettingKeys.INTERACTIVE_BLOCKS.manager().fullSet());
    stallProfile.editMultipleValueSetting(SettingKeys.INTERACTIVE_ENTITIES.name(), String.class)
        .setDeclarative(SettingKeys.INTERACTIVE_ENTITIES.manager().fullSet());

    int index = 0;
    String stallName;
    Map<Integer, UUID> playerUuids = new HashMap<>();
    for (int x = 0; x < 10; x++) {
      for (int z = 0; z < 10; z++) {
        stallName = "stall" + index;
        editor.createScene(stallName, 20);
        editor.editScene(stallName).addCuboid(MockNope.DOMAIN_1,
            x * 10, 0, z * 10,
            (x + 1) * 10, 10, (z + 1) * 10);
        editor.editScene(stallName).addProfile("stall", 0);

        UUID playerUuid = UUID.randomUUID();
        playerUuids.put(index, playerUuid);
        Assertions.assertTrue(editor.editScene(stallName).editTarget(0).addPlayer(playerUuid));
        Assertions.assertTrue(editor.editScene(stallName).editTarget(0).playerSet().contains(playerUuid));
        index++;
      }
    }

    // In his own stall
    Assertions.assertEquals( SettingKeys.BLOCK_CHANGE.manager().fullSet(),
        service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 5, 5, 5, MockNope.DOMAIN_1,
            playerUuids.get(0), BlockChange.class));
    // On the outside border of his own stall (inclusive)
    Assertions.assertEquals( SettingKeys.BLOCK_CHANGE.manager().fullSet(),
        service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 0, 0, 0, MockNope.DOMAIN_1,
            playerUuids.get(0), BlockChange.class));
    // On the outside border of his own stall (non-inclusive)
    Assertions.assertEquals(SettingKeys.BLOCK_CHANGE.manager().emptySet(),
        service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 10, 10, 10, MockNope.DOMAIN_1,
            playerUuids.get(0), BlockChange.class));
    // Outside the marketplace
    Assertions.assertEquals(SettingKeys.BLOCK_CHANGE.manager().fullSet(),
        service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 1000, 1000, 1000, MockNope.DOMAIN_1,
            playerUuids.get(0), BlockChange.class));
    // Someone else in the first person's stall
    System.out.println("About to evaluate target:");
    Assertions.assertEquals(SettingKeys.BLOCK_CHANGE.manager().emptySet(),
        service().evaluator().polySetting(SettingKeys.BLOCK_CHANGE.name(), 1, 1, 1, MockNope.DOMAIN_1,
            playerUuids.get(12), BlockChange.class));

  }

}
