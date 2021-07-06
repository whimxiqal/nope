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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;

/**
 * A setting to store a value which is a {@link CatalogType}.
 * with the Sponge API
 *
 * @param <C> catalog type
 */
public class CatalogTypeSettingKey<C extends CatalogType> extends SettingKey<String> {
  private final Class<C> clazz;

  public CatalogTypeSettingKey(String id, C defaultData, Class<C> clazz) {
    super(id, defaultData.getId());
    this.clazz = clazz;
  }

  @Override
  public String parse(String id) throws ParseSettingException {
    Sponge.getRegistry().getType(this.clazz, id).orElseThrow(() ->
        new ParseSettingException("The given id "
            + id
            + " id not a valid "
            + this.clazz.getSimpleName()));
    return id;
  }

  @Override
  public Optional<List<String>> getParsable() {
    return Optional.of(Lists.newArrayList(Sponge.getRegistry()
        .getAllOf(this.clazz)
        .stream()
        .map(CatalogType::getName)
        .collect(Collectors.toList())));
  }
}
