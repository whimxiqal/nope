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
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.arguments.RegionWrapper;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.control.GlobalRegion;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.WorldHost;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

public class DeleteRegionCommand extends LambdaCommandNode {
  public DeleteRegionCommand(CommandNode parent) {
    super(parent,
        Permissions.DELETE_REGIONS,
        Text.of("Delete a given region"),
        "delete",
        "remove");
    setCommandElement(GenericArguments.onlyOne(NopeArguments.regionWrapper(Text.of("region"))));
    setExecutor((src, args) -> {
      RegionWrapper region = args.<RegionWrapper>getOne(Text.of("region")).get();
      WorldHost worldHost = region.getWorldHost();
      boolean isGlobal = region.getRegion() instanceof GlobalRegion;
      if (isGlobal) {
        worldHost.removeRegion(region.getRegionName());
        Nope.getInstance().getRegionConfigManager().onRegionRemove(worldHost, region.getRegionName());
        Region newRegion = new GlobalRegion(worldHost.getWorldUuid());
        worldHost.addRegion(region.getRegionName(), newRegion);
        Nope.getInstance().getRegionConfigManager().onRegionCreate(worldHost, region.getRegionName(), newRegion);
        src.sendMessage(Format.info("Global region successfully cleared - it cannot be deleted"));
      }
      else {
        worldHost.removeRegion(region.getRegionName());
        Nope.getInstance().getRegionConfigManager().onRegionRemove(worldHost, region.getRegionName());
        src.sendMessage(Format.info("Region: '" + region + "' has been deleted"));
      }
      return CommandResult.success();
    });
  }
}
