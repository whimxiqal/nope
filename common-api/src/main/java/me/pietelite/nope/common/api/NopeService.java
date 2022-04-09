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
import me.pietelite.nope.common.api.setting.data.BlockChange;
import me.pietelite.nope.common.api.setting.data.DamageCause;
import me.pietelite.nope.common.api.setting.data.Explosive;
import me.pietelite.nope.common.api.setting.data.Movement;
import me.pietelite.nope.common.api.struct.AltSet;

/**
 * A developer API for all platform implementations of Nope.
 */
public interface NopeService {

  /**
   * Returns a {@link SettingKeyBuilder} to build a key for settings with {@link Boolean} values.
   * The values are set with the terms "true" and "false". Due to these bland-sounding terms,
   * the {@link #toggleKeyBuilder(String)} is generally preferred over this builder.
   *
   * @param id the identifier of the setting
   * @return the builder
   */
  SettingKeyBuilder.Unary<Boolean, ?> booleanKeyBuilder(String id);

  /**
   * Returns a {@link SettingKeyBuilder} to build a key for settings with {@link Boolean} values.
   * The values are set with the terms "allow" and "deny", so it is best used for settings that
   * represent the ability for a player or entity to do something.
   *
   * <p>For example, "ride" is a setting with this type because a player either is allowed to
   * ride an entity or not ride an entity.
   *
   * @param id the identifier of the setting
   * @return the builder
   */
  SettingKeyBuilder.Unary<Boolean, ?> stateKeyBuilder(String id);

  /**
   * Returns a {@link SettingKeyBuilder} to build a key for settings with {@link Boolean} values.
   * The values are set with the terms "on" and "off", so it is best used for settings that
   * represent a simple stand-alone processes of the server that can either happen or simply not happen.
   *
   * <p>For example, "leaf-decay" is a setting with this type because it is something the server does,
   * irrespective of player intervention.
   *
   * @param id the identifier of the setting
   * @return the builder
   */
  SettingKeyBuilder.Unary<Boolean, ?> toggleKeyBuilder(String id);

  /**
   * Returns a {@link SettingKeyBuilder} to build a key for settings with {@link Integer} values.
   *
   * @param id the identifier of the setting
   * @return the builder
   */
  SettingKeyBuilder.Unary<Integer, ?> integerKeyBuilder(String id);

  /**
   * Returns a {@link SettingKeyBuilder} to build a key for settings with {@link String} values.
   *
   * @param id the identifier of the setting
   * @return the builder
   */
  SettingKeyBuilder.Unary<String, ?> stringKeyBuilder(String id);

  /**
   * Returns a {@link SettingKeyBuilder} to build a key for settings with {@link String} values
   * that represent types of Minecraft entities.
   *
   * @param id the identifier of the setting
   * @return the builder
   */
  SettingKeyBuilder.Poly<String, ?, ?> entitiesManager(String id);

  /**
   * Returns a {@link SettingKeyBuilder} to build a key for settings with {@link String} values
   * that represent types of Minecraft blocks that are able to grow.
   *
   * @param id the identifier of the setting
   * @return the builder
   */
  SettingKeyBuilder.Poly<String, ? extends AltSet<String>, ?> growablesManager(String id);

  /**
   * Returns a {@link SettingKeyBuilder} to build a key for settings with {@link String} values,
   * which are each the name of a plugin.
   *
   * @param id the identifier of the setting
   * @return the builder
   */
  SettingKeyBuilder.Poly<String, ? extends AltSet<String>, ?> pluginsManager(String id);

  /**
   * Returns a {@link SettingKeyBuilder} to build a key for settings with {@link BlockChange} values.
   *
   * @param id the identifier of the setting
   * @return the builder
   */
  SettingKeyBuilder.Poly<BlockChange, ?, ?> blockChangesManager(String id);

  /**
   * Returns a {@link SettingKeyBuilder} to build a key for settings with {@link DamageCause} values.
   *
   * @param id the identifier of the setting
   * @return the builder
   */
  SettingKeyBuilder.Poly<DamageCause, ?, ?> damageCausesManager(String id);

  /**
   * Returns a {@link SettingKeyBuilder} to build a key for settings with {@link Explosive} values.
   *
   * @param id the identifier of the setting
   * @return the builder
   */
  SettingKeyBuilder.Poly<Explosive, ?, ?> explosivesManager(String id);

  /**
   * Returns a {@link SettingKeyBuilder} to build a key for settings with {@link Movement} values.
   *
   * @param id the identifier of the setting
   * @return the builder
   */
  SettingKeyBuilder.Poly<Movement, ?, ?> movementsManager(String id);

}
