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

package com.minecraftonline.nope.command;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.host.VolumeHost;
import com.minecraftonline.nope.key.zonewand.ZoneWandHandler;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.game.listener.DynamicSettingListeners;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Comparator;

public class CreateCommand extends LambdaCommandNode {

  CreateCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_CREATE,
        Text.of("Create a zone with current selection and given name"),
        "create",
        "c", "add");
    addCommandElements(
        GenericArguments.onlyOne(GenericArguments.string(Text.of("name"))),
        NopeArguments.zoneLocation(Text.of("selection")),
        GenericArguments.flags()
            .valueFlag(GenericArguments.integer(Text.of("priority")), "p")
            .buildWith(GenericArguments.none()));
    addChildren(new CreateSlabCommand(this));
    setExecutor((src, args) -> {
      if (!(src instanceof Player)) {
        return CommandResult.empty();
      }

      Player player = (Player) src;
      String name = args.requireOne(Text.of("name"));
      ZoneWandHandler.Selection selection = args.requireOne(Text.of("selection"));
      int priority = args.<Integer>getOne("priority").orElse(
          Nope.getInstance()
              .getHostTree()
              .getContainingHosts(player.getLocation())
              .stream().max(Comparator.comparingInt(Host::getPriority))
              .map(host -> host.getPriority() + 1).orElse(0));

      try {
        assert selection.getWorld() != null;
        VolumeHost zone = Nope.getInstance().getHostTree().addZone(
            name,
            selection.getWorld().getUniqueId(),
            selection.getMin(),
            selection.getMax(),
            priority
        );
        if (zone == null) {
          src.sendMessage(Format.error("Could not create zone"));
          return CommandResult.empty();
        }
        Nope.getInstance().saveState();
        DynamicSettingListeners.register();
        src.sendMessage(Format.success("Successfully created zone ", Format.note(zone.getName()), "!"));
        Nope.getInstance().getZoneWandHandler().getSelectionMap().remove(player.getUniqueId());
      } catch (IllegalArgumentException e) {
        src.sendMessage(Format.error("Could not create zone: " + e.getMessage()));
        return CommandResult.empty();
      }
      return CommandResult.success();
    });
  }
}
