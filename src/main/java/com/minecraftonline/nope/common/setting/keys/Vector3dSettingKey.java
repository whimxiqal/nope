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

package com.minecraftonline.nope.common.setting.keys;

import com.google.gson.JsonObject;
import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingKeys;
import com.minecraftonline.nope.common.math.Vector3d;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A setting key that stores a {@link Vector3d} as a value.
 */
public class Vector3dSettingKey extends SettingKey<Vector3d> {

  public Vector3dSettingKey(String id, @Nullable Vector3d defaultData, @NotNull Class<Vector3d> type) {
    super(id, defaultData, type);
  }

  @Override
  public Object serializeDataGenerified(Vector3d value) {
    final JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("x", value.posX());
    jsonObject.addProperty("y", value.posY());
    jsonObject.addProperty("z", value.posZ());
    return jsonObject;
  }

  @Override
  public Vector3d deserializeDataGenerified(Object serialized) {
    final Map<String, Double> map = (Map<String, Double>) serialized;
    return Vector3d.of(
        map.get("x"),
        map.get("y"),
        map.get("z")
    );
  }

  @Override
  public Vector3d parse(String s) throws ParseSettingException {
    String[] parts = s.split(SettingKeys.SET_SPLIT_REGEX, 3);
    if (parts.length != 3) {
      throw new ParseSettingException("Expected 3 parts for Vector3d, got " + parts.length);
    }
    int i = 0;
    try {
      double x = Double.parseDouble(parts[i++]);
      double y = Double.parseDouble(parts[i++]);
      double z = Double.parseDouble(parts[i]);
      if (Math.max(Math.abs(x), Math.abs(z)) > Nope.WORLD_RADIUS
          || Math.abs(y) > Nope.WORLD_DEPTH) {
        throw new ParseSettingException("The magnitudes of these numbers are too high!");
      }
      return Vector3d.of(x, y, z);
    } catch (NumberFormatException e) {
      throw new ParseSettingException("Value at position " + i + ", "
          + "could not be parsed into a double");
    }
  }

  @NotNull
  @Override
  public String print(@NotNull Vector3d data) {
    if (data == null) {
      return "null";
    }
    return "x:" + data.posX() + ", y: " + data.posY() + ", z: " + data.posZ();
  }
}
