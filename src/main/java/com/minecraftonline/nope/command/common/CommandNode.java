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
import com.google.common.collect.Lists;
import com.minecraftonline.nope.permission.Permission;
import lombok.Getter;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class CommandNode implements CommandExecutor {

  @Getter
  @Nullable
  private final CommandNode parent;
  @Getter
  @Nullable
  private final Permission permission;
  @Getter
  @Nullable
  private final Text description;
  @Getter
  private final List<String> aliases = Lists.newArrayList();
  private final List<CommandNode> children = Lists.newArrayList();
  private final List<CommandElement> commandElements = new ArrayList<>();
  private Supplier<Text> comment = () -> null;
  @Getter
  @Nullable
  private final HelpCommandNode helpCommand;

  /**
   * A helpful constructor which easily allows for addition of
   * multiple aliases and also automatically registers a help
   * sub-command. This constructor should be used most of the time.
   *
   * @param parent       the parent node of this node
   * @param permission   the permission allowing this command to be executed
   * @param description  the description of the functionality of the command
   * @param primaryAlias the alias to be used for referencing this command
   * @param otherAliases other aliases with which to call this command
   */
  public CommandNode(CommandNode parent,
                     Permission permission,
                     @Nonnull Text description,
                     @Nonnull String primaryAlias,
                     @Nonnull String... otherAliases) {
    this(parent, permission, description, primaryAlias, true);
    Preconditions.checkNotNull(otherAliases);
    this.aliases.addAll(Arrays.asList(otherAliases));
  }

  /**
   * Full constructor.
   *
   * @param parent       the parent node of this node
   * @param permission   the permission allowing this command to be executed
   * @param description  the description of the functionality of the command
   * @param primaryAlias the alias to be used for referencing this command
   * @param addHelp      whether an extra help sub-command should be added afterwards
   */
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
    this.aliases.add(primaryAlias);
    if (addHelp) {
      helpCommand = new HelpCommandNode(this);
      this.children.add(helpCommand);
    } else {
      helpCommand = null;
    }
  }

  @Nonnull
  public final CommandSpec build() {
    CommandSpec.Builder builder = CommandSpec.builder();
    builder.arguments(this.commandElements.toArray(new CommandElement[0]))
        .children(this.children
            .stream()
            .sorted(Comparator.comparing(node -> node.getPrimaryAlias()))
            .collect(Collectors.toMap(CommandNode::getAliases, CommandNode::build)))
        .description(this.description)
        .childArgumentParseExceptionFallback(false) // Stops too many argument error messages due to falling back to help subcommand
        .executor(this);
    if (permission != null) {
      builder.permission(permission.get());
    }
    return builder.build();
  }

  // Getters and Setters

  @Nonnull
  public final String getPrimaryAlias() {
    return aliases.get(0);
  }

  public final boolean hasPermission(Subject subject) {
    return this.permission == null || subject.hasPermission(this.permission.get());
  }

  /**
   * Adds all given Strings after filtering out those which are null.
   *
   * @param aliases the aliases with which to call this command
   */
  protected final void addAliases(@Nonnull String... aliases) {
    this.aliases.addAll(Arrays.stream(aliases)
        .filter(Objects::nonNull)
        .collect(Collectors.toList()));
  }

  @Nonnull
  public final List<CommandNode> getChildren() {
    return children;
  }

  protected final void addChildren(@Nonnull CommandNode... children) {
    this.children.addAll(Arrays.stream(children)
        .filter(Objects::nonNull)
        .collect(Collectors.toList()));
  }

  public void setComment(@Nonnull Supplier<Text> comment) {
    this.comment = comment;
  }

  @Nullable
  public Text getComment() {
    return this.comment.get();
  }

  protected final void addCommandElements(@Nonnull CommandElement... commandElement) {
    Objects.requireNonNull(commandElement);
    for (CommandElement element : commandElement) {
      this.commandElements.add(Objects.requireNonNull(element));
    }
  }

  public final boolean isRoot() {
    return parent == null;
  }

  @Nonnull
  public final String getFullCommand() {
    StringBuilder command = new StringBuilder(getPrimaryAlias());
    CommandNode cur = this;
    while (!cur.isRoot()) {
      command.insert(0, cur.parent.getPrimaryAlias() + " ");
      cur = cur.parent;
    }
    return "/" + command.toString();
  }

}
