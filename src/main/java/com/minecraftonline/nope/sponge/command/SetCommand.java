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

package com.minecraftonline.nope.sponge.command;

import com.google.common.collect.Sets;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.general.arguments.NopeArguments;
import com.minecraftonline.nope.sponge.command.general.CommandNode;
import com.minecraftonline.nope.sponge.command.general.FlagDescription;
import com.minecraftonline.nope.sponge.command.general.LambdaCommandNode;
import com.minecraftonline.nope.sponge.game.listener.DynamicSettingListeners;
import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SetSettingKey;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingValue;
import com.minecraftonline.nope.sponge.util.Format;
import java.util.Optional;
import java.util.Set;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * A command to set a setting on a host.
 */
public class SetCommand extends LambdaCommandNode {

  SetCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_EDIT,
        Text.of("Set setting on a host"),
        "set");
    addCommandElements(
        GenericArguments.flags()
            .valueFlag(NopeArguments.host(Text.of("zone")), "z", "-zone")
            .flag("a")
            .flag("r")
            .buildWith(GenericArguments.none()),
        NopeArguments.settingKey(Text.of("setting")),
        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("value")))
    );
    addFlagDescription(FlagDescription.ZONE);
    addFlagDescription("a",
        Text.of(TextColors.AQUA, "Append", TextColors.RESET,
            " entries to an existing collection of entries in a setting's value"),
        false);
    addFlagDescription("r",
        Text.of(TextColors.AQUA, "Remove", TextColors.RESET,
            " entries from an existing collection of entries in a setting's value"),
        false);
    setExecutor((src, args) -> {
      SettingKey<?> settingKey = args.requireOne("setting");
      Optional<String> value = args.getOne("value");

      Host host = args.<Host>getOne("zone").orElse(NopeCommandRoot.inferHost(src).orElse(null));
      if (host == null) {
        return CommandResult.empty();
      }

      try {
        if (!host.getName().equals(SpongeNope.getInstance().getHostTreeAdapter().getGlobalHost().getName())
            && settingKey.isGlobal()) {
          src.sendMessage(Format.warn("This setting may only ",
              "work when applied globally"));
          return CommandResult.empty();
        }

        // Trying to set it as empty, as in a set-type setting
        if (!value.isPresent()) {
          if (!(settingKey instanceof SetSettingKey)) {
            src.sendMessage(Format.error("You need to supply a value"));
            return CommandResult.empty();
          }
          host.put((SetSettingKey<?>) settingKey, SettingValue.of(Sets.newHashSet()));
          SpongeNope.getInstance().saveState();
          DynamicSettingListeners.register();
          src.sendMessage(Format.success("Setting ",
              Format.settingKey(settingKey, false),
              " was set to empty on host ",
              Format.host(host)));
          return CommandResult.success();
        }

        boolean append = args.hasAny("a");
        boolean remove = args.hasAny("r");
        if (append || remove) {
          if (!(settingKey instanceof SetSettingKey)) {
            src.sendMessage(Format.error("You may not append or remove values for this setting"));
            return CommandResult.empty();
          }
          SetSettingKey<?> setSettingKey = (SetSettingKey<?>) settingKey;
          if (!host.get(settingKey).isPresent()) {
            src.sendMessage(Format.error("You may not append or remove values "
                + "if there's no data set yet"));
            return CommandResult.empty();
          }
          if (append && remove) {
            src.sendMessage(Format.error("You may not append and remove values at the same time"));
            return CommandResult.empty();
          }
          if (!updateSetSettingValue(host, setSettingKey, value.get(), append)) {
            src.sendMessage(Format.error("Couldn't " + (append ? "append" : "remove")
                + " your values"));
            return CommandResult.empty();
          }
        } else {
          addSetting(host, settingKey, value.get());
        }
      } catch (SettingKey.ParseSettingException e) {
        src.sendMessage(Format.error("Invalid value: ",
            Format.note(e.getMessage())));
        return CommandResult.empty();
      }

      SpongeNope.getInstance().saveState();
      DynamicSettingListeners.register();
      src.sendMessage(Format.success("Set setting ",
          Format.settingKey(settingKey, false),
          " on host ",
          Format.host(host)));

      return CommandResult.success();
    });
  }

  private <T> void addSetting(Host zone,
                              SettingKey<T> key,
                              String s) throws SettingKey.ParseSettingException {
    T data = key.parse(s);
    zone.put(key, SettingValue.of(data));
  }

  private <T> boolean updateSetSettingValue(Host zone,
                                            SetSettingKey<T> key,
                                            String s,
                                            boolean add) throws SettingKey.ParseSettingException {
    Set<T> elements = key.parse(s);
    Optional<SettingValue<Set<T>>> value = zone.get(key);
    if (!value.isPresent()) {
      return false;
    }
    if (add) {
      value.get().getData().addAll(elements);
    } else {
      value.get().getData().removeAll(elements);
    }
    return true;
  }
}
