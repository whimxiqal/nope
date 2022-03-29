/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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

package me.pietelite.nope.sponge.command;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Stream;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.util.Formatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationList;

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
        "?",
        false);
    addAliases("help");
    terminal();
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
    headerLines.add(Component.text("Usage: ")
        .color(Formatter.GOLD)
        .append(Component.text(parent.fullCommand(context))
            .color(Formatter.DULL)));
    headerLines.add(Component.text("Desc: ")
        .color(Formatter.GOLD)
        .append(Component.text(parent.description())
            .color(Formatter.DULL)));
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
              () -> Component.text()
                  .append(Component.text("> ").color(Formatter.THEME))
                  .append(Component.text(parent.primaryAlias())
                      .color(Formatter.ACCENT)),
              (builder, usageString) -> builder.append(Component.text(" ("
                      + usageString
                      + ")")
                  .color(Formatter.ACCENT)),
              (s1, s2) -> s1,
              ComponentBuilder::build)));
    }
    // Add children next, with suffix (prefix of child), if present
    parent.children().stream()
        .filter(child -> child.hasPermission(context.subject()))
        .filter(child -> child.helpCommand() != null)
        .forEach(child -> {
          TextComponent.Builder component = Component.text()
              .append(Component.text("> ").color(Formatter.THEME));
          child.prefix().ifPresent(value -> component.append(Component.text("("
                  + value.usage(context.cause())
                  + ") ")
              .color(Formatter.DULL)));
          component.append(Component.text(child.primaryAlias() + " ").color(Formatter.ACCENT))
              .clickEvent(ClickEvent.suggestCommand(Objects.requireNonNull(child.helpCommand())
                  .fullCommand(context)))
              .hoverEvent(HoverEvent.showText(Component.text("Learn how to use ")
                  .append(Component.text(child.fullCommand(context)).color(Formatter.ACCENT))))
              .append(Component.text(child.description()).color(Formatter.INFO));
          contentLines.add(component.build());
        });
    paginationBuilder.contents(contentLines);

    Sponge.asyncScheduler().submit(Task.builder()
        .execute(() -> paginationBuilder.build().sendTo(context.cause().audience()))
        .plugin(SpongeNope.instance().pluginContainer())
        .build());

    return CommandResult.success();
  }

}
