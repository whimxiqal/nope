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

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;

public final class LocationSetting extends SettingKey<Location<World>> {

  public LocationSetting(String id, Location<World> location) {
    super(id, location);
  }

  @Override
  public JsonElement dataToJsonGenerified(Location<World> data) {
    return new JsonPrimitive(String.join(", ", new String[]{
        data.getExtent().getName(),
        String.valueOf(data.getX()),
        String.valueOf(data.getY()),
        String.valueOf(data.getZ())}));
  }

  @Override
  public Location<World> dataFromJsonGenerified(JsonElement json) throws ParseSettingException {
    return parse(json.getAsString());
  }

  @Nonnull
  @Override
  public Text print(Location<World> data) {
    return Text.of(Format.keyValue("world:", data.getExtent().getName()),
        ", ",
        Format.keyValue("x:", String.valueOf(data.getX())),
        ", ",
        Format.keyValue("y:", String.valueOf(data.getY())),
        ", ",
        Format.keyValue("z:", String.valueOf(data.getZ())));
  }

  @Override
  public Location<World> parse(String data) throws ParseSettingException {
    String[] tokens = data.split(SettingLibrary.SET_SPLIT_REGEX);
    if (tokens.length != 4) throw new ParseSettingException("This requires exactly 4 arguments: world and position");
    try {
      return new Location<>(Sponge.getServer()
          .getWorld(tokens[0])
          .orElseThrow(() -> new ParseSettingException("The world " + tokens[0] + " doesn't exist")),
          Integer.parseInt(tokens[1]),
          Integer.parseInt(tokens[2]),
          Integer.parseInt(tokens[3]));
    } catch (NumberFormatException e) {
      throw new ParseSettingException("Numbers could not be parsed.");
    }
  }

}
