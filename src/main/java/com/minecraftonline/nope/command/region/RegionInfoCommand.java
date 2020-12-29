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

package com.minecraftonline.nope.command.region;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.host.VolumeHost;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.setting.Setting;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingMap;
import com.minecraftonline.nope.setting.SettingValue;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class RegionInfoCommand extends LambdaCommandNode {
  public RegionInfoCommand(CommandNode parent) {
    super(parent,
            Permissions.INFO_REGION,
            Text.of("View detailed information about a region"),
            "info",
            "i");

    CommandElement regionElement = GenericArguments.onlyOne(NopeArguments.host(Text.of("region")));
    regionElement = GenericArguments.flags().flag("f", "-friendly").buildWith(regionElement);
    addCommandElements(regionElement);
    setExecutor((src, args) -> {
      Host host = args.<Host>getOne(Text.of("region")).get();

      boolean friendly = args.<Boolean>getOne(Text.of("f")).orElse(false);

      src.sendMessage(Format.info("-- Info for region " + host.getName() + " --"));

      if (host instanceof VolumeHost) {
        VolumeHost volumeHost = (VolumeHost) host;
        // Non global regions only:
        src.sendMessage(Format.keyValue("min: ", volumeHost.xMin() + ", " + volumeHost.yMin() + ", " + volumeHost.zMin()));
        src.sendMessage(Format.keyValue("max: ", volumeHost.xMax() + ", " + volumeHost.yMax() + ", " + volumeHost.zMax()));
      }

      int regionPriority = host.getPriority();
      src.sendMessage(Format.keyValue("priority: ", String.valueOf(regionPriority)));

      SettingMap map = host.getAll();

      Runnable sendMsg = () -> {
        Text msg = buildMessage(map, friendly);
        src.sendMessage(msg);
      };

      if (friendly) {
        Sponge.getScheduler().createTaskBuilder()
            .async()
            .execute(sendMsg)
            .submit(Nope.getInstance());
      }
      else {
        sendMsg.run();
      }

      // Send the message when we have converted uuids.

      return CommandResult.success();
    });
  }

  /**
   * Sends an info message to the command source about the given SettingMap
   * @param map Map of settings to build text with
   * @param friendly Whether to convert uuids to usernames. If true this method <b>WILL BLOCK</b>
   * @return Text built text with information about the settings.
   */
  public static Text buildMessage(SettingMap map, boolean friendly) {
    Map<UUID, String> uuidUsernameMap = new HashMap<>();

    Text.Builder builder = Text.builder();

    for (Setting<?> entry : map.entries()) {
      SettingKey<?> key = entry.getKey();
      SettingValue<?> value = entry.getValue();

      builder.append(Format.keyValue(key.getId() + ": value: ", key.dataToJson(value.getData()).toString()));

      if (value.getTarget() != null) {
        SettingValue.Target target = value.getTarget();

        builder.append(Text.of("("))
            .append(Format.keyValue("groups: ", String.join(",", target.getGroups())));

        List<String> players;

        if (friendly) {
          // Convert uuids to usernames
          players = new ArrayList<>();
          for (UUID uuid : target.getPlayers()) {
            String username = uuidUsernameMap.get(uuid);
            if (username != null) {
              players.add(username);
            }
            try {
              GameProfile profile = Sponge.getServer().getGameProfileManager().get(uuid).get();
              username = profile.getName().orElse("INVALID_UUID");
            } catch (InterruptedException | ExecutionException e) {
              Nope.getInstance().getLogger().error("Error converting uuids!", e);
            }
            uuidUsernameMap.put(uuid, username);
            players.add(username);
          }
        }
        else {
          players = target.getPlayers().stream()
              .map(UUID::toString)
              .collect(Collectors.toList());
        }
        builder.append(Text.of(" "))
            .append(Format.keyValue("players: ", String.join(",", players) + ")"))
            .append(Text.of(")"));
      }
    }

    return builder.build();
  }

  /**
   * Gets a gameprofile promise
   *
   * @param uuid UUID
   * @return CompletableFuture to obtain a gameprofile.
   */
  private static CompletableFuture<GameProfile> getProfile(UUID uuid) {
    return Sponge.getServer().getGameProfileManager().get(uuid);
  }
}
