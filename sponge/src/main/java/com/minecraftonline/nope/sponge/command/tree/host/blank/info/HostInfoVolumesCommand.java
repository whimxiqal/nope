/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

package com.minecraftonline.nope.sponge.command.tree.host.blank.info;

import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.host.Zone;
import com.minecraftonline.nope.common.math.Geometry;
import com.minecraftonline.nope.common.math.Volume;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.scheduler.Task;

public class HostInfoVolumesCommand extends CommandNode {
  public HostInfoVolumesCommand(CommandNode parent) {
    super(parent, Permissions.INFO,
        "Show information about the volumes on a zone",
        "volumes");
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    Host host = context.requireOne(ParameterKeys.HOST);
    if (!(host instanceof Zone)) {
      return CommandResult.error(Formatter.error(
          "The host must be a zone"
      ));
    }
    Zone zone = (Zone) host;
    List<Volume> volumes = zone.volumes();
    List<Component> contents = new LinkedList<>();
    for (int i = 0; i < volumes.size(); i++) {
      contents.add(Component.text(i + ". ").color(Formatter.DULL)
          .append(Component.text(Geometry.typeOf(volumes.get(i))).color(Formatter.ACCENT)));
    }
    Sponge.asyncScheduler().submit(
        Task.builder()
            .plugin(SpongeNope.instance().pluginContainer())
            .execute(() -> Sponge.serviceProvider().paginationService().builder()
                .title(Component.text("Volumes of ").color(Formatter.GOLD)
                    .append(Component.text(zone.name()).color(Formatter.ACCENT)))
                .padding(Component.text("=").color(Formatter.THEME))
                .contents(contents)
                .sendTo(context.cause().audience()))
            .build());
    return CommandResult.success();
  }
}
