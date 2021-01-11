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
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.FunctionlessCommandNode;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class RegionCommand extends FunctionlessCommandNode {
  public RegionCommand(CommandNode parent) {
    super(parent,
        null,
        Text.of("Alter Nope regions"),
        "region",
        "rg");
    addChildren(new RegionApplyCommand(this));
    addChildren(new RegionCreateCommand(this));
    addChildren(new RegionDestroyCommand(this));
    addChildren(new RegionInfoCommand(this));
    addChildren(new RegionListCommand(this));
    addChildren(new RegionMoveCommand(this));
    addChildren(new RegionPosition1Command(this));
    addChildren(new RegionPosition2Command(this));
    addChildren(new RegionSetCommand(this));
    addChildren(new RegionSetPriorityCommand(this));
    addChildren(new RegionShowCommand(this));
    addChildren(new RegionTargetCommand(this));
    addChildren(new RegionTeleportCommand(this));
    addChildren(new RegionUnsetCommand(this));
    addChildren(new RegionWandCommand(this));
  }

  static Optional<Host> inferHost(CommandSource src) {
    if (!(src instanceof Player)) {
      src.sendMessage(Format.error("Can't infer region! "
          + "Please specify the target region."));
      return Optional.empty();
    }
    Player player = (Player) src;
    List<Host> containing = Nope.getInstance()
        .getHostTree()
        .getContainingHosts(player.getLocation());
    if (containing.isEmpty()) {
      src.sendMessage(Format.error("Can't infer region! "
          + "Please specify the target region."));
      return Optional.empty();
    }
    return containing.stream().max(Comparator.comparing(Host::getPriority));
  }
}
