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

package com.minecraftonline.nope.command.common;

import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.stream.Collectors;

public class HelpCommandNode extends CommandNode implements CommandExecutor {

  public HelpCommandNode(@Nonnull CommandNode parent) {
    super(parent,
        null,
        Text.of("Command help for " + parent.getFullCommand()),
        "help",
        false);
    addAliases("?");
    Objects.requireNonNull(parent);
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) throws CommandException {
    CommandNode parent = this.getParent();
    if (parent == null) {
      throw new CommandException(Text.of("You cannot run the help command without a parent"));
    }
    PaginationList.builder().title(Text.of(Format.prefix(), TextColors.WHITE, "Help Menu ",
        this.getParent().getParent() != null
            ? Text.builder().append(Text.of(TextColors.DARK_GRAY, "[",
            TextColors.AQUA, this.getParent().getParent().getPrimaryAlias(),
            TextColors.DARK_GRAY, "]"))
            .onHover(TextActions.showText(Format.note("Open the help menu for the parent command")))
            .onClick(TextActions.runCommand(Objects.requireNonNull(this.getParent().getParent().getHelpCommand()).getFullCommand()))
            .build()
            : Text.EMPTY,
        " ",
        TextColors.AQUA, this.getParent().getPrimaryAlias()))
        .header(Text.of(
            TextColors.LIGHT_PURPLE, "Parameters > ",
            TextColors.GRAY,
            parent.build().getUsage(src),
            Text.NEW_LINE,
            parent.getComment() == null
                ? Text.EMPTY
                : Text.of("(", parent.getComment(), ")", Text.NEW_LINE),
            TextColors.LIGHT_PURPLE, "Description > ",
            TextColors.YELLOW, parent.getDescription(),
            getParent().getFlagDescriptions().isEmpty()
                ? Text.EMPTY
                : Text.of(
                "\n",
                TextColors.LIGHT_PURPLE, "Flags > ",
                Text.joinWith(Text.of(" "), getParent().getFlagDescriptions().entrySet().stream()
                    .map(entry -> Format.hover(Text.of(TextStyles.ITALIC,
                        TextColors.DARK_GRAY, "[",
                        entry.getValue().isValueFlag()
                            ? TextColors.GREEN
                            : TextColors.GOLD,
                        "-", entry.getKey(),
                        TextColors.DARK_GRAY, "]"),
                        Text.of(entry.getValue().isValueFlag()
                                ? Text.of(TextColors.GREEN, "Flag - Requires Value")
                                : Text.of(TextColors.GOLD, "Flag"),
                            "\n",
                            TextColors.RESET, entry.getValue().getDescription())))
                    .collect(Collectors.toList())))))
        .contents(parent.getChildren().stream()
            .filter(command -> command.getPermission() == null
                || src.hasPermission(command.getPermission().get()))
            .map(command -> Text.builder()
                .append(
                    Text.of(
                        TextColors.AQUA, TextStyles.ITALIC,
                        command.getAliases().get(0),
                        " ",
                        TextColors.WHITE, TextStyles.RESET, command.getDescription()))
                .onHover(TextActions.showText(Text.of(Format.note("Click for this command help menu"),
                    Text.NEW_LINE,
                    Format.note("Aliases: " + String.join(", ", command.getAliases())))))
                .onClick(command.getHelpCommand() == null
                    ? TextActions.executeCallback(s -> s.sendMessage(Format.info("Help commands don't have help menus")))
                    : TextActions.runCommand(command.getHelpCommand().getFullCommand()))
                .build())
            .collect(Collectors.toList()))
        .padding(Format.note("="))
        .build()
        .sendTo(src);
    return CommandResult.success();
  }

}
