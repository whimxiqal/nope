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

package me.pietelite.nope.sponge.command.tree.host.blank;

import me.pietelite.nope.common.host.Child;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.host.Zone;
import me.pietelite.nope.common.math.Cuboid;
import me.pietelite.nope.common.math.Cylinder;
import me.pietelite.nope.common.math.Slab;
import me.pietelite.nope.common.math.Sphere;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.command.tree.host.blank.info.HostInfoVolumesCommand;
import me.pietelite.nope.sponge.command.tree.host.blank.info.SettingInfoCommand;
import me.pietelite.nope.sponge.util.Formatter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextDecoration;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.scheduler.Task;

public class HostInfoCommand extends CommandNode {
  public HostInfoCommand(CommandNode parent) {
    super(parent, Permissions.INFO, "Get info about a host", "info");
    prefix(Parameters.HOST);
    addChild(new HostInfoVolumesCommand(this));
    addChild(new SettingInfoCommand(this));
  }

  private static Collection<Component> extras(Host host) {
    List<Component> extras = new LinkedList<>();
    if (host instanceof Child<?>) {
      ((Child<? extends Host>) host).parent()
          .ifPresent(value -> extras.add(Component.text()
              .append(Formatter.hover("Parent: ",
                  "Parent\nThe parent from which this host derives"))
              .append(Formatter.host(value))
              .build()));
    }
    extras.add(Component.text()
        .append(Formatter.hover("Priority: ",
            "Priority\nThe priority number which gives precedence to certain hosts. "
                + "A higher priority means a higher precedence for conflicting targets and data."))
        .append(Formatter.accent(String.valueOf(host.priority())))
        .build());
    if (host instanceof Zone) {
      Collection<Volume> volumes = ((Zone) host).volumes();
      List<String> volumeDescription = new LinkedList<>();
      long cuboids = volumes.stream().filter(volume -> volume instanceof Cuboid).count();
      if (cuboids != 0) {
        volumeDescription.add("Box " + cuboids + "x");
      }
      long cylinders = volumes.stream().filter(volume -> volume instanceof Cylinder).count();
      if (cylinders != 0) {
        volumeDescription.add("Cylinder " + cylinders + "x");
      }
      long spheres = volumes.stream().filter(volume -> volume instanceof Sphere).count();
      if (spheres != 0) {
        volumeDescription.add("Sphere " + spheres + "x");
      }
      long slabs = volumes.stream().filter(volume -> volume instanceof Slab).count();
      if (slabs != 0) {
        volumeDescription.add("Slab " + slabs + "x");
      }
      extras.add(Component.text()
          .append(Formatter.hover("Volumes",
              "Volumes\nThe individual volumes that determine where the zone is applied throughout a world"))
          .append(Formatter.accent(": "))
          .append(Formatter.command(String.join(", ", volumeDescription),
              "nope host " + host.name() + " list volumes",
              Formatter.accent("Show all the different volumes saved on zone ___", host.name()),
              false, false))
          .build());
    }
    return extras;
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    Host host = context.requireOne(Parameters.HOST);

    Component header = Component.join(JoinConfiguration.separator(Component.newline()),
            HostInfoCommand.extras(host))
        .append(Component.newline())
        .append(Component.newline())
        .append(Component.text()
            .append(Component.text("Settings")
                .color(Formatter.DULL)
                .decorate(TextDecoration.UNDERLINED))
            .build());
    Sponge.asyncScheduler().submit(Task.builder().execute(() ->
            Formatter.paginator(host.name())
                .header(header)
                .contents(host.settings().isEmpty()
                    ? Collections.singleton(Component.text("None").color(Formatter.DULL))
                    : host.settings().stream()
                    .flatMap(setting -> {
                      try {
                        return Formatter.setting(setting, context.cause().subject(), host)
                            .get()
                            .stream();
                      } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return Stream.empty();
                      }
                    }).collect(Collectors.toList()))
                .sendTo(context.cause().audience()))
        .plugin(SpongeNope.instance().pluginContainer())
        .build());

    return CommandResult.success();
  }
}
