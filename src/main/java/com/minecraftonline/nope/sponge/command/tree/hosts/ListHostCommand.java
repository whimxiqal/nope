/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) 2021 Pieter Svenson
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package com.minecraftonline.nope.sponge.command.tree.hosts;

import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.util.Formatter;
import com.minecraftonline.nope.sponge.util.SpongeUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class ListHostCommand extends CommandNode {
  public ListHostCommand(CommandNode parent) {
    super(parent, Permissions.INFO,
        "List all hosts that you are currently occupy",
        "list");
    addChild(new ListAllHostsCommand(this));
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    if (!(context.cause().root() instanceof Player)) {
      return CommandResult.error(Formatter.error("Only players may execute this command"));
    }
    Player player = (Player) context.cause().root();
    List<Host> hosts = new ArrayList<>(SpongeNope.instance()
        .hostSystem()
        .collectSuperiorHosts(SpongeUtil.reduceLocation(player.serverLocation())));
    hosts.sort(Comparator.comparing(Host::priority));
    Sponge.serviceProvider().paginationService().builder()
        .title(Component.text("Hosts").color(Formatter.GOLD))
        .padding(Component.text("=").color(Formatter.THEME))
        .contents(hosts.stream()
            .map(host -> Component.text()
                .append(Component.text("> ").color(Formatter.DULL))
                .append(Formatter.host(host))
                .build())
            .collect(Collectors.toList()))
        .sendTo(context.cause().audience());
    return CommandResult.success();
  }

}
