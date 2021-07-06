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

import com.minecraftonline.nope.common.permission.Permission;
import java.util.Objects;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

/**
 * A function with a tighter implementation because the executor can be added
 * as a lambda function.
 */
public abstract class LambdaCommandNode extends CommandNode {

  private BiFunction<CommandSource, CommandContext, CommandResult> executor = (src, args) ->
      CommandResult.empty();

  public LambdaCommandNode(CommandNode parent,
                           Permission permission,
                           @Nonnull Text description,
                           @Nonnull String primaryAlias,
                           @Nonnull String... otherAliases) {
    super(parent, permission, description, primaryAlias, otherAliases);
  }

  public LambdaCommandNode(CommandNode parent,
                           Permission permission,
                           @Nonnull Text description,
                           @Nonnull String primaryAlias,
                           boolean addHelp) {
    super(parent, permission, description, primaryAlias, addHelp);
  }

  protected final void setExecutor(@Nonnull BiFunction<CommandSource,
      CommandContext,
      CommandResult> executor) {
    this.executor = Objects.requireNonNull(executor);
  }

  @Override
  public final @NotNull CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
    return executor.apply(src, args);
  }
}
