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

package me.pietelite.nope.common;

import me.pietelite.nope.common.api.NopeService;
import me.pietelite.nope.common.api.setting.SettingKeyBuilder;
import me.pietelite.nope.common.api.setting.data.BlockChange;
import me.pietelite.nope.common.api.setting.data.DamageCause;
import me.pietelite.nope.common.api.setting.data.Explosive;
import me.pietelite.nope.common.api.setting.data.Movement;
import me.pietelite.nope.common.setting.SettingKeyManagers;

/**
 * The implementation of the {@link NopeService}.
 */
public class NopeServiceImpl implements NopeService {

  @Override
  public SettingKeyBuilder.Unary<Boolean, ?> booleanKeyBuilder(String id) {
    return SettingKeyManagers.BOOLEAN_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Unary<Boolean, ?> stateKeyBuilder(String id) {
    return SettingKeyManagers.STATE_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Unary<Boolean, ?> toggleKeyBuilder(String id) {
    return SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Unary<Integer, ?> integerKeyBuilder(String id) {
    return SettingKeyManagers.INTEGER_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Unary<String, ?> stringKeyBuilder(String id) {
    return SettingKeyManagers.STRING_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Poly<String, ?, ?> entitiesManager(String id) {
    return SettingKeyManagers.POLY_ENTITY_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Poly<String, ?, ?> growablesManager(String id) {
    return SettingKeyManagers.POLY_GROWABLE_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Poly<String, ?, ?> pluginsManager(String id) {
    return SettingKeyManagers.POLY_PLUGIN_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Poly<BlockChange, ?, ?> blockChangesManager(String id) {
    return SettingKeyManagers.POLY_BLOCK_CHANGE_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Poly<DamageCause, ?, ?> damageCausesManager(String id) {
    return SettingKeyManagers.POLY_DAMAGE_SOURCE_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Poly<Explosive, ?, ?> explosivesManager(String id) {
    return SettingKeyManagers.POLY_EXPLOSIVE_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Poly<Movement, ?, ?> movementsManager(String id) {
    return SettingKeyManagers.POLY_MOVEMENT_KEY_MANAGER.keyBuilder(id);
  }
}
