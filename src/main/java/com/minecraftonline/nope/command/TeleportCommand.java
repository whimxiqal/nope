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

package com.minecraftonline.nope.command;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.host.VolumeHost;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.setting.SettingLibrary;
import com.minecraftonline.nope.setting.SettingValue;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.Random;

class TeleportCommand extends LambdaCommandNode {

  private static final int MAX_TELEPORT_TRIES = 64;

  public TeleportCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_REGION_TELEPORT,
        Text.of("Teleport to a region"),
        "teleport",
        "tp");
    addCommandElements(NopeArguments.host(Text.of("region")));
    setExecutor((src, args) -> {
      if (!(src instanceof Player)) {
        src.sendMessage(Format.error("You must be in game to teleport"));
        return CommandResult.empty();
      }

      Player player = (Player) src;
      Host host = args.requireOne("region");
      Optional<World> world = Optional.ofNullable(host.getWorldUuid())
          .flatMap(uuid -> Sponge.getServer().getWorld(uuid));
      if (!world.isPresent()) {
        src.sendMessage(Format.error("The world of that region's teleport "
            + "location could not be found"));
        return CommandResult.empty();
      }

      Location<World> location;
      if (host.get(SettingLibrary.TELEPORT_LOCATION).isPresent()) {
        SettingValue<Vector3d> value = host.get(SettingLibrary.TELEPORT_LOCATION).get();
        location = new Location<>(world.get(), value.getData());
        if (!host.encompasses(location)) {
          src.sendMessage(Format.error("The stored teleport location for this region "
              + "is not within the region"));
          return CommandResult.empty();
        }
        if (player.setLocationSafely(location)) {
          player.sendMessage(Format.success("Teleported!"));
        } else {
          player.sendMessage(Format.error("The specified teleport location is not safe!"));
        }
        return CommandResult.success();
      }

      if (!(host instanceof VolumeHost)) {
        player.sendMessage(Format.error("That host needs to have a teleport location stored "
            + "before you can teleport there!"));
        return CommandResult.empty();
      }
      VolumeHost volumeHost = (VolumeHost) host;

      Sponge.getScheduler().createTaskBuilder()
          .execute(() -> {
            Random random = new Random();
            for (int i = 0; i < MAX_TELEPORT_TRIES; i++) {
              if (player.setLocationSafely(new Location<>(world.get(),
                  random.nextInt(volumeHost.getMaxX() + 1 - volumeHost.getMinX()) + volumeHost.getMinX(),
                  random.nextInt(volumeHost.getMaxY() + 1 - volumeHost.getMinY()) + volumeHost.getMinY(),
                  random.nextInt(volumeHost.getMaxZ() + 1 - volumeHost.getMinZ()) + volumeHost.getMinZ()))) {
                src.sendMessage(Format.success("Teleported to a random location inside region ",
                    Format.host(host)));
                return;
              }
            }
            src.sendMessage(Format.error("We couldn't find a good spot"));
          })
          .submit(Nope.getInstance());
      return CommandResult.success();
    });

  }

}
