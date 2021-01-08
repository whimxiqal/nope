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

package com.minecraftonline.nope.command.region;

import com.flowpowered.math.vector.Vector3d;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.host.VolumeHost;
import com.minecraftonline.nope.permission.Permission;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.Random;

public class RegionShowCommand extends LambdaCommandNode {
  public RegionShowCommand(CommandNode parent) {
    super(parent,
        Permission.of(Permissions.COMMAND_REGION_SHOW.get()),
        Text.of("Graphically display the region in the world"),
        "show",
        "see");
    addCommandElements(GenericArguments.flags()
        .valueFlag(NopeArguments.host(Text.of("region")), "r", "-region")
        .buildWith(GenericArguments.none()));
    setExecutor((src, args) -> {
      if (!(src instanceof Player)) {
        src.sendMessage(Format.error("You must be a player to send this command!"));
        return CommandResult.empty();
      }

      Player player = (Player) src;
      Host host = args.<Host>getOne("region").orElse(RegionCommand.inferHost(src).orElse(null));
      if (host == null) {
        return CommandResult.empty();
      }
      if (!(host instanceof VolumeHost)) {
        src.sendMessage(Format.error("You must pick a volume region!"));
        return CommandResult.empty();
      }
      VolumeHost volumeHost = (VolumeHost) host;
      src.sendMessage(Format.success("Showing nearby borders of region ", Format.host(host)));
      int[][] pos = new int[][]{
          {volumeHost.getMinX(), volumeHost.getMinY(), volumeHost.getMinZ()},
          {volumeHost.getMaxX(), volumeHost.getMaxY(), volumeHost.getMaxZ()}
      };
      int particleCount = 4;
      double portion = 1.0 / particleCount;
      int proximity = 12;
      int proximitySquared = proximity * proximity;
      Random random = new Random();
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 2; j++) {
          for (int a = pos[0][(i + 1) % 3]; a <= pos[1][(i + 1) % 3]; a++) {
            for (int b = pos[0][(i + 2) % 3]; b <= pos[1][(i + 2) % 3]; b++) {
              for (int q = 0; q < particleCount; q++) {
                // normal axis dim value, next lateral axis dim value, next lateral axis dim value
                double[] vals = {pos[j][i] + j, a + portion * q, b + portion * q};
                Vector3d location = new Vector3d(vals[(3 - i) % 3], vals[(4 - i) % 3], vals[(5 - i) % 3]);
                if (location.distanceSquared(player.getLocation().getPosition()) < proximitySquared) {
                  Sponge.getScheduler().createTaskBuilder()
                      .async()
                      .delayTicks(random.nextInt(60))
                      .execute(() ->
                          player.spawnParticles(ParticleEffect.builder().type(ParticleTypes.CLOUD)
                                  .quantity(1)
                                  .build(),
                              location))
                      .submit(Nope.getInstance());
                }
              }
            }
          }
        }
      }
      return CommandResult.success();
    });
  }
}
