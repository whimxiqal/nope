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

package com.minecraftonline.nope.sponge.command.settingcollection.blank;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.struct.Named;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.util.Formatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.scheduler.Task;

public class InfoCommand<T extends SettingCollection & Named> extends CommandNode {

  private final Parameter.Key<T> settingCollectionParameterKey;
  private final String collectionName;
  private final TextColor collectionPrimaryTheme;
  private final TextColor collectionSecondaryTheme;
  private final Function<T, Collection<Component>> extraInfo;

  public InfoCommand(CommandNode parent,
                     Parameter.Key<T> settingCollectionParameterKey,
                     String collectionName,
                     TextColor collectionPrimaryTheme,
                     TextColor collectionSecondaryTheme,
                     Function<T, Collection<Component>> extraInfo) {
    super(parent, Permissions.EDIT,
        "Get info on a " + collectionName,
        "info");
    this.settingCollectionParameterKey = settingCollectionParameterKey;
    this.collectionName = collectionName;
    this.collectionPrimaryTheme = collectionPrimaryTheme;
    this.collectionSecondaryTheme = collectionSecondaryTheme;
    this.extraInfo = extraInfo;
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    T collection = context.requireOne(settingCollectionParameterKey);

    Component header = Component.join(Component.newline(), extraInfo.apply(collection))
        .append(Component.newline())
        .append(Component.newline())
        .append(Component.text()
            .append(Component.text("Settings")
                .color(Formatter.DULL)
                .decorate(TextDecoration.UNDERLINED))
            .build());
    Sponge.asyncScheduler().submit(Task.builder().execute(() ->
        Sponge.serviceProvider().paginationService().builder()
            .title(Component.text(collectionName + ": ").color(Formatter.GOLD)
                .append(Component.text(collection.name()).color(collectionSecondaryTheme)))
            .padding(Component.text("=").color(collectionPrimaryTheme))
            .header(header)
            .contents(collection.settings().isEmpty()
                ? Collections.singleton(Component.text("None").color(Formatter.DULL))
                : collection.settings().stream()
                .flatMap(setting -> {
                  try {
                    return Formatter.setting(setting, context.cause().subject(), collection)
                        .get()
                        .stream();
                  } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    return Stream.empty();
                  }
                }).collect(Collectors.toList()))
            .sendTo(context.cause().audience()))
        .plugin(SpongeNope.instance().pluginContainer())
        .build());

    return CommandResult.success();
  }

}
