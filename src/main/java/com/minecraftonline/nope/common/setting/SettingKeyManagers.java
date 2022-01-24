/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Pieter Svenson
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

package com.minecraftonline.nope.common.setting;

import com.minecraftonline.nope.common.setting.manager.AllCapsEnumKeyManager;
import com.minecraftonline.nope.common.setting.manager.BooleanKeyManager;
import com.minecraftonline.nope.common.setting.manager.IntegerKeyManager;
import com.minecraftonline.nope.common.setting.manager.PolyAllCapsEnumKeyManager;
import com.minecraftonline.nope.common.setting.manager.PolyStringKeyManager;
import com.minecraftonline.nope.common.setting.manager.StringKeyManager;
import com.minecraftonline.nope.common.setting.sets.BlockSet;
import com.minecraftonline.nope.common.setting.sets.EntitySet;
import com.minecraftonline.nope.common.setting.sets.ExplosiveSet;
import com.minecraftonline.nope.common.setting.sets.MovementSet;

public class SettingKeyManagers {

  public static final BooleanKeyManager BOOLEAN_KEY_MANAGER = new BooleanKeyManager();
  public static final BooleanKeyManager STATE_KEY_MANAGER = new BooleanKeyManager(true);
  public static final IntegerKeyManager INTEGER_KEY_MANAGER = new IntegerKeyManager();
  public static final StringKeyManager STRING_KEY_MANAGER = new StringKeyManager();
  public static final AllCapsEnumKeyManager<MovementSet.Movement> MOVEMENT_KEY_MANAGER =
      new AllCapsEnumKeyManager<>(MovementSet.Movement.class);

  public static final PolyStringKeyManager<EntitySet> POLY_ENTITY_KEY_MANAGER = new PolyStringKeyManager<>(EntitySet::new);
  public static final PolyStringKeyManager<BlockSet> POLY_BLOCK_KEY_MANAGER = new PolyStringKeyManager<>(BlockSet::new);
  public static final PolyAllCapsEnumKeyManager<ExplosiveSet.Explosive, ExplosiveSet> POLY_EXPLOSIVE_KEY_MANAGER =
      new PolyAllCapsEnumKeyManager<>(ExplosiveSet.Explosive.class, ExplosiveSet::new);

  private SettingKeyManagers() {
  }
}
