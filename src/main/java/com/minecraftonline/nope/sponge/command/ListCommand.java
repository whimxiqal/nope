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

package com.minecraftonline.nope.sponge.command;

import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.general.CommandNode;
import com.minecraftonline.nope.sponge.command.general.PlayerOnlyCommandNode;
import com.minecraftonline.nope.sponge.util.AdapterUtil;
import java.util.Comparator;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

/**
 * A command to list any hosts currently occupied by a player.
 */
public class ListCommand extends PlayerOnlyCommandNode {

  ListCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_LIST,
        "List any currently occupied zones",
        "list",
        "l");
  }


  @Override
  public CommandResult execute(CommandContext context, Player cause) throws CommandException {
    Sponge.serviceProvider().paginationService()
        .builder()
        .header(formatter().accent("> ___ > ___", "Priority", "Name"))
        .contents(SpongeNope.instance().getHostTreeAdapter()
            .getContainingHosts(AdapterUtil.adaptLocation(cause.serverLocation()))
            .stream()
            .sorted(Comparator.comparing(Host::getPriority))
            .map(host -> formatter().accent("> ___ > ___", host.getPriority(), formatter().host(host)))
            .collect(Collectors.toList()))
        .title(Component.text("Occupied Zones"))
        .padding(formatter().accent("="))
        .build()
        .sendTo(cause);
    return CommandResult.success();
  }
}
