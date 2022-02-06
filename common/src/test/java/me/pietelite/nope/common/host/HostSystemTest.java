/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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
 *
 */

package me.pietelite.nope.common.host;

import java.util.UUID;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.TestNope;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.common.setting.Setting;
import me.pietelite.nope.common.setting.SettingKeys;
import me.pietelite.nope.common.setting.SettingValue;
import me.pietelite.nope.common.setting.sets.BlockChangeSet;
import me.pietelite.nope.common.struct.AltSet;
import me.pietelite.nope.common.struct.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class HostSystemTest {

  private final static String D1 = "d1";
  private final static String D2 = "d2";

  TestNope nope;

  @BeforeEach
  void setUp() {
    nope = TestNope.init(D1, D2);
  }

  @Test
  void lookup() {
    final UUID operator = UUID.randomUUID();
    final UUID player = UUID.randomUUID();

    final Location inD1 = new Location(0, 0, 0, nope.hostSystem().domain(D1));
    final Location inD2 = new Location(0, 0, 0, nope.hostSystem().domain(D2));
    nope.registerPermission(operator, Permissions.UNRESTRICTED.get());

    nope.hostSystem().universe()
        .set(Setting.of(SettingKeys.ITEM_DROP, SettingValue.Unary.of(false)));

    nope.hostSystem().domain(D1)
        .set(Setting.of(SettingKeys.ITEM_DROP, SettingValue.Unary.of(true)));

    // Unary settings
//    assertTrue(nope.hostSystem().lookup(SettingKeys.ITEM_DROP, operator, inD1));
//    assertTrue(nope.hostSystem().lookup(SettingKeys.ITEM_DROP, operator, inD2),
//        "The operator is not properly unrestricted.");
//    assertTrue(nope.hostSystem().lookup(SettingKeys.ITEM_DROP, player, inD1),
//        "A normal player was not allowed to do something they should be able to do");
//    assertFalse(nope.hostSystem().lookup(SettingKeys.ITEM_DROP, player, inD2),
//        "A normal player was allowed to do something they should not be able to do");

    // Poly settings
    nope.hostSystem().universe()
        .set(Setting.of(SettingKeys.BLOCK_CHANGE,
            SettingValue.Poly.declarative(new BlockChangeSet())));
    nope.hostSystem().domain(D1)
        .set(Setting.of(SettingKeys.BLOCK_CHANGE,
            SettingValue.Poly.declarative(AltSet.full(new BlockChangeSet()))));

    assertTrue(nope.hostSystem().lookup(SettingKeys.BLOCK_CHANGE, operator, inD1).isFull());
//    assertTrue(nope.hostSystem().lookup(SettingKeys.BLOCK_CHANGE, operator, inD2).isFull(),
//        "The operator is not properly unrestricted.");
//    assertTrue(nope.hostSystem().lookup(SettingKeys.BLOCK_CHANGE, player, inD1).isFull(),
//        "A normal player was not allowed to do something they should be able to do");
//    assertTrue(nope.hostSystem().lookup(SettingKeys.BLOCK_CHANGE, player, inD2).isEmpty(),
//        "A normal player was allowed to do something they should not be able to do");
  }
}