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

package com.minecraftonline.nope.sponge.command.settingcollection.blank.edit.setting.blank;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingValue;
import com.minecraftonline.nope.common.struct.AltSet;
import com.minecraftonline.nope.common.struct.Named;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.Flags;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.ParameterValueTypes;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.util.Formatter;
import java.util.Optional;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

public class ValueCommand<T extends SettingCollection & Named> extends CommandNode {

  private final Parameter.Key<T> settingCollectionParameterKey;
  private final String collectionName;

  public ValueCommand(CommandNode parent,
                      Parameter.Key<T> settingCollectionParameterKey,
                      String collectionName) {
    super(parent, Permissions.EDIT,
        "Edit the value of a setting on a " + collectionName,
        "value");
    this.settingCollectionParameterKey = settingCollectionParameterKey;
    this.collectionName = collectionName;
    prefix(Parameters.SETTING_KEY);
    addParameter(Parameter.seqBuilder(Parameters.SETTING_VALUE_ALTER_TYPE)
        .then(Parameters.SETTING_VALUE)
        .build());
    addFlag(Flags.ADDITIVE_VALUE_FLAG);
    addFlag(Flags.SUBTRACTIVE_VALUE_FLAG);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    SettingKey<?, ?, ?> settingKey = context.requireOne(ParameterKeys.SETTING_KEY);
    return executeGenerified(settingKey, context);
  }

  private <X, Y extends SettingValue<X>> CommandResult executeGenerified(SettingKey<X, Y, ?> settingKey, CommandContext context) {
    T collection = context.requireOne(settingCollectionParameterKey);
    ParameterValueTypes.SettingValueAlterType alterType = context.requireOne(ParameterKeys.SETTING_VALUE_ALTER_TYPE);
    Optional<String> settingValue = context.one(ParameterKeys.SETTING_VALUE);
    boolean additive = context.hasFlag(Flags.ADDITIVE_VALUE_FLAG);
    boolean subtractive = context.hasFlag(Flags.SUBTRACTIVE_VALUE_FLAG);

    if (settingKey.global() && !collection.equals(SpongeNope.instance().hostSystem().universe())) {
      context.sendMessage(Identity.nil(),
          Formatter.error("This setting may only be set on the host ___",
              SpongeNope.instance().hostSystem().universe().name()));
      return CommandResult.success();
    }

    if (settingKey instanceof SettingKey.Poly) {
      SettingKey.Poly<?, ?> polyKey = (SettingKey.Poly<?, ?>) settingKey;
      return executeWithPolyKey(polyKey, context, collection, alterType, settingValue, additive, subtractive);
    } else if (additive || subtractive) {
      return CommandResult.error(Formatter.error("You may not alter this value in an additive or subtractive way"));
    } else {
      if (!settingValue.isPresent()) {
        return CommandResult.error(Formatter.error("You must provide a value to update this setting"));
      }
      Y newValue = settingKey.manager().parseDeclarativeValue(settingValue.get());
      return success(context, collection, settingKey, newValue);
    }
  }

