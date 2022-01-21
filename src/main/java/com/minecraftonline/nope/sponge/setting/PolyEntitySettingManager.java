/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Pieter Svenson
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

package com.minecraftonline.nope.sponge.setting;

import com.minecraftonline.nope.common.setting.SettingKey;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.registry.RegistryEntry;

public class PolyEntitySettingManager extends SettingKey.Manager.Poly<String> {

  @Override
  public @NotNull Map<String, Object> elementOptions() {
    return EntityTypes.registry().streamEntries()
        .collect(Collectors.<RegistryEntry<EntityType<? extends Entity>>, String, Object>
            toMap(entity -> entity.key().value(), entity -> entity.value().asComponent()));
  }

  @Override
  public String printElement(String element) {
    return element;
  }

  @Override
  public String parseElement(String element) throws SettingKey.ParseSettingException {
    return EntityTypes.registry().streamEntries()
        .map(entity -> entity.key().value())
        .filter(name -> name.equalsIgnoreCase(element))
        .findFirst()
        .orElseThrow(() -> new SettingKey.ParseSettingException("No entity found called " + element))
        .toLowerCase();
  }
}
