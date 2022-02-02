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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.minecraftonline.nope.common.permission.Permission;
import com.minecraftonline.nope.sponge.SpongeNope;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
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
  @Accessors(fluent = true)
  private final @Nullable CommandNode parent;
  @Getter
  @Accessors(fluent = true)
  private final @Nullable Permission permission;
  @Getter
  @Accessors(fluent = true)
  private final @NotNull String description;
  @Getter
  @Accessors(fluent = true)
  private final List<String> aliases = Lists.newArrayList();
  @Getter
  @Accessors(fluent = true)
  private final List<CommandNode> children = Lists.newArrayList();
  @Getter
  @Accessors(fluent = true)
  private final List<Parameter> parameters = Lists.newArrayList();
  @Getter
  @Accessors(fluent = true)
  private final List<Flag> flags = Lists.newArrayList();
  @Getter
  @Nullable
  @Accessors(fluent = true)
  private final HelpCommandNode helpCommand;
  @Setter
  @Accessors(fluent = true)
  private Parameter.Value<?> prefix = null;
  private Supplier<Component> comment = () -> null;
  @Accessors(fluent = true)
  private Command.Parameterized parameterized;
  @Getter
  @Setter
  @Accessors(fluent = true)
  private boolean terminal = false;

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
  public final String primaryAlias() {
    return aliases.get(0);
  }

  @NotNull
  public final String[] secondaryAliases() {
    if (aliases.size() <= 1) {
      return new String[0];
    } else {
      return aliases.subList(1, aliases.size()).toArray(new String[0]);
    }
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

  public final void addChild(@NotNull CommandNode child) {
    this.children.add(child);
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

  public final void addParameter(Parameter.Value<?> parameter) {
    this.parameters.add(parameter);
  }

  public final void addParameter(Parameter.Multi parameters) {
    this.parameters.add(parameters);
  }

  /**
   * Get the full command string by traversing backwards through
   * the command tree and concatenating each parent class.
   *
   * @return the fully qualified command
   */
  @NotNull
  public final String fullCommand(@Nullable CommandCause cause) {
    StringBuilder command = new StringBuilder();
    CommandNode cur = this;
    while (cur != null /* is null if is parent of root command */) {
      command.insert(0, cur.primaryAlias() + " ");
      if (cur.prefix != null) {
        if (cause == null) {
          command.insert(0, cur.prefix.key().key() + " ");
        } else {
          command.insert(0, cur.prefix.usage(cause) + " ");
        }

      }
      cur = cur.parent;
    }
    return "/" + command.substring(0, command.length() - 1);
  }

  public final Command.Parameterized parameterized() {

    // Cache parameterized build
    if (this.parameterized != null) {
      return parameterized;
    }

    List<Parameter> possibleParameters = new LinkedList<>();
    if (!this.parameters.isEmpty()) {
      possibleParameters.add(Parameter.seq(new ArrayList<>(this.parameters)));
    }

    Set<Parameter> prefixes = new HashSet<>();
    List<Parameter> subCommands = new LinkedList<>();
    Command.Builder builder = Command.builder()
        .executor(this)
        .extendedDescription(Component.text(description))
        .shortDescription(Component.text(description))
        .addFlags(flags);

    children().forEach(child -> {
          if (child.prefix == null) {
            builder.addChild(child.parameterized(), child.aliases);
          } else {
            prefixes.add(child.prefix);
            subCommands.add(child.asSubCommand());
          }
        }
    );

    if (!subCommands.isEmpty()) {
      possibleParameters.add(Parameter.seq(Parameter.firstOf(prefixes), Parameter.firstOf(subCommands)));
    }

    if (!possibleParameters.isEmpty()) {
      builder.addParameter(Parameter.firstOf(possibleParameters));
    }

    if (permission != null) {
      builder.permission(permission.get());
    }

    this.parameterized = builder.build();
    return this.parameterized;
  }

  public final Optional<Parameter.Value<?>> prefix() {
    return Optional.ofNullable(prefix);
  }

  private Parameter asSubCommand() {
    return Parameter.subcommand(this.parameterized(), this.primaryAlias(), this.secondaryAliases());
  }
}
