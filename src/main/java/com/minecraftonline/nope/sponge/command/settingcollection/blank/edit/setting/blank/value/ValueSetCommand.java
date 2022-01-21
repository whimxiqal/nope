///*
// *
// *  * MIT License
// *  *
// *  * Copyright (c) 2021 Pieter Svenson
// *  *
// *  * Permission is hereby granted, free of charge, to any person obtaining a copy
// *  * of this software and associated documentation files (the "Software"), to deal
// *  * in the Software without restriction, including without limitation the rights
// *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// *  * copies of the Software, and to permit persons to whom the Software is
// *  * furnished to do so, subject to the following conditions:
// *  *
// *  * The above copyright notice and this permission notice shall be included in all
// *  * copies or substantial portions of the Software.
// *  *
// *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// *  * SOFTWARE.
// *
// */
//
//package com.minecraftonline.nope.sponge.command.settingcollection.blank.edit.setting.blank.value;
//
//import com.minecraftonline.nope.common.permission.Permissions;
//import com.minecraftonline.nope.common.setting.SettingCollection;
//import com.minecraftonline.nope.common.setting.SettingKey;
//import com.minecraftonline.nope.common.setting.SettingValue;
//import com.minecraftonline.nope.common.struct.Named;
//import com.minecraftonline.nope.sponge.command.CommandNode;
//import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
//import com.minecraftonline.nope.sponge.command.parameters.Parameters;
//import com.minecraftonline.nope.sponge.util.Formatter;
//import org.spongepowered.api.command.CommandResult;
//import org.spongepowered.api.command.exception.CommandException;
//import org.spongepowered.api.command.parameter.CommandContext;
//import org.spongepowered.api.command.parameter.Parameter;
//
//public class ValueSetCommand<T extends SettingCollection & Named> extends CommandNode {
//
//  private final Parameter.Key<T> settingCollectionParameterKey;
//
//  public ValueSetCommand(CommandNode parent,
//                      Parameter.Key<T> settingCollectionParameterKey,
//                      String collectionName) {
//    super(parent, Permissions.EDIT,
//        "Set a permission on the target of a " + collectionName,
//        "set");
//    this.settingCollectionParameterKey = settingCollectionParameterKey;
//  }
//
//  @Override
//  public CommandResult execute(CommandContext context) throws CommandException {
//    T collection = context.requireOne(settingCollectionParameterKey);
//    SettingKey<?, ?> key = context.requireOne(ParameterKeys.SETTING_KEY);
//    String data = context.requireOne(ParameterKeys.SETTING_VALUE);
//    try {
//      collection.setValueUnchecked(key, key.manager().parseDeclarativeValue(data));
//    } catch (SettingKey.ParseSettingException e) {
//      return CommandResult.error(Formatter.error(
//          "Value ___ could not be parsed for key ___", data, key.id()
//      ));
//    }
//    context.cause().audience().sendMessage(Formatter.success(
//        "Value of ___ on ___ is now set to ___",
//        key.id(), collection.name(), printData(collection, key)
//    ));
////    DynamicHandler.register();
//    return CommandResult.success();
//  }
//
//  private <X, Y extends SettingValue<X>> String printData(SettingCollection collection, SettingKey<X, Y> key) {
//    return key.manager().printValue(collection.requireValue(key));
//  }
//}
