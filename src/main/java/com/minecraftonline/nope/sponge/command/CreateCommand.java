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

import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.general.arguments.NopeArguments;
import com.minecraftonline.nope.sponge.command.general.CommandNode;
import com.minecraftonline.nope.sponge.command.general.FlagDescription;
import com.minecraftonline.nope.sponge.command.general.LambdaCommandNode;
import com.minecraftonline.nope.sponge.game.listener.DynamicSettingListeners;
import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.host.VolumeHost;
import com.minecraftonline.nope.sponge.key.zonewand.ZoneWandHandler;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.util.Format;
import java.util.Comparator;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * Command to create a zone with a current Zone wand selection
 * and a given name.
 */
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
    addFlagDescription(FlagDescription.PRIORITY);
    addChildren(new CreateSlabCommand(this));
    setExecutor((src, args) -> {
      if (!(src instanceof Player)) {
        return CommandResult.empty();
      }

      Player player = (Player) src;
      String name = args.requireOne(Text.of("name"));
      ZoneWandHandler.Selection selection = args.requireOne(Text.of("selection"));
      int priority = args.<Integer>getOne("priority").orElse(
          SpongeNope.getInstance()
              .getHostTreeAdapter()
              .getContainingHosts(player.getLocation())
              .stream().max(Comparator.comparingInt(Host::getPriority))
              .map(host -> host.getPriority() + 1).orElse(0));

      try {
        if (selection.getWorld() == null
            || selection.getMin() == null
            || selection.getMax() == null) {
          // This shouldn't happen
          src.sendMessage(Format.error("Selection is malformed"));
          return CommandResult.empty();
        }
        VolumeHost zone = SpongeNope.getInstance().getHostTreeAdapter().addZone(
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
        SpongeNope.getInstance().saveState();
        DynamicSettingListeners.register();
        src.sendMessage(Format.success("Successfully created zone ",
            Format.note(zone.getName()), "!"));
        SpongeNope.getInstance().getZoneWandHandler().getSelectionMap().remove(player.getUniqueId());
      } catch (IllegalArgumentException e) {
        src.sendMessage(Format.error("Could not create zone: " + e.getMessage()));
        return CommandResult.empty();
      }
      return CommandResult.success();
    });
  }
}
