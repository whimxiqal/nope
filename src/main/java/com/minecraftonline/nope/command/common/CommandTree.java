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
import org.spongepowered.api.Sponge;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Stack;

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
  public <N extends CommandNode> Optional<N> findNode(Class<N> commandType) {
    Stack<CommandNode> nextUp = new Stack<>();
    if (root == null) {
      throw new IllegalStateException("The root of the Nope tree cannot be null!");
    }
    nextUp.add(root);
    CommandNode current;
    while (!nextUp.isEmpty()) {
      current = nextUp.pop();
      if (commandType.isInstance(current)) {
        return Optional.of((N) current);
      }
      nextUp.addAll(current.getChildren());
    }
    return Optional.empty();
  }

}

