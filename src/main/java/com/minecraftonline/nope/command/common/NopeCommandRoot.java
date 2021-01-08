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

package com.minecraftonline.nope.command.common;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.command.ReloadCommand;
import com.minecraftonline.nope.command.region.RegionCommand;
import com.minecraftonline.nope.command.setting.SettingCommand;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.host.HostTreeImpl;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.structures.HashQueueVolumeTree;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nonnull;

public class NopeCommandRoot extends CommandNode {

  public NopeCommandRoot() {
    super(null,
        null,
        Text.of("The base command for all commands pertaining to Nope"),
        "nope");
    addCommandElements();
    addChildren(new RegionCommand(this));
    addChildren(new SettingCommand(this));
    addChildren(new ReloadCommand(this));
  }

  @Nonnull
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) {
    src.sendMessage(Text.of(Format.note("==========================")));
    src.sendMessage(Text.of(
        Format.THEME, TextStyles.BOLD, String.format(
            "Nope v%s",
            Nope.getInstance().getPluginContainer().getVersion().orElse("unknown")),
        " ",
        TextStyles.RESET, Format.note("by MinecraftOnline")));
    src.sendMessage(Text.of(
        TextColors.AQUA, "Authors: ",
        Format.note(String.join(
            ", ",
            Nope.getInstance().getPluginContainer().getAuthors()))));
    src.sendMessage(Format.note(
        "Check out the",
        " ",
        Format.url("website", Nope.getInstance().getPluginContainer().getUrl().orElse("unknown")),
        " ",
        "or",
        " ",
        Format.url("source code", Nope.REPO_URL),
        "."));
    src.sendMessage(Format.note(
        "Try the",
        " ",
        Format.command(
            "help",
            "/nope help",
            Text.EMPTY),
        " ",
        "command."));
    if (src instanceof Player) {
      Host worldHost = Nope.getInstance()
          .getHostTree()
          .getWorldHost(((Player) src).getLocation().getExtent().getUniqueId());
      if (worldHost instanceof HostTreeImpl.WorldHost) {
        if (((HostTreeImpl.WorldHost) worldHost).getRegionTree() instanceof HashQueueVolumeTree) {
          src.sendMessage(Text.of("Cache size: ",
              Format.note(((HashQueueVolumeTree<?, ?>) ((HostTreeImpl.WorldHost) worldHost).getRegionTree())
                  .getCacheSize())));
        }
      }
    }
    return CommandResult.success();
  }

}
