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
//import com.google.gson.JsonPrimitive;
//import com.minecraftonline.nope.common.Nope;
//import com.minecraftonline.nope.common.setting.SettingKey;
//import com.minecraftonline.nope.common.setting.SettingKeys;
//import com.minecraftonline.nope.common.struct.Location;
//import org.jetbrains.annotations.NotNull;
//
///**
// * A setting to store a {@link Location} as a value.
// */
//public final class LocationSettingKey extends SettingKey<Location> {
//
//  public LocationSettingKey(String id, Location location) {
//    super(id, location);
//  }
//
//  @Override
//  public Object serializeDataGenerified(Location data) {
//    return new JsonPrimitive(String.join(", ", new String[]{
//        String.valueOf(data.posX()),
//        String.valueOf(data.posY()),
//        String.valueOf(data.posZ()),
//        data.domain().id()
//    }));
//  }
//
//  @Override
//  public Location deserializeDataGenerified(Object serialized) throws ParseSettingException {
//    return parse((String) serialized);
//  }
//
//  @NotNull
//  @Override
//  public String print(@NotNull Location data) {
//    return "world:" + data.domain().id()
//        + ", "
//        + "x:" + data.posX()
//        + ", "
//        + "y:" + data.posY()
//        + ", "
//        + "z:" + data.posZ();
//  }
//
//  @Override
//  public Location parse(String data) throws ParseSettingException {
//    String[] tokens = data.split(SettingKeys.SET_SPLIT_REGEX);
//    if (tokens.length != 4) {
//      throw new ParseSettingException("This requires exactly 4 arguments: world and position");
//    }
//    try {
//      return new Location(Integer.parseInt(tokens[0]),
//          Integer.parseInt(tokens[1]),
//          Integer.parseInt(tokens[2]),
//          Nope.instance().hostSystem().domain(tokens[3]));
//    } catch (NumberFormatException e) {
//      throw new ParseSettingException("Numbers could not be parsed.");
//    }
//  }
//
//}