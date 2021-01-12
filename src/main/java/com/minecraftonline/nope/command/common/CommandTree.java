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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
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

  @Nonnull
  public Optional<CommandNode> findNode(Class<? extends CommandNode> commandType) {
    Stack<CommandNode> nextUp = new Stack<>();
    if (root == null) {
      throw new IllegalStateException("The root of the Nope tree cannot be null!");
    }
    nextUp.add(root);
    CommandNode current;
    while (!nextUp.isEmpty()) {
      current = nextUp.pop();
      if (commandType.isInstance(current)) {
        return Optional.of(current);
      }
      nextUp.addAll(current.getChildren());
    }
    return Optional.empty();
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
    public CommandResult execute(@Nonnull CommandSource src,
                                 @Nonnull CommandContext args) throws CommandException {
      CommandNode parent = this.getParent();
      if (parent == null) {
        throw new CommandException(Text.of("You cannot run the help command without a parent"));
      }
      PaginationList.builder().title(Format.info("Command Help : ", Format.note(
          "{",
          Text.joinWith(
              Text.of(", "),
              parent.getAliases().stream().map(Text::of).collect(Collectors.toList())),
          "}")))
          .header(Text.of(
              TextColors.LIGHT_PURPLE, "Parameters:",
              " ",
              TextColors.GRAY,
              parent.build().getUsage(src),
              Text.NEW_LINE,
              parent.getComment() == null
                  ? Text.EMPTY
                  : Text.of(parent.getComment(), Text.NEW_LINE),
              TextColors.LIGHT_PURPLE, "Description:",
              " ",
              TextColors.YELLOW, parent.getDescription()))
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
                  .onHover(TextActions.showText(Text.of(Format.note("Click to run command"),
                      Text.NEW_LINE,
                      Format.note("Aliases: " + String.join(", ", command.getAliases())))))
                  .onClick(TextActions.suggestCommand(command.getFullCommand()))
                  .build())
              .collect(Collectors.toList()))
          .padding(Format.note("="))
          .build()
          .sendTo(src);
      return CommandResult.success();
    }

  }

}

