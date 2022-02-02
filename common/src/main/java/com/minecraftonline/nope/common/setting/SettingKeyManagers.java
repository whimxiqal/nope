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
 */

package com.minecraftonline.nope.common.setting;

import com.minecraftonline.nope.common.setting.manager.BooleanKeyManager;
import com.minecraftonline.nope.common.setting.manager.IntegerKeyManager;
import com.minecraftonline.nope.common.setting.manager.PolyAllCapsEnumKeyManager;
import com.minecraftonline.nope.common.setting.manager.PolyStringKeyManager;
import com.minecraftonline.nope.common.setting.manager.StateKeyManager;
import com.minecraftonline.nope.common.setting.manager.StringKeyManager;
import com.minecraftonline.nope.common.setting.manager.ToggleKeyManager;
import com.minecraftonline.nope.common.setting.sets.BlockChangeSet;
import com.minecraftonline.nope.common.setting.sets.DamageCauseSet;
import com.minecraftonline.nope.common.setting.sets.ExplosiveSet;
import com.minecraftonline.nope.common.setting.sets.MobGriefSet;
import com.minecraftonline.nope.common.setting.sets.MovementSet;
import com.minecraftonline.nope.common.setting.sets.StringSet;

public class SettingKeyManagers {

  public static final BooleanKeyManager BOOLEAN_KEY_MANAGER = new BooleanKeyManager();
  public static final IntegerKeyManager INTEGER_KEY_MANAGER = new IntegerKeyManager();
  public static final PolyAllCapsEnumKeyManager<BlockChangeSet.BlockChange, BlockChangeSet>
      POLY_BLOCK_CHANGE_KEY_MANAGER =
      new PolyAllCapsEnumKeyManager<>(BlockChangeSet.BlockChange.class, BlockChangeSet::new);
  public static final PolyStringKeyManager<StringSet> POLY_BLOCK_KEY_MANAGER =
      new PolyStringKeyManager<>(StringSet::new);
  public static final PolyAllCapsEnumKeyManager<DamageCauseSet.DamageCause, DamageCauseSet>
      POLY_DAMAGE_SOURCE_KEY_MANAGER =
      new PolyAllCapsEnumKeyManager<>(DamageCauseSet.DamageCause.class, DamageCauseSet::new);
  public static final PolyStringKeyManager<StringSet> POLY_ENTITY_KEY_MANAGER =
      new PolyStringKeyManager<>(StringSet::new);
  public static final PolyAllCapsEnumKeyManager<ExplosiveSet.Explosive, ExplosiveSet>
      POLY_EXPLOSIVE_KEY_MANAGER =
      new PolyAllCapsEnumKeyManager<>(ExplosiveSet.Explosive.class, ExplosiveSet::new);
  public static final PolyStringKeyManager<StringSet> POLY_GROWABLE_KEY_MANAGER =
      new PolyStringKeyManager<>(StringSet::new);
  public static final PolyAllCapsEnumKeyManager<MobGriefSet.MobGrief, MobGriefSet>
      POLY_MOB_GRIEF_KEY_MANAGER =
      new PolyAllCapsEnumKeyManager<>(MobGriefSet.MobGrief.class, MobGriefSet::new);
  public static final PolyAllCapsEnumKeyManager<MovementSet.Movement, MovementSet> POLY_MOVEMENT_KEY_MANAGER =
      new PolyAllCapsEnumKeyManager<>(MovementSet.Movement.class, MovementSet::new);
  public static final StateKeyManager STATE_KEY_MANAGER = new StateKeyManager();
  public static final StringKeyManager STRING_KEY_MANAGER = new StringKeyManager();
  public static final ToggleKeyManager TOGGLE_KEY_MANAGER = new ToggleKeyManager();

  private SettingKeyManagers() {
  }
}
