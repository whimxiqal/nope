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

package me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create.builder;

import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Cuboid;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.EditSceneCommandNode;
import me.pietelite.nope.common.message.Formatter;
import me.pietelite.nope.sponge.util.SpongeUtil;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class BuilderCuboidCommand extends EditSceneCommandNode {
  public BuilderCuboidCommand(CommandNode parent) {
    super(parent, null, "Begin a new session to edit a box", "box");
  }

  @Override
  public CommandResult execute(CommandContext context, Scene scene) {
    if (!(context.cause().root() instanceof ServerPlayer)) {
      return CommandResult.error(Formatter.error("Only players may execute this command"));
    }
    ServerPlayer player = (ServerPlayer) context.cause().root();
    if (Nope.instance().interactiveVolumeHandler().hasSession(player.uniqueId())) {
      Nope.instance().interactiveVolumeHandler().finishSession(player.uniqueId());
      context.sendMessage(Identity.nil(), Formatter.warn("Quit your previous session"));
    }
    Cuboid cuboid = new Cuboid(SpongeUtil.reduceWorld(player.world()),
        (float) Math.floor(player.serverLocation().x()) - 5,
        (float) Math.floor(player.serverLocation().y()),
        (float) Math.floor(player.serverLocation().z()) - 5,
        (float) Math.floor(player.serverLocation().x()) + 5,
        (float) Math.floor(player.serverLocation().y()) + 5,
        (float) Math.floor(player.serverLocation().z()) + 5);
    Nope.instance().interactiveVolumeHandler().beginSession(player.uniqueId(), scene, cuboid);
    context.sendMessage(Identity.nil(), Formatter.success("You are now editing a ___", "box"));
    return CommandResult.success();
  }
}
