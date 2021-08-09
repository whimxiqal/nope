/*
 * MIT License
 *
 * Copyright (c) 2021 MinecraftOnline
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

package com.minecraftonline.nope.sponge.command;

import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.general.CommandNode;
import com.minecraftonline.nope.sponge.command.general.arguments.NopeParameterKeys;
import com.minecraftonline.nope.sponge.command.general.arguments.NopeParameters;
import com.minecraftonline.nope.sponge.listener.DynamicSettingListeners;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

/**
 * Command to destroy a given zone by name.
 */
public class DestroyCommand extends CommandNode {

  DestroyCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_DESTROY,
        "Destroy a given zone",
        "destroy", "remove");
    addParameter(NopeParameters.HOST);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    Host host = context.requireOne(NopeParameterKeys.HOST);

    try {
      SpongeNope.instance().getHostTreeAdapter().removeZone(host.getName());
    } catch (IllegalArgumentException e) {
      return CommandResult.error(formatter().error("This zone cannot be destroyed!"));
    }

    SpongeNope.instance().saveState();
    DynamicSettingListeners.register();
    context.cause()
        .audience()
        .sendMessage(formatter().success("Zone ___ was successfully destroyed.", host.getName()));

    return CommandResult.success();
  }
}
