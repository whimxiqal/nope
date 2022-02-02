/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

///*
// *
// *  * MIT License
// *  *
// *  * Copyright (c) 2021 Pieter Svenson
// *  *
// *  * Permission is hereby granted, free of charge, to any person obtaining a copy
// *  * of this software and associated documentation files (the "Software"), to deal
// *  * in the Software without restriction, including without limitation the rights
// *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// *  * copies of the Software, and to permit persons to whom the Software is
// *  * furnished to do so, subject to the following conditions:
// *  *
// *  * The above copyright notice and this permission notice shall be included in all
// *  * copies or substantial portions of the Software.
// *  *
// *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// *  * SOFTWARE.
// *
// */
//
//package com.minecraftonline.nope.sponge.listenerold.dynamic;
//
//import com.google.common.collect.Lists;
//import com.minecraftonline.nope.sponge.listenerold.SettingListener;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class DynamicHandler {
//
//  private static final Set<SettingListener<?>> registered = new HashSet<>();
//
//  private static final List<SettingListener<?>> listeners = Lists.newArrayList(
//      new ArmorStandDestroyListener(),
//      new ArmorStandInteractListener(),
//      new ArmorStandPlaceListener()
//  );
//
//  public static void register() {
//    listeners.forEach(listener -> {
//      if (!registered.contains(listener)) {
//        if (listener.registerIfAssigned()) {
//          registered.add(listener);
//        }
//      }
//    });
//  }
//
//}
