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
import com.minecraftonline.nope.arguments.RegionWrapper;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.control.GlobalRegion;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.flags.Flag;
import com.minecraftonline.nope.control.target.Target;
import com.minecraftonline.nope.control.target.TargetSet;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
      RegionWrapper regionWrapper = args.<RegionWrapper>getOne(Text.of("region")).get();
      boolean friendly = args.<Boolean>getOne(Text.of("f")).orElse(false);

      Region region = regionWrapper.getRegion();
      boolean isGlobal = region instanceof GlobalRegion;
      src.sendMessage(Format.info("-- Info for region " + regionWrapper.getRegionName() + " --"));

      if (!isGlobal) {
        // Non global regions only:
        src.sendMessage(Format.regionInfo("min: ", region.getSettingValue(Settings.REGION_MIN).get().toString()));
        src.sendMessage(Format.regionInfo("max: ", region.getSettingValue(Settings.REGION_MAX).get().toString()));
      }

      CompletableFuture<String> ownersFuture = serializeTargetSet(region.getSettingValue(Settings.REGION_OWNERS).orElse(new TargetSet()), friendly);
      CompletableFuture<String> membersFuture = serializeTargetSet(region.getSettingValue(Settings.REGION_MEMBERS).orElse(new TargetSet()), friendly);

      String regionPriority = region.getSettingValue(Settings.REGION_PRIORITY).orElse(0).toString();

      // Flags
      StringBuilder builder = new StringBuilder("{ ");
      for (Map.Entry<Setting<?>, ?> settingEntry : regionWrapper.getRegion().getSettingMap().entrySet()) {
        if (!(settingEntry.getValue() instanceof Flag)) {
          continue; // Only look at flags
        }
        Flag<?> flag = (Flag<?>) settingEntry.getValue();
        Flag<?> defaultValue = (Flag<?>) settingEntry.getKey().getDefaultValue();

        String settingName = settingEntry.getKey().getName();
        builder.append(settingName).append(": ").append(serialize(defaultValue, flag)).append(", ");
        if (flag.getGroup() != Flag.TargetGroup.ALL) {
          builder.append(settingName).append("-group: ").append(flag.getGroup().toString().toLowerCase()).append(", ");
        }
      }
      // Delete last comma
      builder.deleteCharAt(builder.length() - 2).append("}");

      // Send the message when we have converted uuids.

      ownersFuture.whenComplete((owners, t) -> {
        src.sendMessage(Format.regionInfo("owners: ", owners));
        membersFuture.whenComplete((members, e) -> {
          src.sendMessage(Format.regionInfo("members: ", members));
          src.sendMessage(Format.regionInfo("priority: ", regionPriority));
          src.sendMessage(Format.regionInfo("flags: ", builder.toString()));
        });
      });

      return CommandResult.success();
    });
  }

  @SuppressWarnings("unchecked")
  private static <T> String serialize(Flag<T> defaultValue, Flag<?> value) {
    return defaultValue.serialize((Flag<T>)value);
  }

  private static CompletableFuture<String> serializeTargetSet(TargetSet targetSet, boolean convertUUIDs) {
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
  }

  /**
   * Gets a gameprofile promise
   * @param uuid UUID
   * @return CompletableFuture to obtain a gameprofile.
   */
  private static CompletableFuture<GameProfile> getProfile(UUID uuid) {
    return Sponge.getServer().getGameProfileManager().get(uuid);
  }
}
