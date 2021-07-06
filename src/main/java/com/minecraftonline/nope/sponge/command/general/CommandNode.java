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
import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.permission.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A general command node in the general command tree
 * with the purpose of intuitively constructing the command structure.
 */
public abstract class CommandNode {

  @Getter
  private final @Nullable CommandNode parent;
  @Getter
  private final @Nullable Permission permission;
  @Getter
  private final @NotNull String description;
  @Getter
  private final List<String> aliases = Lists.newArrayList();
  private final List<CommandNode> children = Lists.newArrayList();
  private final List<CommandElement> commandElements = new ArrayList<>();
  @Getter
  private final Map<String, FlagDescription> flagDescriptions = new HashMap<>();
  @Getter
  @Nullable
  private final HelpCommandNode helpCommand;
  private Supplier<String> comment = () -> null;

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
    return this.permission == null || Nope.getInstance().hasPermission(playerUuid, this.permission);
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
  public String getComment() {
    return this.comment.get();
  }

  public void setComment(@NotNull Supplier<String> comment) {
    this.comment = comment;
  }

  protected final void addCommandElements(@NotNull CommandElement... commandElement) {
    Objects.requireNonNull(commandElement);
    for (CommandElement element : commandElement) {
      this.commandElements.add(Objects.requireNonNull(element));
    }
  }

  public final boolean isRoot() {
    return parent == null;
  }

  public final void addFlagDescription(FlagDescription flagDescription) {
    this.flagDescriptions.put(flagDescription.getFlag(), flagDescription);
  }

  public final void addFlagDescription(String flag, String description, boolean valueFlag) {
    this.addFlagDescription(new FlagDescription(flag, description, valueFlag));
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

}
