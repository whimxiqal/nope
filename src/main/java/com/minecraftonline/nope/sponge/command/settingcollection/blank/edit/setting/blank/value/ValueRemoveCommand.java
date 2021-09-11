/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) 2021 Pieter Svenson
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package com.minecraftonline.nope.sponge.command.settingcollection.blank.edit.setting.blank.value;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.keys.SetSettingKey;
import com.minecraftonline.nope.common.struct.Named;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.util.Formatter;
import java.util.HashSet;
import java.util.Set;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

public class ValueRemoveCommand<T extends SettingCollection & Named> extends CommandNode {

  private final Parameter.Key<T> settingCollectionParameterKey;

  public ValueRemoveCommand(CommandNode parent,
                            Parameter.Key<T> settingCollectionParameterKey,
                            String collectionName) {
    super(parent, Permissions.EDIT,
        "Remove a value from a setting which stores a set of values",
        "remove");
    this.settingCollectionParameterKey = settingCollectionParameterKey;
    addParameter(Parameters.SETTING_DATA);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    T collection = context.requireOne(settingCollectionParameterKey);
    SettingKey<?> key = context.requireOne(ParameterKeys.SETTING_KEY);
    String data = context.requireOne(ParameterKeys.SETTING_DATA);

    if (!(key instanceof SetSettingKey)) {
      return CommandResult.error(Formatter.error(
          "You may not remove values from setting key ___ because it does not store multiple values. Use the ___ command instead.",
          key.id(), "value set"
      ));
    }

    return removeData(context, collection, (SetSettingKey<?>) key, data);

  }

  private <X, S extends SettingCollection & Named> CommandResult removeData(CommandContext context,
                                                                            S collection,
                                                                            SetSettingKey<X> key,
                                                                            String input) {
    Set<X> currentSetData = collection.computeData(key, HashSet::new);
    Set<X> inputSetData;
    try {
      inputSetData = key.parse(input);
    } catch (SettingKey.ParseSettingException e) {
      return CommandResult.error(Formatter.error(
          "Value ___ could not be parsed for key ___", input, key.id()
      ));
    }

    for (X value : inputSetData) {
      if (!currentSetData.remove(value)) {
        context.cause().audience().sendMessage(Formatter.error(
            "Value ___ could not be removed", value
        ));
      }
    }

    context.cause().audience().sendMessage(Formatter.success(
        "Value of ___ on ___ is now set to ___",
        key.id(), collection.name(), printData(collection, key)
    ));
    return CommandResult.success();
  }

  private <X> String printData(SettingCollection collection, SettingKey<X> key) {
    return key.print(collection.requireData(key));
  }
}
