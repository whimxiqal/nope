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

import com.minecraftonline.nope.common.host.VolumeHost;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.general.CommandErrors;
import com.minecraftonline.nope.sponge.command.general.CommandNode;
import com.minecraftonline.nope.sponge.command.general.arguments.NopeFlags;
import com.minecraftonline.nope.sponge.command.general.arguments.NopeParameterKeys;
import com.minecraftonline.nope.sponge.command.general.arguments.NopeParameters;
import com.minecraftonline.nope.sponge.listener.DynamicSettingListeners;
import com.minecraftonline.nope.sponge.wand.Selection;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Command to create a zone with a current Zone wand selection
 * and a given name.
 */
public class CreateCommand extends CommandNode {

  CreateCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_CREATE,
        "Create a zone with current selection and given name",
        "create",
        "c", "add");
    addParameter(NopeParameters.NAME);
    addParameter(NopeParameters.SELECTION);
    addFlag(NopeFlags.PRIORITY_FLAG);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    if (!(context.cause().root() instanceof Player)) {
      return CommandResult.error(CommandErrors.ONLY_PLAYERS.get());
    }

    Player player = (Player) context.cause().root();
    String name = context.requireOne(NopeParameterKeys.NAME);
    Selection selection = context.requireOne(NopeParameterKeys.SELECTION);
    int priority = context.requireOne(NopeParameterKeys.PRIORITY);

    try {
      VolumeHost zone = SpongeNope.instance().getHostTreeAdapter().addZone(
          name,
          selection.getWorldKey().asString(),
          selection.minPosition(),
          selection.maxPosition(),
          priority
      );
      if (zone == null) {
        return CommandResult.error(formatter().error("Could not create zone"));
      }
      SpongeNope.instance().saveState();
      DynamicSettingListeners.register();
      player.sendMessage(formatter().success("Successfully created zone ___!", zone.getName()));
    } catch (IllegalArgumentException e) {
      return CommandResult.error(formatter().error(e.getMessage()));
    }
    return CommandResult.success();
  }
}
