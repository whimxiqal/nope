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

package com.minecraftonline.nope.common.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.minecraftonline.nope.common.struct.Location;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * A setting to store a {@link Location} as a value.
 */
public final class LocationSettingKey extends SettingKey<Location> {

  public LocationSettingKey(String id, Location location) {
    super(id, location);
  }

  @Override
  public JsonElement dataToJsonGenerified(Location data) {
    return new JsonPrimitive(String.join(", ", new String[]{
        String.valueOf(data.getPosX()),
        String.valueOf(data.getPosY()),
        String.valueOf(data.getPosZ()),
        data.getWorldUuid().toString()
    }));
  }

  @Override
  public Location dataFromJsonGenerified(JsonElement json) throws ParseSettingException {
    return parse(json.getAsString());
  }

  @NotNull
  @Override
  public String print(Location data) {
    return "world:" + data.getWorldUuid().toString()
        + ", "
        + "x:" + data.getPosX()
        + ", "
        + "y:" + data.getPosY()
        + ", "
        + "z:" + data.getPosZ();
  }

  @Override
  public Location parse(String data) throws ParseSettingException {
    String[] tokens = data.split(SettingLibrary.SET_SPLIT_REGEX);
    if (tokens.length != 4) {
      throw new ParseSettingException("This requires exactly 4 arguments: world and position");
    }
    try {
      return new Location(Integer.parseInt(tokens[0]),
          Integer.parseInt(tokens[1]),
          Integer.parseInt(tokens[2]),
          UUID.fromString(tokens[3]));
    } catch (NumberFormatException e) {
      throw new ParseSettingException("Numbers could not be parsed.");
    }
  }

}
