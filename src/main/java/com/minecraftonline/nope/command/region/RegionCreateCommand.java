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

package com.minecraftonline.nope.command.region;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.RegionWandHandler;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

public class RegionCreateCommand extends LambdaCommandNode {
  public RegionCreateCommand(CommandNode parent) {
    super(parent,
        Permissions.CREATE_REGION,
        Text.of("Create a region with current selection and given name"),
        "create",
        "c",
        "new");
    addCommandElements(
        GenericArguments.onlyOne(GenericArguments.string(Text.of("name"))),
        NopeArguments.regionLocation(Text.of("selection")),
        GenericArguments.optional(GenericArguments.integer(Text.of("priority")), 0)
    );
    setExecutor((src, args) -> {
      String name = args.requireOne(Text.of("name"));
      RegionWandHandler.Selection selection = args.requireOne(Text.of("selection"));
      int priority = args.requireOne("priority");

      try {
        Nope.getInstance().getHostTree().addRegion(
            selection.getWorld().getUniqueId(),
            name,
            selection.getMin(),
            selection.getMax(),
            priority
        );
      } catch (IllegalArgumentException e) {
        src.sendMessage(Format.error("Could not create region: " + e.getMessage()));
        return CommandResult.empty();
      }
      return CommandResult.success();
    });
  }
}
