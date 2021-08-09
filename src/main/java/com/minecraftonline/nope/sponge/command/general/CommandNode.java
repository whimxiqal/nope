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

package com.minecraftonline.nope.sponge.command.general;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.minecraftonline.nope.common.permission.Permission;
import com.minecraftonline.nope.common.util.Formatter;
import com.minecraftonline.nope.sponge.SpongeNope;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.service.permission.Subject;

/**
 * A general command node in the general command tree
 * with the purpose of intuitively constructing the command structure.
 */
public abstract class CommandNode implements CommandExecutor {

  @Getter
  private final @Nullable CommandNode parent;
  @Getter
  private final @Nullable Permission permission;
  @Getter
  private final @NotNull String description;
  @Getter
  private final List<String> aliases = Lists.newArrayList();
  private final List<CommandNode> children = Lists.newArrayList();
  @Getter
  private final List<Parameter> parameters = Lists.newArrayList();
  @Getter
  private final List<Flag> flags = Lists.newArrayList();
  @Getter
  private final List<FlagDescription> flagDescriptions = Lists.newArrayList();
  @Getter
  @Nullable
  private final HelpCommandNode helpCommand;
  private Supplier<Component> comment = () -> null;

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
                     @NotNull String description,
                     @NotNull String primaryAlias,
                     @NotNull String... otherAliases) {
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
  public CommandNode(@Nullable CommandNode parent,
                     @Nullable Permission permission,
                     @NotNull String description,
                     @NotNull String primaryAlias,
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

  // Getters and Setters

  @NotNull
  public final String getPrimaryAlias() {
    return aliases.get(0);
  }

  public final boolean hasPermission(UUID playerUuid) {
    return this.permission == null || SpongeNope.instance().hasPermission(playerUuid, this.permission);
  }

  public final boolean hasPermission(Subject subject) {
    return this.permission == null || subject.hasPermission(this.permission.get());
  }

  /**
   * Adds all given Strings after filtering out those which are null.
   *
   * @param aliases the aliases with which to call this command
   */
  protected final void addAliases(@NotNull String... aliases) {
    this.aliases.addAll(Arrays.asList(aliases));
  }

  @NotNull
  public final List<CommandNode> getChildren() {
    return children;
  }

  protected final void addChildren(@NotNull CommandNode... children) {
    this.children.addAll(Arrays.asList(children));
  }

  @Nullable
  public Component getComment() {
    return this.comment.get();
  }

  public void setComment(@NotNull Supplier<Component> comment) {
    this.comment = comment;
  }

  public final boolean isRoot() {
    return parent == null;
  }

  public final void addFlag(Flag flag/*, String description*/) {
    this.flags.add(flag);
//    this.flagDescriptions.add(new FlagDescription(flag.aliases().iterator().next(),
//        description,
//        flag.associatedParameter().isPresent()));
  }

  public final void addParameter(Parameter parameter) {
    this.parameters.add(parameter);
  }

  /**
   * Get the full command string by traversing backwards through
   * the command tree and concatenating each parent class.
   *
   * @return the fully qualified command
   */
  @NotNull
  public final String getFullCommand() {
    StringBuilder command = new StringBuilder(getPrimaryAlias());
    CommandNode cur = this;
    while (cur.parent != null /* is null if is root command */) {
      command.insert(0, cur.parent.getPrimaryAlias() + " ");
      cur = cur.parent;
    }
    return "/" + command;
  }

  public final Command.Parameterized build() {
    Command.Builder builder = Command.builder()
        .executor(this)
        .extendedDescription(Component.text(description))
        .shortDescription(Component.text(description))
        .addParameters(parameters)
        .addChildren(children.stream().collect(Collectors.toMap(CommandNode::getAliases, CommandNode::build)))
        .addFlags(flags);

    if (permission != null) {
      builder.permission(permission.get());
    }

    return builder.build();
  }

  protected final Formatter<Component, TextColor> formatter() {
    return SpongeNope.instance().formatter();
  }

  protected final <N extends CommandNode> N getRelatedNode(Class<N> nodeClass) {
    CommandNode root = this;
    while (root.parent != null) {
      root = root.parent;
    }
    Stack<CommandNode> nextUp = new Stack<>();
    nextUp.add(root);
    CommandNode current;
    while (!nextUp.isEmpty()) {
      current = nextUp.pop();
      if (nodeClass.isInstance(current)) {
        return nodeClass.cast(current);
      }
      nextUp.addAll(current.getChildren());
    }
    throw new IllegalStateException("This command tree does not contain a command class of type " + nodeClass.getName());
  }

}
