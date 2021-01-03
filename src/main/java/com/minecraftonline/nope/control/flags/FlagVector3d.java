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
// */
//
//package com.minecraftonline.nope.control.flags;
//
//import com.flowpowered.math.vector.Vector3d;
//import org.spongepowered.api.util.TypeTokens;
//
//public class FlagVector3d extends Flag<Vector3d> {
//  public FlagVector3d(Vector3d value) {
//    super(value, TypeTokens.VECTOR_3D_TOKEN);
//  }
//
//  public FlagVector3d(Vector3d value, TargetGroup group) {
//    super(value, TypeTokens.VECTOR_3D_TOKEN, group);
//  }
//
//  @Override
//  public Vector3d parseValue(String s) {
//    String[] strings = s.split(",");
//    if (strings.length != 3) {
//      return null;
//    }
//    try {
//      return new Vector3d(
//          Double.parseDouble(strings[0]),
//          Double.parseDouble(strings[1]),
//          Double.parseDouble(strings[2])
//      );
//    } catch (NumberFormatException e) {
//      return null;
//    }
//  }
//}
