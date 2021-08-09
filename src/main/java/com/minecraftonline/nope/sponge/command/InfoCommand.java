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

import com.google.common.collect.Lists;
import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.host.VolumeHost;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.general.CommandNode;
import com.minecraftonline.nope.sponge.command.general.arguments.NopeParameterKeys;
import com.minecraftonline.nope.sponge.command.general.arguments.NopeParameters;
import com.minecraftonline.nope.sponge.util.SpongeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;

/**
 * A command to view detailed information about a host.
 */
public class InfoCommand extends CommandNode {

  InfoCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_INFO,
        "View detailed information about a host",
        "info",
        "i");
    addParameter(NopeParameters.HOST_INFER);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    Host host = context.requireOne(NopeParameterKeys.HOST);

    List<Component> headerLines = Lists.newLinkedList();

    if (host.getWorldKey() != null) {
      Component worldName = Sponge.server()
          .worldManager().world(host.getWorldKey())
          .flatMap(world -> world.properties().displayName())
          .orElseThrow(() -> new RuntimeException("Sponge cannot find world with UUID: "
              + host.getWorldKey()));
      headerLines.add(formatter()
          .keyValue(SpongeFormatter.DULL, "world: ", worldName));
    }

    if (host.getParent() != null) {
      headerLines.add(formatter().keyValue(SpongeFormatter.DULL,
          "parent: ",
          formatter().host(host.getParent())));
    }

    if (host instanceof VolumeHost) {
      VolumeHost volumeHost = (VolumeHost) host;
      // Volume zones only:
      headerLines.add(formatter().keyValue(SpongeFormatter.DULL, "min: ",
          (volumeHost.getMinX() == Integer.MIN_VALUE ? "-Inf" : volumeHost.getMinX())
              + ", "
              + volumeHost.getMinY()
              + ", "
              + (volumeHost.getMinZ() == Integer.MIN_VALUE ? "-Inf" : volumeHost.getMinZ())));

      headerLines.add(formatter().keyValue(SpongeFormatter.DULL, "max: ",
          (volumeHost.getMaxX() == Integer.MAX_VALUE ? "Inf" : volumeHost.getMaxX())
              + ", "
              + volumeHost.getMaxY()
              + ", "
              + (volumeHost.getMaxZ() == Integer.MAX_VALUE ? "Inf" : volumeHost.getMaxZ())));
    }

    int zonePriority = host.getPriority();
    headerLines.add(formatter().keyValue(SpongeFormatter.DULL,
        "priority: ",
        String.valueOf(zonePriority)));
    headerLines.add(Component.text("--------------").color(SpongeFormatter.DULL));  // line separator
    headerLines.add(Component.text()
        .append(Component.text("<< Settings >>  ").color(SpongeFormatter.ACCENT))
        .append(formatter().commandSuggest("NEW", SpongeNope.instance().getCommandTree()
                .findNode(SetCommand.class)
                .orElseThrow(() ->
                    new RuntimeException("SetCommand is not set in Nope command tree!"))
                .getFullCommand()
                + String.format(" -z %s ___ ___", host.getName()),
            formatter().accent("Set a new setting on this host. Use ___ to see all settings",
                "/nope settings")))
        .build());
    Sponge.asyncScheduler().submit(
        Task.builder().execute(() -> {
          List<Component> contents = host.getAll().entries()
              .stream()
              .sorted(Comparator.comparing(setting -> setting.getKey().getId()))
              .flatMap(setting -> {
                try {
                  return formatter().setting(setting,
                      (context.subject() instanceof User)
                          ? ((User) context.subject()).uniqueId()
                          : null, host, SpongeNope.instance()
                          .getHostTreeAdapter()
                          .isRedundant(host, setting.getKey()))
                      .get()
                      .stream()
                      .map(text -> text.clickEvent(ClickEvent.suggestCommand(
                          SpongeNope.instance().getCommandTree()
                              .findNode(UnsetCommand.class)
                              .orElseThrow(() ->
                                  new RuntimeException("UnsetCommand is not "
                                      + "set in Nope command tree!")).getFullCommand()
                              + String.format(" -z %s %s", host.getName(), setting.getKey()))));
                } catch (InterruptedException | ExecutionException e) {
                  e.printStackTrace();
                  return Stream.empty();
                }
              })
              .collect(Collectors.toList());

          Sponge.serviceProvider()
              .paginationService()
              .builder()
              .title(formatter().accent("Host ___", host.getName()))
              .header(headerLines.isEmpty()
                  ? Component.text("None")
                  : Component.join(SpongeFormatter.NEW_LINE, headerLines))
              .padding(Component.text("=").color(SpongeFormatter.ACCENT))
              .contents(contents.isEmpty()
                  ? Collections.singleton(Component.text("None"))
                  : contents)
              .build()
              .sendTo(context.cause().audience());
        }).build());

    // Send the message when we have converted uuids.

    return CommandResult.success();
  }
}
