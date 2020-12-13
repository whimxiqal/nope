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
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.flags.Flag;
import com.minecraftonline.nope.control.flags.FlagUtil;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class FlagValueCommandElement extends CommandElement {
  protected FlagValueCommandElement(@Nullable Text key) {
    super(key);
  }

  @Nullable
  @Override
  protected FlagValueWrapper<?> parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
    String settingName = args.next();
    Setting<Flag<?>> flagSetting = Settings.REGISTRY_MODULE.getById(settingName)
        .filter(setting -> setting.getDefaultValue() instanceof Flag)
        .map(setting -> (Setting<Flag<?>>)setting)
        .orElseThrow(() -> new ArgumentParseException(Text.of("No region flag with the name: '" + settingName + "'"), settingName, settingName.length()));
    String strValue = args.next();
    Object value = flagSetting.getDefaultValue().deserializeIngame(strValue);
    if (value == null) {
      throw new ArgumentParseException(Text.of(strValue + " is not a valid value for this flag!"), strValue, settingName.length() + strValue.length() + 1); // 1 space
    }
    Flag<?> flag = FlagUtil.makeFlag(flagSetting.getDefaultValue(), value);
    if (args.hasNext()) {
      String targetGroup = args.next();
      try {
        flag.setGroup(Flag.TargetGroup.valueOf(targetGroup.toUpperCase()));
      } catch (IllegalArgumentException e) {
        throw new ArgumentParseException(Text.of("Invalid Target Group"), targetGroup, settingName.length() + strValue.length() + targetGroup.length() + 2); // 2 for spaces
      }
    }

    return FlagValueWrapper.makeFlagValueWrapper(flagSetting, flag);
  }

  @Override
  public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
    try {
      if (args.hasNext() && args.next().isEmpty()) {
        return ArgsUtil.getFlagSettings().stream()
            .map(Setting::getId)
            .collect(Collectors.toList());
      }
    } catch (ArgumentParseException e) {
      e.printStackTrace();
    }
    return Lists.newArrayList();
  }
}
