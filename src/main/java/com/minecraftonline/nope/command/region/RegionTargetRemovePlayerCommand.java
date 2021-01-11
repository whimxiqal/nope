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

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingValue;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

class RegionTargetRemovePlayerCommand extends LambdaCommandNode {
  public RegionTargetRemovePlayerCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_REGION_EDIT,
        Text.of("Remove a user to the whitelist or blacklist"),
        "player");
    addCommandElements(GenericArguments.flags()
            .valueFlag(NopeArguments.host(Text.of("region")), "r", "-region")
            .buildWith(GenericArguments.none()),
        NopeArguments.settingKey(Text.of("setting")),
        GenericArguments.string(Text.of("player")));
    setExecutor((src, args) -> {
      Host host = args.<Host>getOne("region").orElse(RegionCommand.inferHost(src).orElse(null));
      if (host == null) {
        return CommandResult.empty();
      }
      SettingKey<Object> key = args.requireOne("setting");

      Optional<SettingValue<Object>> value = host.get(key);
      if (!value.isPresent()) {
        src.sendMessage(Format.error("The setting ",
            Format.settingKey(key, false),
            " is not set on region ",
            Format.host(host)));
        return CommandResult.empty();
      }

      Sponge.getScheduler().createTaskBuilder()
          .async()
          .execute(() -> {
            GameProfile profile = null;
            try {
              profile = Sponge.getServer().getGameProfileManager()
                  .get(args.<String>requireOne("player"))
                  .get();
            } catch (InterruptedException | ExecutionException e) {
              e.printStackTrace();
            }
            if (profile == null) {
              src.sendMessage(Format.error("That player cannot be found!"));
              return;
            }
            if (value.get().getTarget().hasUser(profile.getUniqueId())) {
              value.get().getTarget().removePlayer(profile.getUniqueId());
              Nope.getInstance().saveState();
              src.sendMessage(Format.success("Removed player ",
                  Format.note(profile.getName().orElse("unknown")),
                  " from setting ",
                  Format.settingKey(key, false)));
            } else {
              src.sendMessage(Format.error("The player ",
                  Format.note(profile.getName().orElse("unknown")),
                  " is not targeted on setting ",
                  Format.settingKey(key, false)));
            }
          })
          .submit(Nope.getInstance());
      return CommandResult.success();
    });
  }
}
