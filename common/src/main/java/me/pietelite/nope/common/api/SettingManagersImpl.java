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

import me.pietelite.nope.common.api.setting.SettingKeyBuilder;
import me.pietelite.nope.common.api.setting.BlockChange;
import me.pietelite.nope.common.api.setting.DamageCause;
import me.pietelite.nope.common.api.setting.Explosive;
import me.pietelite.nope.common.api.setting.Movement;
import me.pietelite.nope.common.api.setting.SettingManager;
import me.pietelite.nope.common.api.setting.SettingManagers;
import me.pietelite.nope.common.api.struct.AltSet;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingKeyManagers;

/**
 * Implementation of {@link SettingManagers}.
 */
public class SettingManagersImpl implements SettingManagers {

  @Override
  public SettingManager.Unary<Boolean> booleanManager() {
    return new SettingManagerUnaryImpl<>(SettingKeyManagers.BOOLEAN_KEY_MANAGER);
  }

  @Override
  public SettingManager.Unary<Boolean> stateManager() {
    return new SettingManagerUnaryImpl<>(SettingKeyManagers.STATE_KEY_MANAGER);
  }

  @Override
  public SettingManager.Unary<Boolean> toggleManager() {
    return new SettingManagerUnaryImpl<>(SettingKeyManagers.TOGGLE_KEY_MANAGER);
  }

  @Override
  public SettingManager.Unary<Integer> integerManager() {
    return new SettingManagerUnaryImpl<>(SettingKeyManagers.INTEGER_KEY_MANAGER);
  }

  @Override
  public SettingManager.Unary<String> stringManager() {
    return new SettingManagerUnaryImpl<>(SettingKeyManagers.STRING_KEY_MANAGER);
  }

  @Override
  public SettingManager.Poly<String> entityManager() {
    return new SettingManagerPolyImpl<>(SettingKeyManagers.POLY_ENTITY_KEY_MANAGER);
  }

  @Override
  public SettingManager.Poly<String> growablesManager() {
    return new SettingManagerPolyImpl<>(SettingKeyManagers.POLY_GROWABLE_KEY_MANAGER);
  }

  @Override
  public SettingManager.Poly<String> pluginsManager() {
    return new SettingManagerPolyImpl<>(SettingKeyManagers.POLY_PLUGIN_MANAGER);
  }

  @Override
  public SettingManager.Poly<BlockChange> blockChangesManager() {
    return new SettingManagerPolyImpl<>(SettingKeyManagers.POLY_BLOCK_CHANGE_KEY_MANAGER);
  }

  @Override
  public SettingManager.Poly<DamageCause> damageCausesManager() {
    return new SettingManagerPolyImpl<>(SettingKeyManagers.POLY_DAMAGE_SOURCE_KEY_MANAGER);
  }

  @Override
  public SettingManager.Poly<Explosive> explosivesManager() {
    return new SettingManagerPolyImpl<>(SettingKeyManagers.POLY_EXPLOSIVE_KEY_MANAGER);
  }

  @Override
  public SettingManager.Poly<Movement> movementsManager() {
    return new SettingManagerPolyImpl<>(SettingKeyManagers.POLY_MOVEMENT_KEY_MANAGER);
  }

  /**
   * Implementation of {@link SettingManager.Unary}.
   *
   * @param <T> the data type
   */
  public static class SettingManagerUnaryImpl<T> implements SettingManager.Unary<T> {

    private final SettingKey.Manager.Unary<T> manager;

    public SettingManagerUnaryImpl(SettingKey.Manager.Unary<T> manager) {
      this.manager = manager;
    }

    @Override
    public SettingKeyBuilder.Unary<T> settingKeyBuilder(String id) {
      return manager.keyBuilder(id);
    }
  }

  /**
   * Implementation of {@link SettingManager.Poly}.
   *
   * @param <T> the data type
   */
  public static class SettingManagerPolyImpl<T> implements SettingManager.Poly<T> {

    private final SettingKey.Manager.Poly<T, ?> manager;

    public SettingManagerPolyImpl(SettingKey.Manager.Poly<T, ?> manager) {
      this.manager = manager;
    }

    @Override
    public SettingKeyBuilder.Poly<T> settingKeyBuilder(String id) {
      return manager.keyBuilder(id);
    }

    @Override
    public AltSet<T> emptySet() {
      return manager.emptySet();
    }

    @Override
    public AltSet<T> fullSet() {
      return manager.fullSet();
    }
  }

}
