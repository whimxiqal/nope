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

import com.google.common.collect.Lists;
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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class RegionInfoCommand extends LambdaCommandNode {

  RegionInfoCommand(CommandNode parent) {
    super(parent,
        Permissions.INFO_REGION,
        Text.of("View detailed information about a region"),
        "info",
        "i");

    CommandElement regionElement = GenericArguments.optional(NopeArguments.host(Text.of("region")));
    regionElement = GenericArguments.flags().flag("f", "-friendly").buildWith(regionElement);
    addCommandElements(regionElement);
    setExecutor((src, args) -> {

      Optional<Host> hostOptional = args.getOne("region");
      Host host;
      if (!hostOptional.isPresent()) {
        if (!(src instanceof Player)) {
          src.sendMessage(Format.error("Can't infer region! "
              + "Please specify the target region."));
          return CommandResult.empty();
        }
        Player player = (Player) src;
        List<Host> containing = Nope.getInstance()
            .getHostTree()
            .getContainingHosts(player.getLocation());
        if (containing.isEmpty()) {
          src.sendMessage(Format.error("Can't infer region! "
              + "Please specify the target region."));
          return CommandResult.empty();
        }
        host = containing.stream().max(Comparator.comparing(Host::getPriority)).get();
      } else {
        host = hostOptional.get();
      }

      final boolean friendly = args.<Boolean>getOne(Text.of("f")).orElse(false);

      List<Text> headerLines = Lists.newLinkedList();

      if (host.getWorldUuid() != null) {
        String worldName = Sponge.getServer()
            .getWorld(host.getWorldUuid())
            .map(World::getName)
            .orElseThrow(() -> new RuntimeException("Sponge cannot find world with UUID: "
                + host.getWorldUuid()));
        headerLines.add(Format.keyValue("world: ", worldName));
      }

      if (host.getParent() != null) {
        headerLines.add(Format.keyValue("parent: ", Format.host(host.getParent())));
      }

      if (host instanceof VolumeHost) {
        VolumeHost volumeHost = (VolumeHost) host;
        // Volume regions only:
        headerLines.add(Format.keyValue("min: ", volumeHost.getMinX()
            + ", " + volumeHost.getMinY()
            + ", " + volumeHost.getMinZ()));

        headerLines.add(Format.keyValue("max: ", volumeHost.getMaxX()
            + ", " + volumeHost.getMaxY()
            + ", " + volumeHost.getMaxZ()));
      }

      int regionPriority = host.getPriority();
      headerLines.add(Format.keyValue("priority: ", String.valueOf(regionPriority)));

      headerLines.add(Text.of(Text.NEW_LINE, TextColors.AQUA, "<< Settings >>"));

      List<Text> contents = buildSettingMapMessage(host.getAll(), friendly);

      Runnable sendMsg = () -> Sponge.getServiceManager()
          .provide(PaginationService.class)
          .orElseThrow(() -> new RuntimeException("PaginationService doesn't exist!"))
          .builder()
          .title(Format.info("Region Info: ", Format.note(host.getName())))
          .header(headerLines.isEmpty()
              ? Format.note("None")
              : Text.joinWith(Text.NEW_LINE, headerLines))
          .padding(Text.of(Format.ACCENT, "="))
          .contents(contents.isEmpty()
              ? Collections.singleton(Format.note("None"))
              : contents)
          .build()
          .sendTo(src);

      if (friendly) {
        Sponge.getScheduler().createTaskBuilder()
            .async()
            .execute(sendMsg)
            .submit(Nope.getInstance());
      } else {
        sendMsg.run();
      }

      // Send the message when we have converted uuids.

      return CommandResult.success();
    });
  }

  /**
   * Sends an info message to the command source about the given SettingMap.
   *
   * @param map      Map of settings to build text with
   * @param friendly Whether to convert uuids to usernames. If true this method <b>WILL BLOCK</b>
   * @return Text built text with information about the settings.
   */
  public static List<Text> buildSettingMapMessage(SettingMap map, boolean friendly) {
    return map.entries().stream().map(Format::setting).collect(Collectors.toList());
  }

  /**
   * Gets a gameprofile promise.
   *
   * @param uuid UUID
   * @return CompletableFuture to obtain a gameprofile.
   */
  private static CompletableFuture<GameProfile> getProfile(UUID uuid) {
    return Sponge.getServer().getGameProfileManager().get(uuid);
  }
}
