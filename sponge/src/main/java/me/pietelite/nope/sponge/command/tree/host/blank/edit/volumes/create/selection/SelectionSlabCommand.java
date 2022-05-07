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

package me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create.selection;

import java.util.LinkedList;
import java.util.List;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Geometry;
import me.pietelite.nope.common.math.Slab;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.common.util.ApiUtil;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.EditSceneCommandNode;
import me.pietelite.nope.sponge.tool.SlabSelection;
import me.pietelite.nope.sponge.util.Formatter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class SelectionSlabCommand extends EditSceneCommandNode {

  public SelectionSlabCommand(CommandNode parent) {
    super(parent, Permissions.HOST_EDIT,
        "Create a new slab",
        "slab");
    addParameter(Parameters.SLAB);
  }

  @Override
  public CommandResult execute(CommandContext context, Scene scene) throws CommandException {
    Object cause = context.cause().root();
    if (!(cause instanceof Player)) {
      return CommandResult.error(Formatter.error("Only players may execute this command"));
    }
    Player player = (Player) context.cause().root();
    SlabSelection selection = SpongeNope.instance()
        .selectionHandler()
        .slabDraft(player.uniqueId());
    if (selection == null) {
      return CommandResult.error(Formatter.error(
          "Use the ___ to make a selection first", "slab tool"
      ));
    }
    List<String> errors = new LinkedList<>();
    if (!selection.validate(errors)) {
      errors.forEach(error -> player.sendMessage(Formatter.error(error)));
    }
    Slab volume = selection.build();
    if (volume == null) {
      return CommandResult.error(Formatter.error(
          "Your ___ selection is invalid", "slab"
      ));
    }

    for (int i = 0; i < scene.volumes().size(); i++) {
      if (Geometry.intersects(scene.volumes().get(i), volume)) {
        context.cause().audience().sendMessage(Formatter.warn(
            "Your new ___ intersects with scene ___'s zone number ___ ",
            "slab", scene.name(), i
        ));
      }
    }
    ApiUtil.editNopeScope().editScene(scene.name()).addSlab(volume.domain().name(),
        volume.minY(), volume.maxY());
    context.cause().audience().sendMessage(Formatter.success(
        "A ___ was created on scene ___",
        "slab", scene.name()
    ));
    if (context.cause().root() instanceof ServerPlayer) {
      SpongeNope.instance().particleEffectHandler().show(volume, (ServerPlayer) context.cause().root());
    }
    return CommandResult.success();
  }

}
