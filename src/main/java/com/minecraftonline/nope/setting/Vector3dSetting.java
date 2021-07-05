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

package com.minecraftonline.nope.setting;

import com.flowpowered.math.vector.Vector3d;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.util.Format;
import javax.annotation.Nonnull;
import org.spongepowered.api.text.Text;

public class Vector3dSetting extends SettingKey<Vector3d> {
  public Vector3dSetting(String id, Vector3d defaultValue) {
    super(id, defaultValue);
  }

  @Override
  public JsonElement dataToJsonGenerified(Vector3d value) {
    final JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("x", value.getX());
    jsonObject.addProperty("y", value.getY());
    jsonObject.addProperty("z", value.getZ());
    return jsonObject;
  }

  @Override
  public Vector3d dataFromJsonGenerified(JsonElement jsonElement) {
    final JsonObject jsonObject = jsonElement.getAsJsonObject();
    return Vector3d.from(
        jsonObject.get("x").getAsDouble(),
        jsonObject.get("y").getAsDouble(),
        jsonObject.get("z").getAsDouble()
    );
  }

  @Override
  public Vector3d parse(String s) throws ParseSettingException {
    String[] parts = s.split(SettingLibrary.SET_SPLIT_REGEX, 3);
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
      return Vector3d.from(x, y, z);
    } catch (NumberFormatException e) {
      throw new ParseSettingException("Value at position " + i + ", "
          + "could not be parsed into a double");
    }
  }

  @Nonnull
  @Override
  public Text print(Vector3d data) {
    return Text.of(Format.keyValue("x:", String.valueOf(data.getX())),
        ", ",
        Format.keyValue("y:", String.valueOf(data.getY())),
        ", ",
        Format.keyValue("z:", String.valueOf(data.getZ())));
  }
}
