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
 *
 */

package com.minecraftonline.nope.command.common;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.permission.Permission;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

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

  public abstract static class CommandNode implements CommandExecutor {
    private final CommandNode parent;
    private final Permission permission;
    private final Text description;
    private final List<String> aliases;
    private final List<CommandNode> children;
    private CommandElement commandElement = GenericArguments.none();

    public CommandNode(CommandNode parent,
                       Permission permission,
                       @Nonnull Text description,
                       @Nonnull String primaryAlias,
                       @Nonnull String... otherAliases) {
      this(parent, permission, description, primaryAlias, true);
      Preconditions.checkNotNull(otherAliases);
      this.aliases.addAll(Arrays.asList(otherAliases));
    }

    public CommandNode(CommandNode parent,
                       Permission permission,
                       @Nonnull Text description,
                       @Nonnull String primaryAlias,
                       boolean addHelp) {
      Preconditions.checkNotNull(description);
      Preconditions.checkNotNull(primaryAlias);
      this.parent = parent;
      this.permission = permission;
      this.description = description;
      this.aliases = Lists.newArrayList();
      this.aliases.add(primaryAlias);
      this.children = Lists.newArrayList();
      if (addHelp) {
        this.children.add(new HelpCommandNode(this));
      }
    }

    @Nonnull
    public CommandSpec build() {
      CommandSpec.Builder builder = CommandSpec.builder();
      builder.arguments(this.commandElement)
          .children(this.children
              .stream()
              .collect(Collectors.toMap(CommandNode::getAliases, CommandNode::build)))
          .description(this.description)
          .executor(this);
      if (permission != null) {
        builder.permission(permission.get());
      }
      return builder.build();
    }

    // Getters and Setters

    @Nullable
    public CommandNode getParent() {
      return parent;
    }

    @Nonnull
    public Optional<Permission> getPermission() {
      return Optional.ofNullable(permission);
    }

    @Nonnull
    public Text getDescription() {
      return description;
    }

    @Nonnull
    public String getPrimaryAlias() {
      return aliases.get(0);
    }

    @Nonnull
    public List<String> getAliases() {
      return aliases;
    }

    public void addAliases(String... aliases) {
      this.aliases.addAll(Arrays.asList(aliases));
    }

    @Nonnull
    public String getFullCommand() {
      StringBuilder command = new StringBuilder(getPrimaryAlias());
      while (!isRoot()) {
        command.insert(0, parent + " ");
      }
      return command.toString();
    }

    @Nonnull
    public List<CommandNode> getChildren() {
      return children;
    }

    public boolean isRoot() {
      return parent == null;
    }

  }

  private static class HelpCommandNode extends CommandNode implements CommandExecutor {

    public HelpCommandNode(@Nonnull CommandNode parent) {
      super(parent,
          null,
          Text.of("Command help for " + parent.getFullCommand()),
          "help",
          false);
      addAliases("?");
      Preconditions.checkNotNull(parent);
    }

    @Nonnull
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      src.sendMessage(Format.info(getDescription()));
      // TODO: format help response
      return CommandResult.success();
    }

  }

}

