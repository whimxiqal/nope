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
import java.util.Optional;
import java.util.Stack;
import javax.annotation.Nonnull;

/**
 * An abstract command tree which holds the entire plugin
 * command structure. There is a root and each node within the tree
 * may have children and parameters.
 */
public class CommandTree {

  private final CommandNode root;

  public CommandTree(@Nonnull CommandNode root) {
    Preconditions.checkNotNull(root);
    this.root = root;
  }

  public CommandNode root() {
    return root;
  }

  /**
   * Find a certain command node from the tree by its type.
   * If there are multiple of the same type (which there shouldn't be).
   *
   * @param commandType the java class wrapper type of the command
   * @param <N>         the type of command
   * @return the instance of the given type
   */
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
        return Optional.of(commandType.cast(current));
      }
      nextUp.addAll(current.getChildren());
    }
    return Optional.empty();
  }

}

