///*
// * MIT License
// *
// * Copyright (c) 2021 MinecraftOnline
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
//package com.minecraftonline.nope.common.setting.keys;
//
//import com.google.common.collect.Lists;
//import com.minecraftonline.nope.common.setting.SettingKey;
//import java.util.List;
//
///**
// * A setting storing a boolean value.
// */
//public class BooleanSettingKey extends SettingKey<Boolean> {
//  public BooleanSettingKey(String id, Boolean defaultValue) {
//    super(id, defaultValue);
//  }
//
//  @Override
//  public Boolean parse(String data) throws ParseSettingException {
//    switch (data.toLowerCase()) {
//      case "true":
//      case "t":
//        return true;
//      case "false":
//      case "f":
//        return false;
//      default:
//        throw new ParseSettingException("Allowed values: t, true, f, false");
//    }
//  }
//
//  @Override
//  public List<String> options() {
//    return Lists.newArrayList("true", "false", "t", "f");
//  }
//}
