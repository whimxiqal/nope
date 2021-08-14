/*
 * MIT License
 *
 * Copyright (c) 2021 MinecraftOnline
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

package com.minecraftonline.nope.common.update;

import com.minecraftonline.nope.common.setting.Setting;
import com.minecraftonline.nope.common.setting.SettingKey;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A converter to allow the transferal of information
 * about a setting, given its key and some value stored
 * in the server configuration, to a new setting with some key
 * and some value.
 *
 * @param <T> the type of value stored in the original setting
 * @param <S> the type of value stored in the resulting setting
 */
@AllArgsConstructor
public class SettingUpdate<T, S> {

  @Getter
  private final SettingKey<T> key;
  @Getter
  private final Function<SettingValue<T>, Setting<S>> converter;

}
