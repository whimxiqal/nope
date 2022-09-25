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

package me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes;

import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.ZoneType;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.EditSceneCommandNode;
import me.pietelite.nope.common.message.Formatter;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class VolumeEditCommand extends EditSceneCommandNode {
  public VolumeEditCommand(CommandNode parent) {
    super(parent, null, "Edit the dimensions of a zone for a scene", "edit");
    addParameter(Parameters.VOLUME_INDEX);
  }

  @Override
  public CommandResult execute(CommandContext context, Scene scene) {
    Object cause = context.cause().root();
    if (!(cause instanceof ServerPlayer)) {
      return CommandResult.error(Formatter.error("Only players may execute this command"));
    }
    ServerPlayer player = (ServerPlayer) context.cause().root();
    int index = context.requireOne(ParameterKeys.VOLUME_INDEX);
    if (index < 0 || index >= scene.volumes().size()) {
      return CommandResult.error(Formatter.error("Your index ___ is out of bounds", index));
    }
    if (Nope.instance().interactiveVolumeHandler().hasSession(player.uniqueId())) {
      context.sendMessage(Identity.nil(), Formatter.error("You are already editing a zone"));
      return CommandResult.success();
    }
    Volume volume = scene.volumes().get(index);
    if (volume.zoneType() != ZoneType.CUBOID) {
      context.sendMessage(Identity.nil(),
          Formatter.error("Currently, editing anything other than boxes is not supported"));
      return CommandResult.success();
    }
    Nope.instance().interactiveVolumeHandler().beginSession(player.uniqueId(), scene,
        scene.volumes().get(index).copy());
    context.sendMessage(Identity.nil(),
        Formatter.success("You are editing the zone of ___ at index ___", scene.name(), index));
    return CommandResult.success();
  }
}
