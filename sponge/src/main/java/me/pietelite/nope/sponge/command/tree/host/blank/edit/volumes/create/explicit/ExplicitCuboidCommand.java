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

package me.pietelite.nope.sponge.command.tree.host.blank.edit.volumes.create.explicit;

import java.util.Optional;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Cuboid;
import me.pietelite.nope.common.math.Geometry;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.common.util.ApiUtil;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.command.tree.host.blank.edit.EditSceneCommandNode;
import me.pietelite.nope.common.message.Formatter;
import me.pietelite.nope.sponge.util.SpongeUtil;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerWorld;

/**
 * Command to create a new {@link Cuboid}.
 */
public class ExplicitCuboidCommand extends EditSceneCommandNode {

  /**
   * Generic constructor.
   *
   * @param parent parent node
   */
  public ExplicitCuboidCommand(CommandNode parent) {
    super(parent, Permissions.HOST_EDIT,
        "Create a new box",
        "box");
    addParameter(Parameters.CUBOID);
  }

  @Override
  public CommandResult execute(CommandContext context, Scene scene) {
    Cuboid volume;
    Optional<ServerWorld> world = context.one(ParameterKeys.WORLD);
    Optional<Double> posX1 = context.one(ParameterKeys.POS_X_1);
    Optional<Double> posY1 = context.one(ParameterKeys.POS_Y_1);
    Optional<Double> posZ1 = context.one(ParameterKeys.POS_Z_1);
    Optional<Double> posX2 = context.one(ParameterKeys.POS_X_2);
    Optional<Double> posY2 = context.one(ParameterKeys.POS_Y_2);
    Optional<Double> posZ2 = context.one(ParameterKeys.POS_Z_2);
    if (!world.isPresent()
        || !posX1.isPresent()
        || !posY1.isPresent()
        || !posZ1.isPresent()
        || !posX2.isPresent()
        || !posY2.isPresent()
        || !posZ2.isPresent()) {
      return CommandResult.error(Formatter.error(
          "You must supply the zone specifications for your ___",
          "box"
      ));
    }

    volume = new Cuboid(Nope.instance().system().domains().get(SpongeUtil.worldToId(world.get())),
        (float) Math.min(posX1.get(), posX2.get()),
        (float) Math.min(posY1.get(), posY2.get()),
        (float) Math.min(posZ1.get(), posZ2.get()),
        (float) Math.max(posX1.get(), posX2.get()) + 1,
        (float) Math.max(posY1.get(), posY2.get()) + 1,
        (float) Math.max(posZ1.get(), posZ2.get()) + 1);
    if (!volume.valid()) {
      return CommandResult.error(Formatter.error(
          "Your designated ___ is invalid", "box"
      ));
    }

    for (int i = 0; i < scene.volumes().size(); i++) {
      if (Geometry.intersects(scene.volumes().get(i), volume)) {
        context.cause().audience().sendMessage(Formatter.warn(
            "Your new box intersects with scene ___'s zone number ___ ",
            scene.name(), i
        ));
      }
    }
    ApiUtil.editNopeScope().editScene(scene.name()).addCuboid(
        volume.domain().name(),
        volume.minX(), volume.minY(), volume.minZ(),
        volume.maxX(), volume.maxY(), volume.maxZ());
    context.cause().audience().sendMessage(Formatter.success(
        "A box was created on scene ___", scene.name()
    ));
    if (context.cause().root() instanceof ServerPlayer) {
      SpongeNope.instance().particleEffectHandler().show(volume, (ServerPlayer) context.cause().root());
    }
    return CommandResult.success();

  }
}
