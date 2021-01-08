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

import com.google.common.collect.Maps;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingValue;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.Optional;

/**
 * The permission UNAFFECTED in {@link com.minecraftonline.nope.permission.Permissions}
 * is given to high ranking players who should not be affected by Nope.
 * A setting can bypass this passthrough feature by setting "force affect"
 * on a Target to simulate the motion that the affecting of a player is being forced.
 */
public class RegionTargetForceCommand extends LambdaCommandNode {
  public RegionTargetForceCommand(CommandNode parent) {
    super(parent,
        Permissions.UNAFFECTED,
        Text.of("Toggle whether the "
            + Permissions.UNAFFECTED.get()
            + " permission is respected on this setting"),
        "force");
    addCommandElements(GenericArguments.flags()
            .valueFlag(NopeArguments.host(Text.of("region")), "r", "-region")
            .buildWith(GenericArguments.none()),
        NopeArguments.settingKey(Text.of("setting")));
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

      value.get().getTarget().setForceAffect(!value.get().getTarget().isForceAffect());

      if (value.get().getTarget().isForceAffect()) {
        src.sendMessage(Format.success("The setting ",
            Format.settingKey(key, false),
            " now bypasses the ",
            Format.note(Permissions.UNAFFECTED.get()),
            " permission"));
      } else {
        src.sendMessage(Format.success("The setting ",
            Format.settingKey(key, false),
            " now does not bypass the ",
            Format.note(Permissions.UNAFFECTED.get()),
            " permission"));
      }
      Nope.getInstance().saveState();
      return CommandResult.success();
    });
  }
}
