///*
// * MIT License
// *
// * Copyright (c) 2020 MinecraftOnline
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// *
// */
//
//package com.minecraftonline.nope.common.settingold;
//
//import com.minecraftonline.nope.common.setting.value.SettingValue;
//import java.util.Objects;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.experimental.Accessors;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
///**
// * Setting DTO. Not for persistent storage.
// *
// * @param <T> the type of value stored
// */
//public class Setting<T> {
//
//  @Getter
//  @Accessors(fluent = true)
//  private final SettingKey<T> key;
//
//  @Getter
//  @Setter
//  @Accessors(fluent = true)
//  private SettingValue<T> data;
//
//  @Getter
//  @Setter
//  @Accessors(fluent = true)
//  @Nullable
//  private Target target;
//
//  private Setting(@NotNull SettingKey<T> key, @NotNull SettingValue<T> data, @Nullable Target target) {
//    this.key = key;
//    this.data = data;
//    this.target = target;
//  }
//
//  public static <X> Setting<X> of(@NotNull SettingKey<X> key,
//                                  @NotNull SettingValue<X> data) {
//    return new Setting<>(key, data, null);
//  }
//
//  public static <X> Setting<X> of(@NotNull SettingKey<X> key,
//                                  @NotNull SettingValue<X> data,
//                                  @Nullable Target target) {
//    return new Setting<>(key, data, target);
//  }
//
//  @SuppressWarnings("unchecked")
//  public static <X> Setting<X> ofUnchecked(@NotNull SettingKey<?> key,
//                                           @NotNull SettingValue<?> data,
//                                           @Nullable Target target) {
//    return new Setting<>((SettingKey<X>) key, (SettingValue<X>) data, target);
//  }
//
//  public T requireData() {
//    return Objects.requireNonNull(data).get();
//  }
//
//  public Target requireTarget() {
//    return Objects.requireNonNull(target);
//  }
//
//}
