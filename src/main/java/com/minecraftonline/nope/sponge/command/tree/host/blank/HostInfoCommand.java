/*
 *
 * MIT License
 *
 * Copyright (c) 2021 Pieter Svenson
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

package com.minecraftonline.nope.sponge.command.tree.host.blank;

import com.minecraftonline.nope.common.host.Child;
import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.host.Zone;
import com.minecraftonline.nope.common.math.Cuboid;
import com.minecraftonline.nope.common.math.Cylinder;
import com.minecraftonline.nope.common.math.Slab;
import com.minecraftonline.nope.common.math.Sphere;
import com.minecraftonline.nope.common.math.Volume;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.command.settingcollection.blank.InfoCommand;
import com.minecraftonline.nope.sponge.command.settingcollection.blank.info.SettingInfoCommand;
import com.minecraftonline.nope.sponge.command.settingcollection.blank.info.setting.blank.TargetInfoCommand;
import com.minecraftonline.nope.sponge.command.settingcollection.blank.info.setting.blank.ValueInfoCommand;
import com.minecraftonline.nope.sponge.command.settingcollection.blank.info.setting.blank.target.PermissionTargetInfoCommand;
import com.minecraftonline.nope.sponge.command.settingcollection.blank.info.setting.blank.target.PlayerTargetInfoCommand;
import com.minecraftonline.nope.sponge.command.tree.host.blank.info.HostInfoVolumesCommand;
import com.minecraftonline.nope.sponge.util.Formatter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.kyori.adventure.text.Component;

public class HostInfoCommand extends InfoCommand<Host> {
  public HostInfoCommand(CommandNode parent) {
    super(parent, ParameterKeys.HOST, "host",
        Formatter.THEME, Formatter.ACCENT, HostInfoCommand::extras);
    prefix(Parameters.HOST);
    addChild(new HostInfoVolumesCommand(this));

    // Children here instead of in classes
    SettingInfoCommand<Host> settingInfoCommand = new SettingInfoCommand<>(this,
        ParameterKeys.HOST,
        "host",
        Formatter.THEME, Formatter.ACCENT);
    addChild(settingInfoCommand);

    TargetInfoCommand<Host> targetInfoCommand = new TargetInfoCommand<>(settingInfoCommand,
        ParameterKeys.HOST,
        "host",
        Formatter.THEME, Formatter.ACCENT);
    settingInfoCommand.addChild(targetInfoCommand);

    ValueInfoCommand<Host> valueInfoCommand = new ValueInfoCommand<>(settingInfoCommand,
        ParameterKeys.HOST,
        "host",
        Formatter.THEME, Formatter.ACCENT);
    settingInfoCommand.addChild(valueInfoCommand);

    PermissionTargetInfoCommand<Host> permissionTargetInfoCommand = new PermissionTargetInfoCommand<>(targetInfoCommand,
        ParameterKeys.HOST,
        "host",
        Formatter.THEME, Formatter.ACCENT);
    targetInfoCommand.addChild(permissionTargetInfoCommand);

    PlayerTargetInfoCommand<Host> playerTargetInfoCommand = new PlayerTargetInfoCommand<>(targetInfoCommand,
        ParameterKeys.HOST,
        "host",
        Formatter.THEME, Formatter.ACCENT);
    targetInfoCommand.addChild(playerTargetInfoCommand);

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
}
