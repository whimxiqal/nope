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

package me.pietelite.nope.common.setting;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Setting DTO. Not for persistent storage.
 *
 * @param <T> the type of value stored
 */
public class Setting<T, V extends SettingValue<T>> {

  @Getter
  @Accessors(fluent = true)
  private final SettingKey<T, V, ?> key;

  @Getter
  @Setter
  @Accessors(fluent = true)
  private V value;

  @Getter
  @Setter
  @Accessors(fluent = true)
  @Nullable
  private Target target;

  private Setting(@NotNull SettingKey<T, V, ?> key, @Nullable V value, @Nullable Target target) {
    this.key = key;
    this.value = value;
    this.target = target;
  }

  public static <X, Y extends SettingValue<X>> Setting<X, Y> of(@NotNull SettingKey<X, Y, ?> key,
                                                                                                                                          @Nullable Y data) {
    return new Setting<>(key, data, null);
  }

  public static <X, Y extends SettingValue<X>> Setting<X, Y> of(@NotNull SettingKey<X, Y, ?> key,
                                                                                                                                          @Nullable Y data,
                                                                                                                                          @Nullable Target target) {
    return new Setting<>(key, data, target);
  }

  @SuppressWarnings("unchecked")
  public static <X, Y extends SettingValue<X>> Setting<X, Y> ofUnchecked(@NotNull SettingKey<?, ?, ?> key,
                                                                                                                                                   @NotNull SettingValue<?> data,
                                                                                                                                                   @Nullable Target target) {
    return new Setting<>((SettingKey<X, Y, ?>) key, (Y) data, target);
  }

  public Target requireTarget() {
    return Objects.requireNonNull(target);
  }

}
