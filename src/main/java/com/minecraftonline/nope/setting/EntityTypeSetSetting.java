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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityTypeSetSetting extends SetSetting<EntityType> {
  public EntityTypeSetSetting(String id, Set<EntityType> defaultValue) {
    super(id, defaultValue);
  }

  @Override
  protected JsonElement elementToJsonGenerified(EntityType element) {
    return new JsonPrimitive(element.getId());
  }

  @Override
  protected EntityType elementFromJsonGenerified(JsonElement jsonElement) {
    return Sponge.getRegistry()
        .getType(EntityType.class, jsonElement.getAsString())
        .orElseThrow(() -> new ParseSettingException("Unknown EntityType: " + jsonElement.getAsString()));
  }

  @Override
  public Set<EntityType> parse(String s) throws ParseSettingException {
    return stringsToEntityTypes(Arrays.asList(s.split(SettingLibrary.SET_SPLIT_REGEX)));
  }

  private Set<EntityType> stringsToEntityTypes(Collection<String> strings) {
    Set<EntityType> set = new HashSet<>();
    for (String s : strings) {
      final EntityType entityType = Sponge.getRegistry()
          .getType(EntityType.class, s)
          .orElseThrow(() -> new ParseSettingException("Unknown EntityType: " + s));
      set.add(entityType);
    }
    return set;
  }

  @Nonnull
  @Override
  public Text printElement(EntityType element) {
    return Format.hover(element.getName(), element.getId());
  }
}
