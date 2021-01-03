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
//import com.google.common.collect.Sets;
//import com.minecraftonline.nope.util.NopeTypeTokens;
//
//import java.util.Set;
//
//public class FlagStringSet extends Flag<Set<String>> {
//  public FlagStringSet(Set<String> value) {
//    super(value,  NopeTypeTokens.STRING_SET_TOKEN); // TODO: check this works correctly
//  }
//
//  public FlagStringSet(Set<String> value, TargetGroup group) {
//    super(value, NopeTypeTokens.STRING_SET_TOKEN, group);
//  }
//
//  @Override
//  public String serialize(Flag<Set<String>> flag) {
//    StringBuilder builder = new StringBuilder("{");
//    flag.getValue().forEach(name -> builder.append(" ").append(name).append(","));
//    return builder.deleteCharAt(builder.length() - 1).append(" }").toString();
//  }
//
//  @Override
//  public Set<String> parseValue(String s) {
//    return Sets.newHashSet(s.split(","));
//  }
//}
