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

package com.minecraftonline.nope.sponge.command;

import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.general.CommandNode;
import com.minecraftonline.nope.sponge.command.general.LambdaCommandNode;
import com.minecraftonline.nope.sponge.key.zonewand.ZoneWandHandler;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

/**
 * The command to set position 1 of the player's nope selection.
 */
public class Position1Command extends LambdaCommandNode {

  /**
   * Default constructor.
   *
   * @param parent the parent class
   */
  public Position1Command(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_CREATE,
        Text.of("Set position 1 of the nope selection"),
        "pos1");
    setComment(() -> Format.note("You can also use the nope wand")
        .toBuilder()
        .onClick(TextActions.suggestCommand(SpongeNope.getInstance().getCommandTree()
            .findNode(WandCommand.class)
            .orElseThrow(() ->
                new RuntimeException("Wand command is not present in the Nope command tree!"))
            .getFullCommand()))
        .build());
    setExecutor((src, args) -> {
      if (!(src instanceof Player)) {
        src.sendMessage(Format.error("You must be in game to teleport"));
        return CommandResult.empty();
      }

      Player player = (Player) src;
      SpongeNope.getInstance()
          .getZoneWandHandler()
          .getSelectionMap()
          .putIfAbsent(player.getUniqueId(), new ZoneWandHandler.Selection());

      SpongeNope.getInstance()
          .getZoneWandHandler()
          .getSelectionMap()
          .get(player.getUniqueId()).setPos1(player.getLocation(), player);

      return CommandResult.success();
    });
  }
}
