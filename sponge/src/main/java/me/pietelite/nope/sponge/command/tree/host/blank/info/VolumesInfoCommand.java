/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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

package me.pietelite.nope.sponge.command.tree.host.blank.info;

import java.util.LinkedList;
import java.util.List;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Geometry;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.util.Formatter;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.scheduler.Task;

public class VolumesInfoCommand extends CommandNode {
  public VolumesInfoCommand(CommandNode parent) {
    super(parent, null,
        "Show information about the volumes on a zone",
        "zones");
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    Host host = context.requireOne(ParameterKeys.HOST);
    if (!(host instanceof Scene)) {
      return CommandResult.error(Formatter.error(
          "The host must be a scene"
      ));
    }
    Scene scene = (Scene) host;
    List<Volume> volumes = scene.volumes();
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
                    .append(Component.text(scene.name()).color(Formatter.ACCENT)))
                .padding(Component.text("=").color(Formatter.THEME))
                .contents(contents)
                .sendTo(context.cause().audience()))
            .build());
    return CommandResult.success();
  }
}
