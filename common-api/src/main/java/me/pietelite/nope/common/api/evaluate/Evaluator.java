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

package me.pietelite.nope.common.api.evaluate;

import java.util.UUID;
import me.pietelite.nope.common.api.struct.AltSet;
import org.jetbrains.annotations.Nullable;

/**
 * An entry point for retrieving the value evaluated for a specific Setting Key.
 * This is mostly used for evaluating a setting key at a certain location to ultimately determine how
 * some server or player behavior should be affected.
 *
 * <p>For example, a setting that determines "leaf decay" in the world might have a Setting Key with id
 * <code>leaf-decay</code>. The value it stores is a {@link Boolean}. If the server found a leaf block
 * that wanted to decay, it would ask the {@link Evaluator} what the value of <code>leaf-decay</code>
 * was at the location of the block. That resultant {@link Boolean} would determine whether the block
 * should go ahead and decay or remain in the world.
 */
public interface Evaluator {

  /**
   * Evaluate a (Poly) Setting Key at the given location.
   *
   * @param settingKey the setting key
   * @param x          the x location
   * @param y          the y location
   * @param z          the z location
   * @param domain     the domain
   * @param type       a class instance of {@link T}
   * @param <T>        the type of the resultant value
   * @return the result of the evaluation
   */
  <T> AltSet<T> polySetting(String settingKey, float x, float y, float z, String domain, Class<T> type);

  /**
   * Evaluate a (Poly) Setting Key at the given location for a specific player.
   *
   * @param settingKey the setting key
   * @param x          the x location
   * @param y          the y location
   * @param z          the z location
   * @param domain     the domain
   * @param player     the uuid of the player for whom the value is intended
   * @param type       a class instance of {@link T}
   * @param <T>        the type of the resultant value
   * @return the result of the evaluation
   */
  <T> AltSet<T> polySetting(String settingKey, float x, float y, float z, String domain,
                            @Nullable UUID player, Class<T> type);

  /**
   * Evaluate a (Poly) Setting Key at the given block location.
   *
   * @param settingKey the setting key
   * @param x          the x location
   * @param y          the y location
   * @param z          the z location
   * @param domain     the domain
   * @param type       a class instance of {@link T}
   * @param <T>        the type of the resultant value
   * @return the result of the evaluation
   */
  <T> AltSet<T> polySettingBlock(String settingKey, int x, int y, int z, String domain, Class<T> type);

  /**
   * Evaluate a (Poly) Setting Key at the given location for a specific player.
   *
   * @param settingKey the setting key
   * @param x          the x location
   * @param y          the y location
   * @param z          the z location
   * @param domain     the domain
   * @param player     the uuid of the player for whom the value is intended
   * @param type       a class instance of {@link T}
   * @param <T>        the type of the resultant value
   * @return the result of the evaluation
   */
  <T> AltSet<T> polySettingBlock(String settingKey, int x, int y, int z, String domain,
                                 @Nullable UUID player, Class<T> type);

  /**
   * Evaluate a (Poly) Setting Key, only considering the Global Host.
   *
   * @param settingKey the setting key
   * @param type       a class instance of {@link T}
   * @param <T>        the type of the resultant value
   * @return the result of the evaluation
   */
  <T> AltSet<T> polySettingGlobal(String settingKey, Class<T> type);

  /**
   * Evaluate a (Poly) Setting Key for a specific player, only considering the Global Host.
   *
   * @param settingKey the setting key
   * @param player     the uuid of the player for whom the value is intended
   * @param type       a class instance of {@link T}
   * @param <T>        the type of the resultant value
   * @return the result of the evaluation
   */
  <T> AltSet<T> polySettingGlobal(String settingKey, @Nullable UUID player, Class<T> type);

  /**
   * Evaluate a (Unary) Setting Key at the given location.
   *
   * @param settingKey the setting key
   * @param x          the x location
   * @param y          the y location
   * @param z          the z location
   * @param domain     the domain
   * @param type       a class instance of {@link T}
   * @param <T>        the type of the resultant value
   * @return the result of the evaluation
   */
  <T> T unarySetting(String settingKey, float x, float y, float z, String domain, Class<T> type);

  /**
   * Evaluate a (Unary) Setting Key at the given location for a specific player.
   *
   * @param settingKey the setting key
   * @param x          the x location
   * @param y          the y location
   * @param z          the z location
   * @param domain     the domain
   * @param player     the uuid of the player for whom the value is intended
   * @param type       a class instance of {@link T}
   * @param <T>        the type of the resultant value
   * @return the result of the evaluation
   */
  <T> T unarySetting(String settingKey, float x, float y, float z, String domain,
                     @Nullable UUID player, Class<T> type);

  /**
   * Evaluate a (Unary) Setting Key at the given block location.
   *
   * @param settingKey the setting key
   * @param x          the x location
   * @param y          the y location
   * @param z          the z location
   * @param domain     the domain
   * @param type       a class instance of {@link T}
   * @param <T>        the type of the resultant value
   * @return the result of the evaluation
   */
  <T> T unarySettingBlock(String settingKey, int x, int y, int z, String domain, Class<T> type);

  /**
   * Evaluate a (Unary) Setting Key at the given location for a specific player.
   *
   * @param settingKey the setting key
   * @param x          the x location
   * @param y          the y location
   * @param z          the z location
   * @param domain     the domain
   * @param player     the uuid of the player for whom the value is intended
   * @param type       a class instance of {@link T}
   * @param <T>        the type of the resultant value
   * @return the result of the evaluation
   */
  <T> T unarySettingBlock(String settingKey, int x, int y, int z, String domain,
                          @Nullable UUID player, Class<T> type);

  /**
   * Evaluate a (Unary) Setting Key, only considering the Global Host.
   *
   * @param settingKey the setting key
   * @param type       a class instance of {@link T}
   * @param <T>        the type of the resultant value
   * @return the result of the evaluation
   */
  <T> T unarySettingGlobal(String settingKey, Class<T> type);

  /**
   * Evaluate a (Unary) Setting Key for a specific player, only considering the Global Host.
   *
   * @param settingKey the setting key
   * @param player     the uuid of the player for whom the value is intended
   * @param type       a class instance of {@link T}
   * @param <T>        the type of the resultant value
   * @return the result of the evaluation
   */
  <T> T unarySettingGlobal(String settingKey, @Nullable UUID player, Class<T> type);

}
