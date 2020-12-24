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
import com.minecraftonline.nope.SettingLibrary;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.control.target.TargetSet;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.host.VolumeHost;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

    CommandElement regionElement = GenericArguments.onlyOne(NopeArguments.regionWrapper(Text.of("region")));
    regionElement = GenericArguments.flags().flag("f", "-friendly").buildWith(regionElement);
    addCommandElements(regionElement);
    setExecutor((src, args) -> {
      Host host = args.<Host>getOne(Text.of("region")).get();

      boolean friendly = args.<Boolean>getOne(Text.of("f")).orElse(false);

      src.sendMessage(Format.info("-- Info for region " + host.getName() + " --"));

      if (host instanceof VolumeHost) {
        VolumeHost volumeHost = (VolumeHost)host;
        // Non global regions only:
        src.sendMessage(Format.keyValue("min: ", volumeHost.xMin() + ", " + volumeHost.yMin() + ", " + volumeHost.zMin()));
        src.sendMessage(Format.keyValue("max: ", volumeHost.xMax() + ", " + volumeHost.yMax() + ", " + volumeHost.zMax()));
      }

      // TODO: add these settings and maybe redo these?
      //CompletableFuture<String> ownersFuture = serializeTargetSet(host.get(SettingLibrary.REGION_OWNERS).orElse(new TargetSet()), friendly);
      //CompletableFuture<String> membersFuture = serializeTargetSet(host.get(SettingLibrary.REGION_MEMBERS).orElse(new TargetSet()), friendly);

      int regionPriority = host.getPriority();

      // Flags
//      StringBuilder builder = new StringBuilder("{ ");
//      for (Map.Entry<Setting<?>, ?> settingEntry : regionWrapper.getRegion().getSettingMap().entrySet()) {
//        if (!(settingEntry.getValue() instanceof Flag)) {
//          continue; // Only look at flags
//        }
//        Flag<?> flag = (Flag<?>) settingEntry.getValue();
//        Flag<?> defaultValue = (Flag<?>) settingEntry.getKey().getDefaultValue();
//
//        String settingName = settingEntry.getKey().getName();
//        builder.append(settingName).append(": ").append(serializeFlag(defaultValue, flag)).append(", ");
//        if (flag.getGroup() != Flag.TargetGroup.ALL) {
//          builder.append(settingName).append("-group: ").append(flag.getGroup().toString().toLowerCase()).append(", ");
//        }
//      }
//      // Delete last comma
//      builder.deleteCharAt(builder.length() - 2).append("}");

      String settings = host.getAll().entrySet().stream()
          .map(settingEntry -> {
            SettingLibrary.Setting<?> setting = settingEntry.getKey();
            return setting.getId() + ": " + setting.encodeValue(settingEntry.getValue());
          }).collect(Collectors.joining(", "));

      src.sendMessage(Format.info("Settings: " + settings));

      // Send the message when we have converted uuids.

      /*ownersFuture.whenComplete((owners, t) -> {
        src.sendMessage(Format.keyValue("owners: ", owners));
        membersFuture.whenComplete((members, e) -> {
          src.sendMessage(Format.keyValue("members: ", members));
          src.sendMessage(Format.keyValue("priority: ", regionPriority));
          src.sendMessage(Format.keyValue("flags: ", flagsDescription));
        });
      });*/

      return CommandResult.success();
    });
  }

  /*private static CompletableFuture<String> serializeTargetSet(TargetSet targetSet, boolean convertUUIDs) {
    StringBuilder builder = new StringBuilder("{ ");
    Set<UUID> toConvert = new HashSet<>();

    for (Map.Entry<Target.TargetType, Target> entry : targetSet.getTargets().entries()) {
      switch (entry.getKey()) {
        case PLAYER: {
          builder.append("p:");
          if (convertUUIDs) {
            toConvert.add(UUID.fromString(entry.getValue().serialize()));
          }
          break;
        }
        case GROUP: {
          builder.append("g:");
          break;
        }
        default: {
          throw new IllegalStateException("Missed an enum!");
        }
      }
      builder.append(entry.getValue().serialize()).append(",");
    }
    // Remove trailing comma, add closing bracket.
    builder.deleteCharAt(builder.length() - 1).append(" }");

    return CompletableFuture.supplyAsync(() -> {
      for (UUID uuid : toConvert) {
        try {
          Optional<String> name = getProfile(uuid).get().getName();
          if (name.isPresent()) {
            String uuidStr = uuid.toString();
            int start = builder.indexOf(uuidStr);
            // Replace uuid with name
            builder.replace(start, start + uuidStr.length(), name.get());
          }
          else {
            Nope.getInstance().getLogger().warn("GameProfile for uuid: " + uuid + " had no username!");
          }
        } catch (InterruptedException | ExecutionException e) {
          Nope.getInstance().getLogger().warn("Failed to get GameProfile for uuid: " + uuid);
        }
      }
      return builder.toString();
    });
  }*/

  /**
   * Gets a gameprofile promise
   * @param uuid UUID
   * @return CompletableFuture to obtain a gameprofile.
   */
  private static CompletableFuture<GameProfile> getProfile(UUID uuid) {
    return Sponge.getServer().getGameProfileManager().get(uuid);
  }
}