  private <X, Y extends AltSet<X>> CommandResult executeWithPolyKey(SettingKey.Poly<X, Y> polyKey,
                                                                    CommandContext context,
                                                                    T collection,
                                                                    ParameterValueTypes.SettingValueAlterType alterType,
                                                                    Optional<String> settingValueOptional,
                                                                    boolean additive,
                                                                    boolean subtractive) {
    try {
      if (additive && subtractive) {
        return CommandResult.error(Formatter.error("You may not set both additive "
            + "and subtractive values at the same time"));
      }
      Y inputSet = polyKey.manager().createSet();
      if (alterType == ParameterValueTypes.SettingValueAlterType.ALL) {
        inputSet.fill();
        alterType = ParameterValueTypes.SettingValueAlterType.SET;
      } else if (alterType == ParameterValueTypes.SettingValueAlterType.NONE) {
        alterType = ParameterValueTypes.SettingValueAlterType.SET;
      } else {
        if (settingValueOptional.isPresent()) {
          inputSet.addAll(polyKey.manager().parseSet(settingValueOptional.get()));
        } else {
          return CommandResult.error(Formatter.error("You must specify values to update in this way"));
        }
      }
      Optional<SettingValue.Poly<X, Y>> currentValue = collection.getValue(polyKey);
      Y additiveSet;
      Y subtractiveSet;
      SettingValue.Poly<X, Y> newValue;
      if (additive) {
        switch (alterType) {
          case SET:
          case SET_NOT:
            if (currentValue.isPresent() && currentValue.get().declarative()) {
              context.sendMessage(Identity.nil(),
                  Formatter.warn("The setting value type was changed to ___", "manipulative"));
            }
            newValue = SettingValue.Poly
                .manipulative(inputSet, currentValue.filter(SettingValue.Poly::manipulative)
                    .map(SettingValue.Poly::subtractive)
                    .orElse(polyKey.manager().createSet()));
            break;
          case CONCATENATE:
            additiveSet = polyKey.manager().createSet();
            if (currentValue.isPresent()) {
              if (currentValue.get().declarative()) {
                context.sendMessage(Identity.nil(),
                    Formatter.warn("The setting value type was changed to ___", "manipulative"));
              } else {
                additiveSet = currentValue.get().additive();
              }
            }
            additiveSet.addAll(inputSet);
            newValue = SettingValue.Poly
                .manipulative(additiveSet, currentValue.filter(SettingValue.Poly::manipulative)
                    .map(SettingValue.Poly::subtractive)
                    .orElse(polyKey.manager().createSet()));
            break;
          case REMOVE:
            if (!currentValue.isPresent()
                || currentValue.get().declarative()
                || (currentValue.get().manipulative() && currentValue.get().additive().isEmpty())) {
              context.sendMessage(Identity.nil(),
                  Formatter.warn("There is nothing to remove."));
              return CommandResult.success();
            }
            additiveSet = currentValue.get().additive();
            additiveSet.removeAll(inputSet);
            newValue = SettingValue.Poly.manipulative(additiveSet,
                currentValue.filter(SettingValue.Poly::manipulative)
                    .map(SettingValue.Poly::subtractive)
                    .orElse(polyKey.manager().createSet()));
            break;
          default:
            throw new CommandException(Formatter.error("Unknown setting value alter type: " + alterType));
        }
      } else if (subtractive) {
        switch (alterType) {
          case SET:
          case SET_NOT:
            if (currentValue.isPresent() && currentValue.get().declarative()) {
              context.sendMessage(Identity.nil(),
                  Formatter.warn("The setting value type was changed to ___", "manipulative"));
            }
            newValue = SettingValue.Poly.manipulative(currentValue.map(SettingValue.Poly::additive)
                .orElse(polyKey.manager().createSet()), inputSet);
            break;
          case CONCATENATE:
            subtractiveSet = polyKey.manager().createSet();
            if (currentValue.isPresent()) {
              if (currentValue.get().declarative()) {
                context.sendMessage(Identity.nil(),
                    Formatter.warn("The setting value type was changed to ___", "manipulative"));
              } else {
                subtractiveSet = currentValue.get().subtractive();
              }
            }
            subtractiveSet.addAll(inputSet);
            newValue = SettingValue.Poly.manipulative(currentValue.map(SettingValue.Poly::additive)
                .orElse(polyKey.manager().createSet()), subtractiveSet);
            break;
          case REMOVE:
            if (!currentValue.isPresent()
                || currentValue.get().declarative()
                || (currentValue.get().manipulative() && currentValue.get().subtractive().isEmpty())) {
              context.sendMessage(Identity.nil(),
                  Formatter.warn("There is nothing to remove."));
              return CommandResult.success();
            }
            subtractiveSet = currentValue.get().subtractive();
            subtractiveSet.removeAll(inputSet);
            newValue = SettingValue.Poly.manipulative(currentValue.get().additive(), subtractiveSet);
            break;
          default:
            throw new CommandException(Formatter.error("Unknown setting value alter type: " + alterType));
        }
      } else {
        // declarative
        switch (alterType) {
          case SET:
          case SET_NOT:
            if (currentValue.isPresent() && currentValue.get().manipulative()) {
              context.sendMessage(Identity.nil(),
                  Formatter.warn("The setting value type was changed to ___", "declarative"));
            }
            newValue = SettingValue.Poly.declarative(inputSet);
            break;
          case CONCATENATE:
            additiveSet = polyKey.manager().createSet();
            if (currentValue.isPresent()) {
              if (currentValue.get().manipulative()) {
                context.sendMessage(Identity.nil(),
                    Formatter.warn("The setting value type was changed to ___", "declarative"));
              } else {
                additiveSet = currentValue.get().additive();
              }
            }
            additiveSet.addAll(inputSet);
            newValue = SettingValue.Poly.declarative(additiveSet);
            break;
          case REMOVE:
            if (!currentValue.isPresent()
                || currentValue.get().manipulative()
                || (currentValue.get().declarative() && currentValue.get().additive().isEmpty())) {
              context.sendMessage(Identity.nil(),
                  Formatter.warn("There is nothing to remove."));
              return CommandResult.success();
            }
            additiveSet = currentValue.get().additive();
            additiveSet.removeAll(inputSet);
            newValue = SettingValue.Poly.declarative(additiveSet);
            break;
          default:
            throw new CommandException(Formatter.error("Unknown setting value alter type: " + alterType));
        }
      }
      if (alterType == ParameterValueTypes.SettingValueAlterType.SET_NOT) {
        inputSet.invert();
      }
      return success(context, collection, polyKey, newValue);
    } catch (SettingKey.ParseSettingException | CommandException e) {
      return CommandResult.error(Formatter.error(e.getMessage()));
    }
  }

  private <X, Y extends SettingValue<X>> CommandResult success(CommandContext context,
                                                               T collection,
                                                               SettingKey<X, Y, ?> key,
                                                               Y value) {
    collection.setValue(key, value);
    context.sendMessage(Identity.nil(),
        Formatter.success("Set value of ___ on " + this.collectionName + " ___: ___",
            key.id(),
            collection.name(),
            key.manager().printValue(value)));
    if (!key.functional()) {
      context.sendMessage(Identity.nil(),
          Formatter.warn("This setting may not work yet so your change may have no effect."));
    }
    SpongeNope.instance().settingListeners().registerAll();
    return CommandResult.success();
  }
}
