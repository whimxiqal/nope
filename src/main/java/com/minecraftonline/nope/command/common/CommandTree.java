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

package com.minecraftonline.nope.command.common;

import com.google.common.base.Preconditions;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class CommandTree {

  private CommandNode root;

  public CommandTree(@Nonnull CommandNode root) {
    Preconditions.checkNotNull(root);
    this.root = root;
  }

  public CommandNode root() {
    return root;
  }

  public void register() {
    Sponge.getCommandManager().register(Nope.getInstance(), root().build(), root().getAliases());
  }

  static class HelpCommandNode extends CommandNode implements CommandExecutor {

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
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      PaginationList.builder().title(Format.info("Command Help : ", Format.note(
          "{",
          Text.joinWith(
              Text.of(", "),
              getAliases().stream().map(Text::of).collect(Collectors.toList())),
          "}")))
          .header(Text.of(
              TextColors.LIGHT_PURPLE, "Parameters:",
              " ",
              TextColors.GRAY,
              build().getUsage(src),
              "\n",
              TextColors.LIGHT_PURPLE, "Description:",
              " ",
              TextColors.YELLOW, getDescription()))
          .contents(getChildren().stream()
              .filter(command -> src.hasPermission(command.getPermission().toString()))
              .map(command -> Format.note(
                  TextColors.AQUA, Format.hover(
                      command.getAliases().get(0),
                      "Aliases: " + String.join(", ", command.getAliases())),
                  " ",
                  TextColors.WHITE, command.getDescription()))
              .collect(Collectors.toList()))
          .padding(Format.note("="))
          .build()
          .sendTo(src);
      return CommandResult.success();
    }

  }

}

