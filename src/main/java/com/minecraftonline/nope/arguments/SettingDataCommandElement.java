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

package com.minecraftonline.nope.arguments;

import com.google.common.collect.Lists;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingLibrary;
import com.minecraftonline.nope.setting.SettingValue;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class SettingDataCommandElement extends CommandElement {
  protected SettingDataCommandElement(@Nullable Text key) {
    super(key);
  }

  @Nullable
  @Override
  protected SettingValue<?> parseValue(@Nonnull CommandSource source, CommandArgs args) throws ArgumentParseException {
    String keyId = args.next();
    SettingKey<?> key;
    try {
      key = SettingLibrary.lookup(keyId);
    } catch (NoSuchElementException e) {
      throw new ArgumentParseException(Text.of(e.getMessage()), e, keyId, 0);
    }

    String dataString = args.next();
    Object value;
    try {
      value = key.parse(dataString);
    } catch (SettingKey.ParseSettingException e) {
      throw new ArgumentParseException(Text.of(e.getMessage()), e, dataString, keyId.length() + 1);
    }

//    if (args.hasNext()) {
//      String targetGroup = args.next();
//      try {
//        flag.setGroup(Flag.TargetGroup.valueOf(targetGroup.toUpperCase()));
//      } catch (IllegalArgumentException e) {
//        throw new ArgumentParseException(Text.of("Invalid Target Group"), targetGroup, settingName.length() + data.length() + targetGroup.length() + 2); // 2 for spaces
//      }
//    }

    return SettingValue.of(value);
  }

  @Nonnull
  @Override
  public List<String> complete(@Nonnull CommandSource src, CommandArgs args, @Nonnull CommandContext context) {
    try {
      if (args.hasNext() && args.peek().isEmpty()) {
        return SettingLibrary.getAll()
                .stream()
                .map(SettingKey::getId)
                .collect(Collectors.toList());
      }
    } catch (ArgumentParseException e) {
      e.printStackTrace();
    }
    return Lists.newArrayList();
  }
}
