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

package com.minecraftonline.nope.command;

import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.FunctionlessCommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.permission.Permission;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;

/**
 * A simple example class to show how the current Nope command system works.
 * Make sure to put the correct parent (something in the Nope command Tree).
 * The registration will happen automatically following the registration of
 * the root command in the command tree.
 */
public final class ExampleCommand extends FunctionlessCommandNode {

  public ExampleCommand(@Nonnull CommandNode parent) {
    super(parent,
        Permission.of("node.command.example"),
        Text.of("This is a description of the command we're about to do"),
        "example",
        "ex", "placeholder");
    // Add all children
    addChildren(new ExampleSubCommand(this));
  }

  // There is no "execute" method here because we are extending the FunctionlessCommandNode here

  /**
   * A sub command for the {@link ExampleCommand} that's given as a nested class
   * for ease of use.
   */
  public static final class ExampleSubCommand extends LambdaCommandNode {

    public ExampleSubCommand(CommandNode parent) {
      super(parent, Permission.of("node.command.example.sub"),
          Text.of("This is another description"),
          "nextexample", "ex2");
      // Add an element to describe the arguments of this command node
      addCommandElements(GenericArguments.optional(GenericArguments.integer(Text.of("int"))));
      setExecutor((src, args) -> {
        // Use the elements down here
        if (!args.getOne(Text.of("int")).isPresent()) {
          src.sendMessage(Format.error("Boo! You forgot the argument!"));
          return CommandResult.empty();
        }
        src.sendMessage(Format.info("Look at that! The sub command was used! "
            + "And, no less, you gave us the number " + args.getOne(Text.of("int")).get() + " !"));
        return CommandResult.success();
      });
    }
  }

}
