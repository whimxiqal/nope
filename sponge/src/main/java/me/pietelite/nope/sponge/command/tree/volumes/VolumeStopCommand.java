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

package me.pietelite.nope.sponge.command.tree.volumes;

import me.pietelite.nope.common.Nope;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.common.message.Formatter;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class VolumeStopCommand extends CommandNode {
  public VolumeStopCommand(CommandNode parent) {
    super(parent, null, "Stop a current session editing a zone", "stop");
  }

  @Override
  public CommandResult execute(CommandContext context) {
    if (!(context.cause().root() instanceof ServerPlayer)) {
      return CommandResult.error(Formatter.error("Only players may execute this command"));
    }
    ServerPlayer player = (ServerPlayer) context.cause().root();
    if (!Nope.instance().interactiveVolumeHandler().hasSession(player.uniqueId())) {
      context.sendMessage(Identity.nil(), Formatter.error("You do not have an active zone editor session"));
      return CommandResult.success();
    }
    Nope.instance().interactiveVolumeHandler().finishSession(player.uniqueId());
    context.sendMessage(Identity.nil(), Formatter.success("Stopped your zone editor session"));
    return CommandResult.success();
  }
}
