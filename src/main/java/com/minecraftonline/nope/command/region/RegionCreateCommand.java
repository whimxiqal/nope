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
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.RegularRegion;
import com.minecraftonline.nope.control.WorldHost;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class RegionCreateCommand extends LambdaCommandNode {
  public RegionCreateCommand(CommandNode parent) {
    super(parent,
        Permissions.CREATE_REGION,
        Text.of("Create a region with current selection and given name"),
        "create",
        "new");
    addCommandElements(GenericArguments.onlyOne(GenericArguments.string(Text.of("name"))));
    setExecutor((src, args) -> {
      String name = (String) args.getOne(Text.of("name")).get();
      if (!(src instanceof Player)) {
        src.sendMessage(Format.error("You must execute this command as a player"));
        return CommandResult.empty();
      }
      Player player = (Player)src;
      RegionWandHandler.Selection selection = Nope.getInstance().getRegionWandHandler().getSelectionMap().get(player);
      if (selection == null || !selection.isComplete()) {
        player.sendMessage(Format.error("You must have a selection (use /nope region wand to get a wand)"));
        return CommandResult.empty();
      }
      WorldHost worldHost = Nope.getInstance().getGlobalHost().getWorld(selection.getWorld());
      if (worldHost.getRegions().get(name) != null) {
        player.sendMessage(Format.error("There is already a region with the name '" + name + "'"));
        return CommandResult.empty();
      }
      Region region = new RegularRegion(selection.getWorld(), selection.getPos1(), selection.getPos2());
      worldHost.addRegion(name, region);
      Nope.getInstance().getRegionConfigManager().onRegionCreate(worldHost, name, region);
      player.sendMessage(Format.info("Region '" + name + "' successfully created"));
      return CommandResult.success();
    });
  }
}
