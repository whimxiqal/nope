/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

package com.minecraftonline.nope.sponge.command.settingcollection.blank.info.setting.blank;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.struct.Named;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import net.kyori.adventure.text.format.TextColor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

public class ValueInfoCommand<T extends SettingCollection & Named> extends CommandNode {

  private final Parameter.Key<T> settingCollectionParameterKey;
  private final String collectionName;
  private final TextColor collectionPrimaryTheme;
  private final TextColor collectionSecondaryTheme;

  public ValueInfoCommand(CommandNode parent,
                            Parameter.Key<T> settingCollectionParameterKey,
                            String collectionName,
                            TextColor collectionPrimaryTheme,
                            TextColor collectionSecondaryTheme) {
    super(parent, Permissions.EDIT,
        "Get info on the value of a setting of a " + collectionName,
        "value");
    this.settingCollectionParameterKey = settingCollectionParameterKey;
    this.collectionName = collectionName;
    this.collectionPrimaryTheme = collectionPrimaryTheme;
    this.collectionSecondaryTheme = collectionSecondaryTheme;
    prefix(Parameters.SETTING_KEY);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    T collection = context.requireOne(settingCollectionParameterKey);

    return CommandResult.success();
  }
}
