/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
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

package com.minecraftonline.nope.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.spongepowered.api.entity.EntityType;

import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class NopeTypeTokens {

  private NopeTypeTokens() {}

  public static final TypeToken<Set<String>> STRING_SET_TOKEN = new TypeToken<Set<String>>() {private static final long serialVersionUID = -1;};
  public static final TypeToken<Set<EntityType>> ENTITY_TYPE_SET_TOKEN = new TypeToken<Set<EntityType>>() {private static final long serialVersionUID = -1;};

  public static final TypeToken<JsonElement> JSON_ELEMENT_TYPE_TOKEN = new TypeToken<JsonElement>() {};
  public static final TypeToken<JsonPrimitive> JSON_PRIMITIVE_TYPE_TOKEN = new TypeToken<JsonPrimitive>() {};
}