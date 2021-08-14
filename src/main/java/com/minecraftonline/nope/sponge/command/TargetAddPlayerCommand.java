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

package com.minecraftonline.nope.sponge.command;

import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.general.arguments.NopeParameters;
import com.minecraftonline.nope.sponge.command.general.CommandNode;
import com.minecraftonline.nope.sponge.command.general.FlagDescription;
import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.sponge.util.Format;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

class TargetAddPlayerCommand extends CommandNode {
  public TargetAddPlayerCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_EDIT,
        Text.of("Add a user to the whitelist or blacklist"),
        "player");
    addCommandElements(GenericArguments.flags()
            .valueFlag(NopeParameters.host(Text.of("zone")), "z", "-zone")
            .buildWith(GenericArguments.none()),
        NopeParameters.settingKey(Text.of("setting")),
        GenericArguments.string(Text.of("player")));
    addFlagDescription(FlagDescription.ZONE);
    setExecutor((src, args) -> {
      Settee settee = args.<Host>getOne("zone").orElse(NopeCommandRoot.inferHost(src).orElse(null));
      if (settee == null) {
        return CommandResult.empty();
      }
      SettingKey<Object> key = args.requireOne("setting");

      Optional<SettingValue<Object>> value = settee.get(key);
      if (!value.isPresent()) {
        src.sendMessage(Format.error("The setting ",
            Format.settingKey(key, false),
            " is not set on zone ",
            Format.host(settee)));
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
            if (value.get().getTarget().addUser(profile.getUniqueId())) {
              SpongeNope.getInstance().saveState();
              src.sendMessage(Format.success("Added player ",
                  Format.note(profile.getName().orElse("unknown")),
                  " to setting ",
                  Format.settingKey(key, false)));
            } else {
              src.sendMessage(Format.error("The player ",
                  Format.note(profile.getName().orElse("unknown")),
                  " is already targeted on setting ",
                  Format.settingKey(key, false)));
            }
          })
          .submit(SpongeNope.getInstance());
      return CommandResult.success();
    });
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    return null;
  }
}

