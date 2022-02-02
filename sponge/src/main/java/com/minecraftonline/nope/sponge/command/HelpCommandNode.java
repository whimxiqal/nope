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

package com.minecraftonline.nope.sponge.command;

import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.util.Formatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.JoinConfiguration;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationList;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * A command node that displays all the following possibilities of any command
 * in a formatted list.
 */
public class HelpCommandNode extends CommandNode {

  /**
   * Default constructor.
   *
   * @param parent the parent command
   */
  public HelpCommandNode(@NotNull CommandNode parent) {
    super(parent,
        parent.permission(),
        "Helpful info for this command",
        "help",
        false);
    addAliases("?");
    Objects.requireNonNull(parent);
  }

  @NotNull
  @Override
  public CommandResult execute(@NotNull CommandContext context) {
    CommandNode parent = this.parent();
    if (parent == null) {
      return CommandResult.error(Formatter.error("You cannot run the help command without a parent"));
    }

    PaginationList.Builder paginationBuilder = Sponge.serviceProvider()
        .paginationService()
        .builder()
        .padding(Component.text("=", Formatter.THEME))
        .title(Component.text("Help Menu", Formatter.GOLD));

    List<Component> headerLines = new LinkedList<>();
    headerLines.add(Formatter.keyValue("Usage", parent.fullCommand(context.cause())));
    headerLines.add(Formatter.keyValue("Desc", parent.description()));
    paginationBuilder.header(Component.join(JoinConfiguration.separator(Component.newline()), headerLines));

    List<Component> contentLines = new LinkedList<>();
    // Add parameters as first item in content
    if (!parent.parameters().isEmpty()) {
      contentLines.add(parent.parameters().stream()
          .flatMap(param -> {
            if (param instanceof Parameter.Value<?>) {
              return Stream.of((Parameter.Value<?>) param);
            } else {
              return ((Parameter.Multi) param).childParameters().stream()
                  .map(p -> (Parameter.Value<?>) p);
            }
          })
          .map(param -> param.usage(context.cause()))
          .collect(Collector.of(
              () -> Component.text().append(Component.text("> ")).color(Formatter.INFO),
              (builder, usageString) -> builder.append(Component.text(usageString)),
              (s1, s2) -> s1,
              ComponentBuilder::build)));
    }
    // Add children next, with suffix, if present
    parent.children().stream()
        .filter(child -> child.hasPermission(context.subject()))
        .forEach(child -> {
          Optional<Parameter.Value<?>> prefix = child.prefix();
          if (prefix.isPresent()) {
            contentLines.add(Formatter.keyValue("> "
                    + prefix.get().usage(context.cause())
                    + " "
                    + child.primaryAlias(),
                child.description()));
          } else {
            contentLines.add(Formatter.keyValue("> "
                    + child.primaryAlias(),
                child.description()));
          }
        });
    paginationBuilder.contents(contentLines);

    Sponge.asyncScheduler().submit(Task.builder()
        .execute(() -> paginationBuilder.build().sendTo(context.cause().audience()))
        .plugin(SpongeNope.instance().pluginContainer())
        .build());

    return CommandResult.success();
  }

}
