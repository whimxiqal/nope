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
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.host.VolumeHost;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class RegionListCommand extends LambdaCommandNode {

  RegionListCommand(CommandNode parent) {
    super(parent,
        Permissions.LIST_REGIONS,
        Text.of("List the regions in the current world"),
        "list",
        "l");
    setExecutor((src, args) -> {
      if (!(src instanceof Player)) {
        src.sendMessage(Format.error("You must be a player to use this command!"));
        return CommandResult.empty();
      }
      UUID worldUuid = ((Player) src).getWorld().getUniqueId();
      Collection<VolumeHost> regions = Nope.getInstance().getHostTree().getRegions(worldUuid);
      src.sendMessage(Format.info("------ Regions ------"));
      if (regions.isEmpty()) {
        src.sendMessage(Format.info("No regions in this world"));
        return CommandResult.success();
      }
      Sponge.getServiceManager().provide(PaginationService.class)
          .orElseThrow(() -> new IllegalStateException("No pagination service found!"))
          .builder()
          .contents(regions.stream()
              .map(host -> Text.of(Format.ACCENT, "> ", Format.note(Format.host(host))))
              .collect(Collectors.toList()))
          .title(Format.info("Regions"))
          .padding(Format.note("="))
          .build()
          .sendTo(src);
      return CommandResult.success();
    });
  }

}
