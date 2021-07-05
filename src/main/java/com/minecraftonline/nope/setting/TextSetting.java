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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.minecraftonline.nope.Nope;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class TextSetting extends SettingKey<Text> {

  protected TextSetting(String id, Text defaultValue) {
    super(id, defaultValue);
  }

  @Override
  protected JsonElement dataToJsonGenerified(Text data) {
    try {
      return new Gson().toJsonTree(DataFormats.JSON.write(data.toContainer()));
    } catch (IOException e) {
      Nope.getInstance().getLogger().error("Could not serialize Text", e);
      return new Gson().toJsonTree("");
    }
  }

  @Override
  public Text dataFromJsonGenerified(JsonElement json) {
    try {
      return Sponge.getDataManager()
          .deserialize(Text.class, DataFormats.JSON.read(json.getAsString()))
          .orElseThrow(() -> new RuntimeException(
              "The json for Text cannot be serialized: "
                  + json.toString()));
    } catch (IllegalStateException | IOException e) {
      Nope.getInstance().getLogger().error("Could not deserialize Text", e);
      return Text.EMPTY;
    }
  }

  @Nonnull
  @Override
  public Text print(Text data) {
    return data;
  }

  @Override
  public Text parse(String s) throws ParseSettingException {
    return TextSerializers.FORMATTING_CODE.deserialize(s);
  }
}
